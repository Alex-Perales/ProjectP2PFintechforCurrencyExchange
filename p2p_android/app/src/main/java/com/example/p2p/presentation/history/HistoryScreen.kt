package com.example.p2p.presentation.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.p2p.ui.theme.*

// ─── Data ────────────────────────────────────────────────────────────────────

private data class Transaction(
    val id: String,
    val status: String,
    val statusColor: Color,
    val from: String,
    val to: String,
    val amount: String,
    val rate: String,
    val date: String,
    val icon: ImageVector
)

private val sampleTransactions = listOf(
    Transaction("#TX-9982", "Completado", SuccessColor, "Carlos", "Victor",  "$ 200.00 USD", "S/ 3.780", "25 May 2026", Icons.Default.SwapHoriz),
    Transaction("#TX-9881", "Completado", SuccessColor, "Carlos", "Ana",     "€ 150.00 EUR", "S/ 4.110", "24 May 2026", Icons.Default.SwapHoriz),
    Transaction("#TX-9756", "Pendiente",  WarningColor, "Carlos", "Luis",    "$ 500.00 USD", "S/ 3.775", "23 May 2026", Icons.Default.Schedule),
    Transaction("#TX-9654", "Disputa",    DangerColor,  "Carlos", "María",   "$ 100.00 USD", "S/ 3.780", "20 May 2026", Icons.Default.Gavel),
    Transaction("#TX-9521", "Completado", SuccessColor, "Carlos", "Víctor",  "$ 300.00 USD", "S/ 3.780", "18 May 2026", Icons.Default.SwapHoriz)
)

private val filterChips = listOf("Todos", "Completados", "Pendientes", "Disputas")

// ─── Screen ──────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel? = null,
    onBack: () -> Unit = {}
) {
    val uiState by viewModel?.uiState?.collectAsState(initial = HistoryUiState()) ?: remember { mutableStateOf(HistoryUiState()) }
    var selectedFilter by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    val transactions = uiState.transactions.map { dto ->
        val statusName = when (dto.status) {
            "completed" -> "Completado"
            "pending" -> "Pendiente"
            "voucher_uploaded" -> "En Proceso"
            "cancelled" -> "Cancelado"
            "disputed" -> "Disputa"
            else -> dto.status
        }
        val sColor = when (dto.status) {
            "completed" -> SuccessColor
            "pending", "voucher_uploaded" -> WarningColor
            "cancelled", "disputed" -> DangerColor
            else -> TextMuted
        }
        val icon = when (dto.status) {
            "completed" -> Icons.Default.SwapHoriz
            "pending", "voucher_uploaded" -> Icons.Default.Schedule
            "cancelled" -> Icons.Default.Cancel
            "disputed" -> Icons.Default.Gavel
            else -> Icons.Default.Info
        }
        val formattedDate = try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val date = parser.parse(dto.created_at.substringBefore("."))
            if (date != null) formatter.format(date) else dto.created_at.take(10)
        } catch (e: Exception) {
            dto.created_at.take(10)
        }

        Transaction(
            id = "#TX-${dto.id.takeLast(4).uppercase()}",
            status = statusName,
            statusColor = sColor,
            from = dto.buyer_name?.split(" ")?.firstOrNull() ?: dto.buyer_id.take(6).uppercase(),
            to = dto.vendor_name?.split(" ")?.firstOrNull() ?: dto.vendor_id.take(6).uppercase(),
            amount = "${String.format("%.2f", dto.amount_from)} USD",
            rate = "S/ ${String.format("%.3f", dto.exchange_rate)}",
            date = formattedDate,
            icon = icon
        )
    }

    val filteredList = transactions
        .filter {
            when (selectedFilter) {
                1 -> it.status == "Completado"
                2 -> it.status == "Pendiente" || it.status == "En Proceso"
                3 -> it.status == "Disputa"
                else -> true
            }
        }
        .filter { tx ->
            if (searchQuery.isBlank()) true
            else tx.id.contains(searchQuery, ignoreCase = true) ||
                tx.from.contains(searchQuery, ignoreCase = true) ||
                tx.to.contains(searchQuery, ignoreCase = true)
        }

    Scaffold(
        containerColor = BackgroundApp,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Historial de Operaciones",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filtros", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Primary)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // ── Search field ──────────────────────────────────────────────────
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar por ID o vendedor...", fontSize = 13.sp, color = TextMuted) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted, modifier = Modifier.size(20.dp))
                    },
                    trailingIcon = if (searchQuery.isNotEmpty()) {
                        { IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp))
                        }}
                    } else null,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = BorderColor,
                        unfocusedContainerColor = SurfaceColor,
                        focusedContainerColor = SurfaceColor
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontSize = 13.sp)
                )
            }

            // ── Filter chips ──────────────────────────────────────────────────
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                items(filterChips.indices.toList()) { index ->
                    val isSelected = index == selectedFilter
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilter = index },
                        label = {
                            Text(
                                filterChips[index],
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Primary,
                            selectedLabelColor = Color.White,
                            containerColor = SurfaceColor,
                            labelColor = TextMuted
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            selectedBorderColor = Primary,
                            borderColor = BorderColor
                        )
                    )
                }
            }

            // ── Summary row ───────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SummaryChip("${transactions.size} Total", Primary)
                SummaryChip("${transactions.count { it.status == "Completado" }} Completados", SuccessColor)
                SummaryChip("${transactions.count { it.status == "Pendiente" || it.status == "En Proceso" }} Pendientes", WarningColor)
                SummaryChip("${transactions.count { it.status == "Disputa" }} Disputas", DangerColor)
            }

            // ── Transaction list ──────────────────────────────────────────────
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filteredList) { tx ->
                    TransactionCard(tx)
                }
            }
        }
    }
}

// ─── Summary Chip ─────────────────────────────────────────────────────────────

@Composable
private fun SummaryChip(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.1f))
            .border(1.dp, color.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text, color = color, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
    }
}

// ─── Transaction Card ─────────────────────────────────────────────────────────

@Composable
private fun TransactionCard(tx: Transaction) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Row 1: icon + ID + status badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(tx.statusColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        tx.icon,
                        contentDescription = null,
                        tint = tx.statusColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(tx.id, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextMain, modifier = Modifier.weight(1f))
                StatusBadge(tx.status, tx.statusColor)
            }

            HorizontalDivider(color = BorderColor, thickness = 0.5.dp)

            // Row 2: parties + amount
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    PartyAvatar(tx.from.take(2).uppercase(), Primary)
                    Icon(Icons.Default.ArrowForward, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                    PartyAvatar(tx.to.take(2).uppercase(), PrimaryLight)
                    Text("${tx.from} → ${tx.to}", fontSize = 12.sp, color = TextMuted)
                }
                Text(tx.amount, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextMain)
            }

            // Row 3: rate + date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.TrendingUp, contentDescription = null, tint = Primary, modifier = Modifier.size(14.dp))
                    Text("Tipo cambio: ${tx.rate}", fontSize = 11.sp, color = Primary, fontWeight = FontWeight.Medium)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, tint = TextMuted, modifier = Modifier.size(12.dp))
                    Text(tx.date, fontSize = 11.sp, color = TextMuted)
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(50.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(status, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun PartyAvatar(initials: String, color: Color) {
    Box(
        modifier = Modifier
            .size(22.dp)
            .clip(CircleShape)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(initials, color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
    }
}
