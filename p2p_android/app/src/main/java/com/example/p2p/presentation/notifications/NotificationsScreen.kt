package com.example.p2p.presentation.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.p2p.ui.theme.BackgroundApp
import com.example.p2p.ui.theme.BorderColor
import com.example.p2p.ui.theme.DangerColor
import com.example.p2p.ui.theme.Primary
import com.example.p2p.ui.theme.SuccessColor
import com.example.p2p.ui.theme.SurfaceColor
import com.example.p2p.ui.theme.TextMain
import com.example.p2p.ui.theme.TextMuted
import com.example.p2p.ui.theme.WarningColor

// ---------------------------------------------------------------------------
// Data model
// ---------------------------------------------------------------------------

private data class NotificationItem(
    val title: String,
    val description: String,
    val time: String,
    val icon: ImageVector,
    val iconBgColor: Color,
    val isUnread: Boolean = true,
)

private val sampleNotifications = listOf(
    NotificationItem(
        title = "Inicio de sesión exitoso",
        description = "Se detectó un nuevo inicio de sesión desde Lima, Perú.",
        time = "hace 10m",
        icon = Icons.Filled.CheckCircle,
        iconBgColor = SuccessColor,
        isUnread = true,
    ),
    NotificationItem(
        title = "Disputa #TX-8811 Actualizada",
        description = "El administrador marcó el caso en estado Revisión.",
        time = "hace 2h",
        icon = Icons.Filled.Gavel,
        iconBgColor = WarningColor,
        isUnread = true,
    ),
    NotificationItem(
        title = "Nueva Oferta de Mercado",
        description = "Victor Vendedor publicó una nueva oferta USD→PEN a S/3.780.",
        time = "ayer",
        icon = Icons.Filled.SwapHoriz,
        iconBgColor = Primary,
        isUnread = false,
    ),
    NotificationItem(
        title = "Alerta de Seguridad",
        description = "Se solicitó restablecimiento de contraseña desde tu cuenta.",
        time = "hace 3d",
        icon = Icons.Filled.Lock,
        iconBgColor = DangerColor,
        isUnread = false,
    ),
)

// ---------------------------------------------------------------------------
// Screen
// ---------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Notificaciones",
                        fontWeight = FontWeight.Bold,
                        color = TextMain,
                    )
                },
                actions = {
                    TextButton(onClick = {}) {
                        Text(
                            text = "Marcar todo leído",
                            color = Primary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceColor),
            )
        },
        containerColor = BackgroundApp,
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 24.dp),
        ) {
            items(sampleNotifications.size) { index ->
                NotificationRow(item = sampleNotifications[index])
                HorizontalDivider(
                    color = BorderColor,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Notification Row
// ---------------------------------------------------------------------------

@Composable
private fun NotificationRow(item: NotificationItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (item.isUnread) Primary.copy(alpha = 0.04f) else SurfaceColor)
            .padding(16.dp),
        verticalAlignment = Alignment.Top,
    ) {
        // Colored icon circle
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(item.iconBgColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = item.iconBgColor,
                modifier = Modifier.size(22.dp),
            )
        }

        Spacer(Modifier.width(14.dp))

        // Text content
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = item.title,
                    fontWeight = if (item.isUnread) FontWeight.Bold else FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = TextMain,
                    modifier = Modifier.weight(1f),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = item.time,
                    fontSize = 11.sp,
                    color = TextMuted,
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = item.description,
                fontSize = 13.sp,
                color = TextMuted,
                lineHeight = 18.sp,
            )
        }

        // Unread dot
        if (item.isUnread) {
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

// ---------------------------------------------------------------------------
// Preview
// ---------------------------------------------------------------------------

@Preview(showBackground = true)
@Composable
fun NotificationsScreenPreview() {
    NotificationsScreen()
}
