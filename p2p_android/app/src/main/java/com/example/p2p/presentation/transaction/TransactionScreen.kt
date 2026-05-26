package com.example.p2p.presentation.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.p2p.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(transactionId: String? = null) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Transacción P2P",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 17.sp,
                        color = TextMain
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = TextMain
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceColor
                )
            )
        },
        containerColor = BackgroundApp
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // Dark gradient card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF1A2332), Color(0xFF0F172A))
                        )
                    )
                    .padding(20.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50.dp))
                            .background(PrimaryMint.copy(alpha = 0.18f))
                            .border(1.dp, PrimaryMint.copy(alpha = 0.5f), RoundedCornerShape(50.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "ORDEN P2P EN CURSO",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryMint,
                            letterSpacing = 0.8.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Timer
                    Text(
                        text = "14:23",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Subtitle
                    Text(
                        text = "Capital en custodia · Operación segura",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.55f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Timeline Row
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceColor)
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val steps = listOf("Pagar", "Voucher", "Confirmar", "Liberado")
                    steps.forEachIndexed { index, label ->
                        // Step circle
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(
                                    if (index == 0) Primary
                                    else Color.Transparent
                                )
                                .then(
                                    if (index != 0) Modifier.border(2.dp, Primary, CircleShape)
                                    else Modifier
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${index + 1}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (index == 0) Color.White else Primary
                            )
                        }

                        // Connector line (except after last)
                        if (index < steps.size - 1) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(2.dp)
                                    .background(
                                        if (index == 0) Primary.copy(alpha = 0.5f)
                                        else BorderColor
                                    )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Labels row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("Pagar", "Voucher", "Confirmar", "Liberado").forEachIndexed { index, label ->
                        Text(
                            text = label,
                            fontSize = 10.sp,
                            fontWeight = if (index == 0) FontWeight.Bold else FontWeight.Normal,
                            color = if (index == 0) Primary else TextMuted,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Receiver info card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Primary.copy(alpha = 0.08f))
                    .border(1.dp, Primary.copy(alpha = 0.18f), RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "CUENTA RECEPTORA",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 0.8.sp
                )
                Text(
                    text = "Victor Vendedor",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMain
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "BCP",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                    Text(
                        text = "·",
                        fontSize = 13.sp,
                        color = TextMuted
                    )
                    Text(
                        text = "002-191-0098765432-12",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        color = TextMuted
                    )
                }
            }

            // Amount card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceColor)
                    .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Transfiere exactamente:",
                    fontSize = 13.sp,
                    color = TextMuted
                )
                Text(
                    text = "S/ 741.60",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = SuccessColor
                )
            }

            // Upload zone
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceColor)
                    .border(
                        width = 1.5.dp,
                        color = BorderColor,
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.CloudUpload,
                        contentDescription = "Subir",
                        tint = Primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = "Subir Comprobante de Pago",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMain
                    )
                    Text(
                        text = "IA OCR · Validación automática en segundos",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }

            // Cancel button
            OutlinedButton(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = DangerColor
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, DangerColor)
            ) {
                Text(
                    text = "Cancelar Transacción",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
