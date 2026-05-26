package com.example.p2p.presentation.market

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.p2p.ui.theme.*

// ─── Data ───────────────────────────────────────────────────────────────────

private data class Offer(
    val initials: String,
    val seller: String,
    val pair: String,
    val rate: String,
    val min: String,
    val max: String,
    val amount: String,
    val bank: String,
    val bankColor: Color
)

private val sampleOffers = listOf(
    Offer("VV", "Victor V.", "Vende USD → PEN", "S/ 3.780", "Min: \$50", "Max: \$500", "Monto: \$1,200", "BCP", BcpColor),
    Offer("AM", "Ana M.",   "Vende EUR → PEN", "S/ 4.110", "Min: €30", "Max: €400", "Monto: €800",   "Interbank", InterbankColor),
    Offer("LR", "Luis R.",  "Vende USD → PEN", "S/ 3.775", "Min: \$100","Max: \$1,000","Monto: \$3,000","BBVA", BbvaColor),
    Offer("MK", "María K.", "Vende PEN → USD", "S/ 0.268", "Min: S/50","Max: S/500", "Monto: S/2,000","Yape", YapeColor)
)

// ─── Screen ──────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketScreen(
    onNavigateToNotifications: () -> Unit = {}
) {
    Scaffold(
        containerColor = BackgroundApp,
        topBar = { MarketTopBar(onNavigateToNotifications = onNavigateToNotifications) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item { WelcomeCard() }
            item { FilterCard() }
            item { MatchingRow() }
            item { OffersHeader(count = sampleOffers.size) }
            items(sampleOffers) { offer -> OfferCard(offer) }
        }
    }
}

// ─── TopBar ──────────────────────────────────────────────────────────────────

@Composable
private fun MarketTopBar(onNavigateToNotifications: () -> Unit = {}) {
    Surface(
        color = Primary,
        shadowElevation = 4.dp
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Peru",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                )
                Text(
                    "Exchange",
                    color = PrimaryMint,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                )
                Spacer(Modifier.weight(1f))
                IconButton(onClick = onNavigateToNotifications, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = "Notificaciones",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            // Ticker row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Primary.copy(alpha = 0.85f))
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TickerItem("USD", "S/3.78", up = true)
                TickerItem("EUR", "S/4.11", up = true)
                TickerItem("BRL", "S/0.74", up = false)
            }
        }
    }
}

@Composable
private fun TickerItem(currency: String, rate: String, up: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(currency, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
        Text(rate, color = Color.White.copy(alpha = 0.9f), fontSize = 11.sp)
        Text(
            if (up) "▲" else "▼",
            color = if (up) PrimaryMint else DangerColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// ─── Welcome Card ────────────────────────────────────────────────────────────

@Composable
private fun WelcomeCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.horizontalGradient(listOf(Primary, PrimaryLight)))
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                // Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(PrimaryMint.copy(alpha = 0.2f))
                        .border(1.dp, PrimaryMint.copy(alpha = 0.5f), RoundedCornerShape(50.dp))
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text("⭐ Experto", color = PrimaryMint, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
                Text("Bienvenido, Carlos", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("P2P Seguro · Lima, Perú", color = Color.White.copy(alpha = 0.75f), fontSize = 12.sp)
            }
            // "PE" Badge
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.15f))
                    .border(2.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("PE", color = Color.White, fontWeight = FontWeight.Black, fontSize = 22.sp)
            }
        }
    }
}

// ─── Filter Card ─────────────────────────────────────────────────────────────

@Composable
private fun FilterCard() {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceColor),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.Default.FilterList, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                Text("Filtrado Multidivisa", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextMain)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CurrencyDropdown("Tengo", "PEN", Modifier.weight(1f))
                CurrencyDropdown("Quiero", "USD", Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun CurrencyDropdown(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.Medium)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .border(1.dp, BorderColor, RoundedCornerShape(10.dp))
                .background(BackgroundApp)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextMain)
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp))
        }
    }
}

// ─── Matching Row ─────────────────────────────────────────────────────────────

@Composable
private fun MatchingRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            onClick = {},
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = WarningColor),
            border = androidx.compose.foundation.BorderStroke(1.dp, WarningColor),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(4.dp))
            Text("Matching Automático", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.weight(1f))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Icon(Icons.Default.Sort, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
            Text("Mejor precio", fontSize = 12.sp, color = TextMuted)
        }
    }
}

// ─── Offers Header ────────────────────────────────────────────────────────────

@Composable
private fun OffersHeader(count: Int) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Ofertas disponibles", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextMain)
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .background(Primary)
                .padding(horizontal = 8.dp, vertical = 2.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("$count", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// ─── Offer Card ───────────────────────────────────────────────────────────────

@Composable
private fun OfferCard(offer: Offer) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Row 1: Avatar + name + rating
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(offer.initials, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(offer.seller, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextMain)
                    Text("★ 4.9 · 180 ops", fontSize = 11.sp, color = WarningColor)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(SuccessColor.copy(alpha = 0.1f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text("En línea", color = SuccessColor, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            HorizontalDivider(color = BorderColor, thickness = 0.5.dp)
            // Row 2: Pair + rate
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(offer.pair, fontSize = 13.sp, color = TextMuted, fontWeight = FontWeight.Medium)
                Text(offer.rate, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Primary)
            }
            // Row 3: min / max / amount
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                InfoChip(offer.min)
                InfoChip(offer.max)
                InfoChip(offer.amount)
            }
            // Row 4: bank + buy button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(offer.bankColor.copy(alpha = 0.12f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(offer.bank, color = offer.bankColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = {},
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text("Comprar", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun InfoChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(BackgroundApp)
            .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text, fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Medium)
    }
}
