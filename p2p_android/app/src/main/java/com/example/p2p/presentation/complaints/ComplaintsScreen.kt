package com.example.p2p.presentation.complaints

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.p2p.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComplaintsScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Reclamos",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = TextMain
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = TextMain)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceColor)
            )
        },
        containerColor = BackgroundApp
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // ── New Complaint Card ────────────────────────────────────────────
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceColor),
                elevation = CardDefaults.cardElevation(1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        "Nuevo Reclamo",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMain
                    )

                    // Type dropdown
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Tipo de Reclamo", fontSize = 12.sp, color = TextMuted)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, BorderColor, RoundedCornerShape(10.dp))
                                .padding(horizontal = 14.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Tipo: Problema con transacción",
                                fontSize = 13.sp,
                                color = TextMain
                            )
                            Icon(
                                Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Description textarea
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        placeholder = {
                            Text(
                                "Describe tu reclamo con detalle...",
                                fontSize = 13.sp,
                                color = TextMuted
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        shape = RoundedCornerShape(10.dp),
                        maxLines = 5,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DangerColor,
                            unfocusedBorderColor = BorderColor,
                            focusedTextColor = TextMain,
                            unfocusedTextColor = TextMain
                        )
                    )

                    // Submit button
                    Button(
                        onClick = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DangerColor)
                    ) {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.White
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Enviar Reclamo",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }

            // ── My Complaints section ─────────────────────────────────────────
            Text(
                "Mis Reclamos",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextMain
            )

            ComplaintItem(
                id = "#RCL-001",
                type = "Problema con transacción",
                status = "En revisión",
                statusColor = WarningColor,
                date = "20 May 2026"
            )
            ComplaintItem(
                id = "#RCL-002",
                type = "Error en plataforma",
                status = "Resuelto",
                statusColor = SuccessColor,
                date = "15 May 2026"
            )

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ComplaintItem(
    id: String,
    type: String,
    status: String,
    statusColor: Color,
    date: String
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceColor),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    id,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMain
                )
                Text(
                    type,
                    fontSize = 12.sp,
                    color = TextMuted
                )
                Text(
                    date,
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }
            // Status pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .background(statusColor.copy(alpha = 0.12f))
                    .border(1.dp, statusColor.copy(alpha = 0.3f), RoundedCornerShape(50.dp))
                    .padding(horizontal = 12.dp, vertical = 5.dp)
            ) {
                Text(
                    status,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = statusColor
                )
            }
        }
    }
}
