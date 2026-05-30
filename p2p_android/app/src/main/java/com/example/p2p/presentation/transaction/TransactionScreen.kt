package com.example.p2p.presentation.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    transactionId: String? = null,
    viewModel: TransactionViewModel? = null,
    onNavigateToDispute: (String) -> Unit = {},
    onNavigateToReceipt: (String) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val uiState by viewModel?.uiState?.collectAsState(initial = TransactionUiState()) ?: remember { mutableStateOf(TransactionUiState()) }
    var timeLeft by remember { mutableStateOf(15 * 60) } // 15 minutes
    var isReadingOcr by remember { mutableStateOf(false) }

    LaunchedEffect(transactionId) {
        if (transactionId != null) {
            viewModel?.loadTransaction(transactionId)
        }
    }

    // Auto-refresh transaction status every 5 seconds to catch vendor confirmations
    LaunchedEffect(transactionId) {
        while (true) {
            delay(5000L)
            if (transactionId != null) {
                viewModel?.loadTransaction(transactionId)
            }
        }
    }

    LaunchedEffect(timeLeft, uiState.transaction?.status, isReadingOcr) {
        if (timeLeft > 0 && uiState.transaction?.status == "pending" && !isReadingOcr) {
            delay(1000L)
            timeLeft--
        }
    }

    val txn = uiState.transaction
    val statusText = when (txn?.status) {
        "pending" -> "ORDEN P2P EN CURSO"
        "voucher_uploaded" -> "VERIFICANDO PAGO"
        "completed" -> "COMPLETADA"
        "cancelled" -> "CANCELADA"
        "disputed" -> "EN DISPUTA"
        else -> "ORDEN P2P EN CURSO"
    }

    val amountTo = txn?.amount_to ?: 741.60

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
                    IconButton(onClick = onNavigateBack) {
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
                            text = statusText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryMint,
                            letterSpacing = 0.8.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Timer
                    if (txn?.status == "voucher_uploaded" || txn?.status == "completed") {
                        Text(
                            text = "VOUCHER SUBIDO",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryMint,
                            letterSpacing = 1.sp
                        )
                    } else {
                        val minutes = timeLeft / 60
                        val seconds = timeLeft % 60
                        Text(
                            text = String.format("%02d:%02d", minutes, seconds),
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 2.sp
                        )
                    }

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

            val currentStep = when (txn?.status) {
                "pending" -> if (isReadingOcr) 1 else 0
                "voucher_uploaded" -> 1
                "completed" -> 3
                "disputed" -> 2
                else -> 0
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
                                    if (index <= currentStep) Primary
                                    else Color.Transparent
                                )
                                .then(
                                    if (index > currentStep) Modifier.border(2.dp, Primary, CircleShape)
                                    else Modifier
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${index + 1}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (index <= currentStep) Color.White else Primary
                            )
                        }

                        // Connector line (except after last)
                        if (index < steps.size - 1) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(2.dp)
                                    .background(
                                        if (index < currentStep) Primary.copy(alpha = 0.5f)
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
                    val steps = listOf("Pagar", "Voucher", "Confirmar", "Liberado")
                    steps.forEachIndexed { index, label ->
                        Text(
                            text = label,
                            fontSize = 10.sp,
                            fontWeight = if (index <= currentStep) FontWeight.Bold else FontWeight.Normal,
                            color = if (index <= currentStep) Primary else TextMuted,
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
                    text = "S/ $amountTo",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = SuccessColor
                )
            }

            // Upload Zone (only active when pending)
            if (txn?.status == "pending") {
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
                    if (isReadingOcr) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(28.dp),
                                color = Primary,
                                strokeWidth = 3.dp
                            )
                            Text(
                                text = "IA OCR leyendo voucher...",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMain
                            )
                        }
                    } else {
                        Button(onClick = {
                            if (transactionId != null) {
                                scope.launch {
                                    isReadingOcr = true
                                    delay(1900L)
                                    isReadingOcr = false
                                    viewModel?.uploadVoucher(transactionId, "http://dummyimage.com/voucher.jpg")
                                    Toast.makeText(context, "OCR validado. Cambia a modo Vendedor para confirmar.", Toast.LENGTH_LONG).show()
                                }
                            }
                        }, colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)) {
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
                    }
                }
            }

            // Waiting box / Dispute flow
            if (txn?.status == "voucher_uploaded") {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = WarningColor.copy(alpha = 0.1f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, WarningColor.copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Filled.Schedule, contentDescription = null, tint = WarningColor)
                            Column {
                                Text("Esperando confirmación del vendedor...", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextMain)
                                Text("El vendedor está verificando el pago en su cuenta bancaria. Cambia a modo vendedor para simular la liberación.", fontSize = 10.sp, color = TextMuted)
                            }
                        }
                    }

                    Button(
                        onClick = { onNavigateToDispute(transactionId ?: "") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = DangerColor),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Filled.Warning, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Abrir Disputa (vendedor no responde)", color = Color.White, fontSize = 13.sp)
                    }
                }
            }

            // Success View Receipt Option
            if (txn?.status == "completed") {
                Button(
                    onClick = { onNavigateToReceipt(transactionId ?: "") },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessColor)
                ) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text("Ver Comprobante Exitoso", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            // Cancel button (only when pending)
            if (txn?.status == "pending") {
                OutlinedButton(
                    onClick = {
                        if (transactionId != null) {
                            viewModel?.updateStatus(transactionId, "cancelled")
                        }
                    },
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
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
