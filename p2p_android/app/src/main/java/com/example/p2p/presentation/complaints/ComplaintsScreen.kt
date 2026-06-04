package com.example.p2p.presentation.complaints

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.p2p.data.remote.model.ComplaintType
import com.example.p2p.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComplaintsScreen(
    viewModel: ComplaintsViewModel? = null,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val uiState by viewModel?.uiState?.collectAsState() ?: remember { mutableStateOf(ComplaintsUiState()) }

    val types = ComplaintType.all
    var selectedType by remember { mutableStateOf(types[0]) }
    var typeExpanded by remember { mutableStateOf(false) }
    var description by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reclamos", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = TextMain) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
            // ── Nuevo Reclamo ─────────────────────────────────────────────────
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceColor),
                elevation = CardDefaults.cardElevation(1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Nuevo Reclamo", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextMain)

                    // Tipo dropdown
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Tipo de Reclamo", fontSize = 12.sp, color = TextMuted)
                        ExposedDropdownMenuBox(
                            expanded = typeExpanded,
                            onExpandedChange = { typeExpanded = it }
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                                    .border(1.dp, BorderColor, RoundedCornerShape(10.dp))
                                    .padding(horizontal = 14.dp, vertical = 14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(ComplaintType.label(selectedType), fontSize = 13.sp, color = TextMain)
                                    Icon(
                                        if (typeExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                        contentDescription = null, tint = TextMuted, modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            ExposedDropdownMenu(expanded = typeExpanded, onDismissRequest = { typeExpanded = false }) {
                                types.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(ComplaintType.label(option), fontSize = 13.sp) },
                                        onClick = { selectedType = option; typeExpanded = false }
                                    )
                                }
                            }
                        }
                    }

                    // Descripción
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        placeholder = { Text("Describe tu reclamo con detalle...", fontSize = 13.sp, color = TextMuted) },
                        modifier = Modifier.fillMaxWidth().height(110.dp),
                        shape = RoundedCornerShape(10.dp),
                        maxLines = 5,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DangerColor,
                            unfocusedBorderColor = BorderColor,
                            focusedTextColor = TextMain,
                            unfocusedTextColor = TextMain
                        )
                    )

                    // Botón enviar
                    Button(
                        onClick = {
                            if (description.isBlank()) {
                                Toast.makeText(context, "Por favor describe tu reclamo", Toast.LENGTH_SHORT).show()
                            } else {
                                viewModel?.createComplaint(
                                    type = selectedType,
                                    description = description,
                                    onSuccess = {
                                        Toast.makeText(context, "Reclamo enviado. Te responderemos en 48h.", Toast.LENGTH_LONG).show()
                                        description = ""
                                    },
                                    onError = { err ->
                                        Toast.makeText(context, "Error: $err", Toast.LENGTH_LONG).show()
                                    }
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DangerColor),
                        enabled = !uiState.isSubmitting
                    ) {
                        if (uiState.isSubmitting) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                        } else {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                            Spacer(Modifier.width(8.dp))
                            Text("Enviar Reclamo", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                        }
                    }
                }
            }

            // ── Mis Reclamos ──────────────────────────────────────────────────
            Text("Mis Reclamos", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextMain)

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary)
                }
            } else if (uiState.complaints.isEmpty()) {
                Text("No tienes reclamos registrados.", fontSize = 13.sp, color = TextMuted)
            } else {
                uiState.complaints.forEach { complaint ->
                    ComplaintItem(
                        id = "#RCL-${complaint.id.takeLast(4).uppercase()}",
                        type = ComplaintType.label(complaint.type),
                        status = when (complaint.status) {
                            "pending"      -> "Pendiente"
                            "under_review" -> "En revisión"
                            "resolved"     -> "Resuelto"
                            "closed"       -> "Cerrado"
                            else           -> complaint.status
                        },
                        statusColor = when (complaint.status) {
                            "pending"      -> WarningColor
                            "under_review" -> Primary
                            "resolved"     -> SuccessColor
                            else           -> TextMuted
                        },
                        date = complaint.created_at.take(10)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ComplaintItem(id: String, type: String, status: String, statusColor: androidx.compose.ui.graphics.Color, date: String) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceColor),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(id, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextMain)
                Text(type, fontSize = 12.sp, color = TextMuted)
                Text(date, fontSize = 11.sp, color = TextMuted)
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .background(statusColor.copy(alpha = 0.12f))
                    .border(1.dp, statusColor.copy(alpha = 0.3f), RoundedCornerShape(50.dp))
                    .padding(horizontal = 12.dp, vertical = 5.dp)
            ) {
                Text(status, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = statusColor)
            }
        }
    }
}