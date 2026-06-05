package com.example.p2p.presentation.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.p2p.data.remote.model.Notification
import com.example.p2p.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    viewModel: NotificationsViewModel,
    onBack: () -> Unit = {},
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Notificaciones", fontWeight = FontWeight.Bold, color = TextMain)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = TextMain)
                    }
                },
                actions = {
                    if (state.notifications.isNotEmpty()) {
                        TextButton(onClick = { viewModel.loadAndMarkRead() }) {
                            Text(
                                text = "Actualizar",
                                color = Primary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceColor),
            )
        },
        containerColor = BackgroundApp,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Primary,
                    )
                }
                state.error != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = DangerColor,
                            modifier = Modifier.size(48.dp)
                        )
                        Text("Error al cargar notificaciones", color = TextMuted, fontSize = 14.sp)
                        TextButton(onClick = { viewModel.loadAndMarkRead() }) {
                            Text("Reintentar", color = Primary)
                        }
                    }
                }
                state.notifications.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.NotificationsNone,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(56.dp)
                        )
                        Text("Sin notificaciones", color = TextMuted, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                        Text("Aquí aparecerán los eventos de tu cuenta.", color = TextMuted, fontSize = 12.sp)
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 24.dp),
                    ) {
                        items(state.notifications, key = { it.id }) { notif ->
                            NotificationRow(
                                item = notif,
                                onDelete = { viewModel.deleteNotification(notif.id) },
                            )
                            HorizontalDivider(
                                color = BorderColor,
                                modifier = Modifier.padding(horizontal = 16.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Notification Row ─────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationRow(
    item: Notification,
    onDelete: () -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DangerColor.copy(alpha = 0.85f))
                    .padding(end = 20.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.White)
            }
        },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (!item.is_read) Primary.copy(alpha = 0.04f) else SurfaceColor)
                .padding(16.dp),
            verticalAlignment = Alignment.Top,
        ) {
            val (icon, color) = notifIconAndColor(item.type)
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = item.title,
                        fontWeight = if (!item.is_read) FontWeight.Bold else FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = TextMain,
                        modifier = Modifier.weight(1f),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = formatRelativeTime(item.created_at),
                        fontSize = 11.sp,
                        color = TextMuted,
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(text = item.body, fontSize = 13.sp, color = TextMuted, lineHeight = 18.sp)
            }

            if (!item.is_read) {
                Spacer(Modifier.width(10.dp))
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Primary)
                        .align(Alignment.CenterVertically),
                )
            }
        }
    }
}

// ── Helpers ──────────────────────────────────────────────────────────────────

private fun notifIconAndColor(type: String): Pair<ImageVector, Color> = when (type) {
    "login"       -> Icons.Default.CheckCircle to SuccessColor
    "transaction" -> Icons.Default.SwapHoriz   to Primary
    "voucher"     -> Icons.Default.Receipt      to WarningColor
    "dispute"     -> Icons.Default.Gavel        to WarningColor
    "offer"       -> Icons.Default.Campaign     to PrimaryLight
    "admin"       -> Icons.Default.AdminPanelSettings to DangerColor
    "security"    -> Icons.Default.Lock         to DangerColor
    else          -> Icons.Default.Notifications to TextMuted
}

private fun formatRelativeTime(isoDate: String): String {
    return try {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault())
        sdf.timeZone = java.util.TimeZone.getTimeZone("UTC")
        val date = sdf.parse(isoDate) ?: return isoDate
        val diffMs = System.currentTimeMillis() - date.time
        val diffMin  = diffMs / 60_000
        val diffHour = diffMin / 60
        val diffDay  = diffHour / 24
        when {
            diffMin < 1    -> "ahora"
            diffMin < 60   -> "hace ${diffMin}m"
            diffHour < 24  -> "hace ${diffHour}h"
            diffDay == 1L  -> "ayer"
            diffDay < 7    -> "hace ${diffDay}d"
            else           -> "${diffDay}d"
        }
    } catch (_: Exception) {
        isoDate.take(10)
    }
}
