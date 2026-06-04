package com.example.p2p.presentation.offer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.p2p.ui.theme.*

import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.p2p.data.remote.model.CreateOfferRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublishScreen(
    viewModel: PublishViewModel? = null,
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel?.uiState?.collectAsState(initial = PublishUiState()) ?: remember { mutableStateOf(PublishUiState()) }
    val context = LocalContext.current

    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            Toast.makeText(context, "Oferta publicada con éxito", Toast.LENGTH_SHORT).show()
            viewModel?.resetState()
            onNavigateBack()
        }
    }

    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            Toast.makeText(context, uiState.error, Toast.LENGTH_LONG).show()
            viewModel?.resetState()
        }
    }

    var amountText by remember { mutableStateOf("") }
    var selectedSaleMode by remember { mutableStateOf(0) } // 0 = Completa, 1 = Por Partes
    var selectedRate by remember { mutableStateOf(0) }     // 0 = Mercado, 1 = Rápida
    var customRateEnabled by remember { mutableStateOf(false) }
    var customRateText by remember { mutableStateOf("3.780") }
    var minTransactionText by remember { mutableStateOf("") }
    var maxTransactionText by remember { mutableStateOf("") }

    var selectedCurrency by remember { mutableStateOf("USD") }
    var selectedFiatCurrency by remember { mutableStateOf("PEN") }


    val currencies = listOf("USD", "EUR", "USDT")
    val fiatCurrencies = listOf("PEN", "COP", "MXN", "ARS")

    val currentRate = if (customRateEnabled) {
        customRateText.toDoubleOrNull() ?: 3.780
    } else {
        if (selectedRate == 0) 3.780 else 3.772
    }
    val amountDouble = amountText.toDoubleOrNull() ?: 0.0
    val amountToReceive = amountDouble * currentRate

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Publicar Anuncio P2P",
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

            // --- Currency pair card ---
            PublishSectionCard(title = "Par de Divisas") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Ofrezco dropdown
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Ofrezco",
                            fontSize = 11.sp,
                            color = TextMuted,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.5.dp, BorderColor, RoundedCornerShape(10.dp))
                                .background(SurfaceColor)
                                .padding(horizontal = 14.dp, vertical = 14.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "USD",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextMain
                                )
                                Text(text = "▾", fontSize = 12.sp, color = TextMuted)
                            }
                        }
                    }

                    // Swap icon
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Primary.copy(alpha = 0.10f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.SwapHoriz,
                            contentDescription = "Intercambiar",
                            tint = Primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Recibo dropdown
                    var selectedFiatCurrency by remember { mutableStateOf("PEN") }
                    var expandedFiat by remember { mutableStateOf(false) }
                    val fiatCurrencies = listOf("PEN", "COP", "MXN", "ARS")

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Recibo", fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(4.dp))
                        ExposedDropdownMenuBox(
                            expanded = expandedFiat,
                            onExpandedChange = { expandedFiat = !expandedFiat }
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(1.5.dp, BorderColor, RoundedCornerShape(10.dp))
                                    .background(SurfaceColor)
                                    .padding(horizontal = 14.dp, vertical = 14.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = selectedFiatCurrency, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextMain)
                                    Text(text = "▾", fontSize = 12.sp, color = TextMuted)
                                }
                            }
                            ExposedDropdownMenu(
                                expanded = expandedFiat,
                                onDismissRequest = { expandedFiat = false }
                            ) {
                                fiatCurrencies.forEach { cur ->
                                    DropdownMenuItem(
                                        text = { Text(cur) },
                                        onClick = { selectedFiatCurrency = cur; expandedFiat = false }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // --- Amount card ---
            PublishSectionCard(title = "Monto Total Disponible") {
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    placeholder = {
                        Text("Ej. 500.00", color = TextMuted, fontSize = 14.sp)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = BorderColor,
                        focusedTextColor = TextMain,
                        unfocusedTextColor = TextMain,
                        cursorColor = Primary
                    )
                )

                if (selectedSaleMode == 1) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Límites por transacción (Opcional)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextMuted
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = minTransactionText,
                            onValueChange = { minTransactionText = it },
                            placeholder = { Text("Mínimo", color = TextMuted, fontSize = 13.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = BorderColor,
                                focusedTextColor = TextMain,
                                unfocusedTextColor = TextMain,
                                cursorColor = Primary
                            )
                        )
                        OutlinedTextField(
                            value = maxTransactionText,
                            onValueChange = { maxTransactionText = it },
                            placeholder = { Text("Máximo", color = TextMuted, fontSize = 13.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = BorderColor,
                                focusedTextColor = TextMain,
                                unfocusedTextColor = TextMain,
                                cursorColor = Primary
                            )
                        )
                    }
                }
            }

            // --- Sale mode card ---
            PublishSectionCard(title = "Modo de Venta") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Venta Completa
                    SaleModeOption(
                        modifier = Modifier.weight(1f),
                        title = "Venta Completa",
                        description = "Vendo todo el monto",
                        iconVector = Icons.Filled.MonetizationOn,
                        isSelected = selectedSaleMode == 0,
                        onClick = { selectedSaleMode = 0 }
                    )
                    // Venta por Partes
                    SaleModeOption(
                        modifier = Modifier.weight(1f),
                        title = "Venta por Partes",
                        description = "El comprador elige cuánto",
                        iconVector = Icons.Filled.Extension,
                        isSelected = selectedSaleMode == 1,
                        onClick = { selectedSaleMode = 1 }
                    )
                }
            }

            // --- Exchange rate card ---
            PublishSectionCard(title = "Tasa de Cambio") {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Two rate options
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Tasa Mercado
                        RateOption(
                            modifier = Modifier.weight(1f),
                            label = "Tasa Mercado",
                            rate = "S/ 3.780",
                            rateColor = SuccessColor,
                            subtitle = "Precio actual",
                            isSelected = selectedRate == 0,
                            onClick = { selectedRate = 0 }
                        )
                        // Venta Rápida
                        RateOption(
                            modifier = Modifier.weight(1f),
                            label = "Venta Rápida",
                            rate = "S/ 3.772",
                            rateColor = WarningColor,
                            subtitle = "Tasa recomendada",
                            isSelected = selectedRate == 1,
                            onClick = { selectedRate = 1 }
                        )
                    }

                    // Custom rate toggle row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(BackgroundApp)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Switch(
                            checked = customRateEnabled,
                            onCheckedChange = { customRateEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = SurfaceColor,
                                checkedTrackColor = Primary,
                                uncheckedThumbColor = SurfaceColor,
                                uncheckedTrackColor = BorderColor
                            ),
                            modifier = Modifier.height(24.dp)
                        )
                        Text(
                            text = "Tasa personalizada",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextMain,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = customRateText,
                            onValueChange = { customRateText = it },
                            modifier = Modifier.width(90.dp),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true,
                            enabled = customRateEnabled,
                            textStyle = LocalTextStyle.current.copy(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (customRateEnabled) TextMain else TextMuted
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = BorderColor,
                                disabledBorderColor = BorderColor,
                                focusedTextColor = TextMain,
                                unfocusedTextColor = TextMuted,
                                cursorColor = Primary
                            )
                        )
                    }
                }
            }

            // --- Preview card ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Primary.copy(alpha = 0.06f))
                    .border(1.dp, Primary.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Vista Previa",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMain
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Recibirás:", fontSize = 13.sp, color = TextMuted)
                    Text(
                        text = "S/ ${String.format("%.2f", amountToReceive)}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = SuccessColor
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Tasa:", fontSize = 13.sp, color = TextMuted)
                    Text(
                        text = String.format("%.3f", currentRate),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextMain
                    )
                }
            }

            // --- Publish button ---
            Button(
                onClick = {
                    val minVal = if (selectedSaleMode == 0) amountDouble else (minTransactionText.toDoubleOrNull() ?: 50.0)
                    val maxVal = if (selectedSaleMode == 0) amountDouble else (maxTransactionText.toDoubleOrNull() ?: amountDouble)

                    if (amountDouble <= 0) {
                        Toast.makeText(context, "Ingresa un monto válido.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (selectedSaleMode == 1) {
                        if (minVal > maxVal) {
                            Toast.makeText(context, "El mínimo no puede ser mayor al máximo.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (maxVal > amountDouble) {
                            Toast.makeText(context, "El máximo no puede superar el monto disponible.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                    }

                    val req = CreateOfferRequest(
                        currency = selectedCurrency,
                        fiat_currency = selectedFiatCurrency,
                        amount = amountDouble,
                        price_per_unit = currentRate,
                        offer_type = if (selectedSaleMode == 0) "full" else "partial",
                        min_transaction = minVal,
                        max_transaction = maxVal,
                        payment_methods = listOf("BCP")
                    )
                    viewModel?.publishOffer(req)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                enabled = !uiState.isLoading && amountText.isNotEmpty()
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Icon(
                        imageVector = Icons.Filled.Campaign,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Publicar Anuncio",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

// ── Helpers ──────────────────────────────────────────────────────────────────

@Composable
private fun PublishSectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceColor)
            .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextMain
        )
        content()
    }
}

@Composable
private fun SaleModeOption(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    iconVector: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) Primary else BorderColor
    val bgColor = if (isSelected) Primary.copy(alpha = 0.06f) else SurfaceColor

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = iconVector,
            contentDescription = null,
            tint = if (isSelected) Primary else TextMuted,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Primary else TextMain
        )
        Text(
            text = description,
            fontSize = 10.sp,
            color = TextMuted,
            lineHeight = 13.sp
        )
    }
}

@Composable
private fun RateOption(
    modifier: Modifier = Modifier,
    label: String,
    rate: String,
    rateColor: Color,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) Primary else BorderColor
    val bgColor = if (isSelected) Primary.copy(alpha = 0.06f) else SurfaceColor

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isSelected) Primary else TextMuted
        )
        Text(
            text = rate,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = rateColor
        )
        Text(
            text = subtitle,
            fontSize = 10.sp,
            color = TextMuted
        )
    }
}
