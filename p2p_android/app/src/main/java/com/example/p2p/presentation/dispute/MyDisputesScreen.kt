package com.example.p2p.presentation.dispute

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.p2p.ui.theme.BackgroundApp
import com.example.p2p.ui.theme.BorderColor
import com.example.p2p.ui.theme.DangerColor
import com.example.p2p.ui.theme.Primary
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
    val opponent: String,
    val amount: String,
    val date: String,
)

private val sampleDisputes = listOf(
    DisputeItem(
        txId = "#TX-9982",
        status = "EN ARBITRAJE",
        statusColor = DangerColor,
        opponent = "vs Victor Vendedor",
        amount = "\$200 USD",
        date = "25 May 2026",
    ),
    DisputeItem(
        txId = "#TX-9756",
        status = "EN REVISIÓN",
        statusColor = WarningColor,
        opponent = "vs Ana Martínez",
        amount = "\$500 USD",
        date = "23 May 2026",
    ),
)

// ---------------------------------------------------------------------------
// Screen
// ---------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDisputesScreen(onBack: () -> Unit = {}) {
    var selectedFilter by remember { mutableStateOf(0) }
    val filters = listOf("Todas", "Arbitraje", "Revisión", "Resuelta")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mis Disputas",
                        fontWeight = FontWeight.Bold,
                        color = TextMain,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = TextMain,
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
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Register new dispute button
            item {
                Button(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DangerColor),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        tint = Color.White,
                    )
                    Text(
                        text = " Registrar nueva disputa",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                    )
                }
            }

            // Filter chips
            item {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    filters.forEachIndexed { index, label ->
                        val isSelected = index == selectedFilter
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

            // Dispute cards
            items(sampleDisputes.size) { index ->
                DisputeCard(dispute = sampleDisputes[index])
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Dispute Card
// ---------------------------------------------------------------------------

@Composable
private fun DisputeCard(dispute: DisputeItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                    fontSize = 15.sp,
                    color = TextMain,
                )
                DisputeStatusBadge(label = dispute.status, color = dispute.statusColor)
            }

            Spacer(Modifier.height(8.dp))

            // Opponent
            Text(
                text = dispute.opponent,
                fontSize = 13.sp,
                color = TextMuted,
            )

            Spacer(Modifier.height(6.dp))

            // Amount + date row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = dispute.amount,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Primary,
                )
                Text(
                    text = dispute.date,
                    fontSize = 12.sp,
                    color = TextMuted,
                )
            }

            Spacer(Modifier.height(12.dp))

            // Ver detalle button
            OutlinedButton(
                onClick = {},
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Primary),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                modifier = Modifier.height(36.dp),
            ) {
                Text(text = "Ver detalle", fontSize = 13.sp, color = Primary)
            }
        }
    }
}

@Composable
private fun DisputeStatusBadge(label: String, color: Color) {
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
fun MyDisputesScreenPreview() {
    MyDisputesScreen()
}
