package com.example.p2p.presentation.vendor

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.p2p.data.remote.model.Transaction
import com.example.p2p.presentation.transaction.TransactionViewModel
import com.example.p2p.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorInboxScreen(
    viewModel: TransactionViewModel,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val pendingTransactions by viewModel.pendingTransactions.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadPendingTransactions()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Inbox de Vendedor",
                        fontWeight = FontWeight.Bold,
                        color = TextMain
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = TextMain)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadPendingTransactions() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refrescar", tint = Primary)
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
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                        )
                    )
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "MODO VENDEDOR",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = WarningColor,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Transacciones Entrantes",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Text(
                            text = "Revisa y confirma los pagos que recibes",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(WarningColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Store, contentDescription = null, tint = WarningColor)
                    }
                }
            }

            if (uiState.isLoading && pendingTransactions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary)
                }
            } else if (pendingTransactions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Inbox,
                            contentDescription = null,
                            tint = BorderColor,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "Sin transacciones pendientes",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextMain
                        )
                        Text(
                            text = "Cuando un comprador suba su voucher, aparecerá aquí.",
                            fontSize = 12.sp,
                            color = TextMuted,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(pendingTransactions) { txn ->
                        VendorTransactionCard(
                            transaction = txn,
                            onConfirm = {
                                viewModel.confirmTransaction(txn.id)
                                Toast.makeText(context, "Operación liberada con éxito", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VendorTransactionCard(
    transaction: Transaction,
    onConfirm: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    val statusText = if (transaction.status == "voucher_uploaded") "Pago recibido · Confirmar" else "Pago pendiente"
                    val statusColor = if (transaction.status == "voucher_uploaded") WarningColor else TextMuted
                    Text(
                        text = statusText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        letterSpacing = 0.8.sp
                    )
                    Text(
                        text = transaction.id.take(8).uppercase(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMain,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(
                            if (transaction.status == "voucher_uploaded") WarningColor.copy(alpha = 0.12f)
                            else BorderColor.copy(alpha = 0.5f)
                        )
                        .border(
                            1.dp,
                            if (transaction.status == "voucher_uploaded") WarningColor.copy(alpha = 0.3f)
                            else BorderColor,
                            RoundedCornerShape(50.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = transaction.status.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (transaction.status == "voucher_uploaded") WarningColor else TextMuted
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(BackgroundApp)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Comprador", fontSize = 12.sp, color = TextMuted)
                    Text(
                        transaction.buyer_name ?: transaction.buyer_id.take(8).uppercase(),
                        fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextMain
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Monto", fontSize = 12.sp, color = TextMuted)
                    Text(
                        "S/ ${transaction.amount_to} PEN · tasa ${transaction.exchange_rate}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMain
                    )
                }
            }

            if (transaction.status == "voucher_uploaded") {
                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessColor),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                    Spacer(Modifier.width(6.dp))
                    Text("Confirmar Pago y Liberar", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
