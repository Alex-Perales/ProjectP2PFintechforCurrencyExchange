package com.example.p2p.presentation.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.p2p.ui.theme.BackgroundApp
import com.example.p2p.ui.theme.BorderColor
import com.example.p2p.ui.theme.DangerColor
import com.example.p2p.ui.theme.Primary
import com.example.p2p.ui.theme.PrimaryMint
import com.example.p2p.ui.theme.SuccessColor
import com.example.p2p.ui.theme.SurfaceColor
import com.example.p2p.ui.theme.TextMain
import com.example.p2p.ui.theme.TextMuted
import com.example.p2p.ui.theme.WarningColor

// ---------------------------------------------------------------------------
// Data model
// ---------------------------------------------------------------------------

private data class DisputeItem(
    val txId: String,
    val status: String,
    val statusColor: Color,
    val buyer: String,
    val seller: String,
    val amount: String,
    val date: String,
)

private val sampleDisputes = listOf(
    DisputeItem(
        txId = "#TX-9982",
        status = "EN ARBITRAJE",
        statusColor = DangerColor,
        buyer = "Carlos Mendoza",
        seller = "Victor V.",
        amount = "\$200 USD",
        date = "25 May 2026",
    ),
    DisputeItem(
        txId = "#TX-9756",
        status = "EN REVISIÓN",
        statusColor = WarningColor,
        buyer = "Luis R.",
        seller = "Ana M.",
        amount = "\$500 USD",
        date = "23 May 2026",
    ),
)

// ---------------------------------------------------------------------------
// Screen
// ---------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Todas", "Arbitraje", "Revisión", "Resuelta")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Panel Administrador",
                        fontWeight = FontWeight.Bold,
                        color = TextMain,
                    )
                },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Filled.Security,
                        contentDescription = "Admin",
                        tint = Primary,
                        modifier = Modifier.padding(start = 16.dp),
                    )
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
            // Admin header card
            item {
                AdminHeaderCard(modifier = Modifier.padding(16.dp))
            }

            // Status pills
            item {
                StatusPillsRow(modifier = Modifier.padding(horizontal = 16.dp))
            }

            // Filter tabs
            item {
                Spacer(Modifier.height(16.dp))
                FilterTabsRow(
                    tabs = tabs,
                    selectedIndex = selectedTab,
                    onTabSelected = { selectedTab = it },
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }

            // Section title
            item {
                Spacer(Modifier.height(20.dp))
                Text(
                    text = "Disputas Activas",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextMain,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
                Spacer(Modifier.height(12.dp))
            }

            // Dispute cards
            items(sampleDisputes.size) { index ->
                DisputeCard(
                    dispute = sampleDisputes[index],
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Admin Header Card
// ---------------------------------------------------------------------------

@Composable
private fun AdminHeaderCard(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(Color(0xFF1A2332), Color(0xFF0F172A)),
                ),
            )
            .padding(20.dp),
    ) {
        Column {
            // "CONTROL DE OPERACIONES" badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .background(PrimaryMint)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            ) {
                Text(
                    text = "CONTROL DE OPERACIONES",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    letterSpacing = 0.5.sp,
                )
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = "ADM · Perú Exchange",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )

            Spacer(Modifier.height(16.dp))

            // Stats row — 3 columns separated by thin dividers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AdminStat(value = "\$142K", label = "Volumen", valueColor = Color.White)
                StatDivider()
                AdminStat(value = "2", label = "Disputas", valueColor = DangerColor)
                StatDivider()
                AdminStat(value = "284", label = "Usuarios", valueColor = Color.White)
            }
        }
    }
}

@Composable
private fun AdminStat(value: String, label: String, valueColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = valueColor,
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.65f),
        )
    }
}

@Composable
private fun StatDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(38.dp)
            .background(Color.White.copy(alpha = 0.2f)),
    )
}

// ---------------------------------------------------------------------------
// Status Pills Row
// ---------------------------------------------------------------------------

@Composable
private fun StatusPillsRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        StatusPill(label = "⚖ 1 En arbitraje", bgColor = DangerColor)
        StatusPill(label = "🔍 1 En revisión", bgColor = WarningColor)
        StatusPill(label = "✅ 0 Resueltas", bgColor = SuccessColor)
    }
}

@Composable
private fun StatusPill(label: String, bgColor: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(bgColor)
            .padding(horizontal = 14.dp, vertical = 6.dp),
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
        )
    }
}

// ---------------------------------------------------------------------------
// Filter Tabs
// ---------------------------------------------------------------------------

@Composable
private fun FilterTabsRow(
    tabs: List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        tabs.forEachIndexed { index, label ->
            val isSelected = index == selectedIndex
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .background(if (isSelected) Primary else Color.Transparent)
                    .border(
                        width = 1.dp,
                        color = if (isSelected) Primary else BorderColor,
                        shape = RoundedCornerShape(50.dp),
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isSelected) Color.White else TextMuted,
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Dispute Card
// ---------------------------------------------------------------------------

@Composable
private fun DisputeCard(dispute: DisputeItem, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceColor),
        border = BorderStroke(1.dp, BorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // TX id + status badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = dispute.txId,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = DangerColor,
                )
                StatusBadge(label = dispute.status, color = dispute.statusColor)
            }

            Spacer(Modifier.height(8.dp))

            // Buyer + Seller
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(text = "Comprador: ${dispute.buyer}", fontSize = 12.sp, color = TextMuted)
                Text(text = "Vendedor: ${dispute.seller}", fontSize = 12.sp, color = TextMuted)
            }

            Spacer(Modifier.height(8.dp))

            // Amount + date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = dispute.amount,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Primary,
                )
                Text(text = dispute.date, fontSize = 12.sp, color = TextMuted)
            }

            Spacer(Modifier.height(12.dp))

            // Action buttons
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = {},
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Primary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.height(34.dp),
                ) {
                    Text(text = "Ver Detalle", fontSize = 12.sp, color = Primary)
                }
                Button(
                    onClick = {},
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessColor),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.height(34.dp),
                ) {
                    Text(text = "Resolver", fontSize = 12.sp, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(label: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(50.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = color,
        )
    }
}

// ---------------------------------------------------------------------------
// Preview
// ---------------------------------------------------------------------------

@Preview(showBackground = true)
@Composable
fun AdminScreenPreview() {
    AdminScreen()
}
