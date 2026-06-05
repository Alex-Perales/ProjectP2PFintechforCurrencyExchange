package com.example.p2p.presentation.market

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Sort
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.p2p.data.remote.model.ExchangeRate
import com.example.p2p.data.remote.model.Offer
import com.example.p2p.ui.theme.*
import kotlinx.coroutines.delay

// ─── Screen ──────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketScreen(
    viewModel: MarketViewModel,
    userName: String = "Usuario",
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToTransaction: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var showBuyDialog by remember { mutableStateOf<Offer?>(null) }
    val context = LocalContext.current

    // Todas las divisas disponibles — ambos lados iguales (S/→USD o USD→S/ etc.)
    val allCurrencies = listOf("PEN", "USD", "EUR", "USDT", "COP", "MXN", "ARS", "GBP", "BRL", "CAD", "AUD", "JPY", "CLP")
    var selectedFiat     by remember { mutableStateOf("PEN") }
    var selectedCurrency by remember { mutableStateOf("USD") }

    // Mensaje de bienvenida breve al entrar
    val firstName = userName.split(" ").firstOrNull() ?: userName
    LaunchedEffect(Unit) {
        Toast.makeText(context, "Bienvenido, $firstName 👋", Toast.LENGTH_SHORT).show()
    }

    // Carga de ofertas cuando cambian filtros
    LaunchedEffect(selectedFiat, selectedCurrency) {
        viewModel.loadOffers(currency = selectedCurrency, fiatCurrency = selectedFiat)
    }

    Scaffold(
        containerColor = BackgroundApp,
        topBar = {
            MarketTopBar(
                exchangeRates = uiState.exchangeRates,
                unreadCount = uiState.unreadCount,
                onNavigateToNotifications = {
                    viewModel.loadUnreadCount() // refresca al volver
                    onNavigateToNotifications()
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(0.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // ── Filtros ──────────────────────────────────────────────────────
            item {
                FilterSection(
                    fiatOptions      = allCurrencies.filter { it != selectedCurrency },
                    currencyOptions  = allCurrencies.filter { it != selectedFiat },
                    selectedFiat     = selectedFiat,
                    selectedCurrency = selectedCurrency,
                    onFiatChange     = { selectedFiat = it },
                    onCurrencyChange = { selectedCurrency = it }
                )
            }

            // ── Barra matching + orden ────────────────────────────────────────
            item {
                ActionRow(
                    isLoading = uiState.isLoading,
                    onMatchingClick = {
                        viewModel.matchOffer(
                            currency = selectedCurrency,
                            fiatCurrency = selectedFiat,
                            onMatched = { showBuyDialog = it },
                            onError   = { Toast.makeText(context, "Sin coincidencias: $it", Toast.LENGTH_SHORT).show() }
                        )
                    }
                )
            }

            // ── Contenido ─────────────────────────────────────────────────────
            when {
                uiState.isLoading -> item {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(260.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CircularProgressIndicator(color = Primary, strokeWidth = 3.dp)
                            Text("Buscando ofertas...", color = TextMuted, fontSize = 13.sp)
                        }
                    }
                }

                uiState.error != null -> item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier.size(56.dp).clip(CircleShape)
                                    .background(DangerColor.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.WifiOff, contentDescription = null, tint = DangerColor, modifier = Modifier.size(26.dp))
                            }
                            Text("No se pudo conectar", fontWeight = FontWeight.SemiBold, color = TextMain, fontSize = 14.sp)
                            Text("Verifica tu conexión e inténtalo de nuevo.", color = TextMuted, fontSize = 12.sp)
                            Button(
                                onClick = { viewModel.loadOffers(selectedCurrency, selectedFiat) },
                                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Reintentar")
                            }
                        }
                    }
                }

                uiState.offers.isEmpty() -> item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier.size(64.dp).clip(CircleShape)
                                    .background(Primary.copy(alpha = 0.08f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.SearchOff, contentDescription = null, tint = Primary, modifier = Modifier.size(30.dp))
                            }
                            Text("Sin ofertas disponibles", fontWeight = FontWeight.SemiBold, color = TextMain, fontSize = 14.sp)
                            Text("No hay ofertas de $selectedCurrency → $selectedFiat ahora.", color = TextMuted, fontSize = 12.sp)
                        }
                    }
                }

                else -> {
                    item {
                        OffersHeader(count = uiState.offers.size, from = selectedCurrency, to = selectedFiat)
                    }
                    itemsIndexed(uiState.offers, key = { _, o -> o.id }) { index, offer ->
                        OfferCard(
                            offer = offer,
                            isBestRate = index == 0,
                            onConfirmBuy = { amount ->
                                val req = com.example.p2p.data.remote.model.CreateTransactionRequest(
                                    offer_id = offer.id,
                                    amount_from = amount,
                                    amount_to = amount * offer.price_per_unit,
                                    buyer_payment_account = "Mi Cuenta BCP",
                                    vendor_payment_account = offer.payment_methods?.firstOrNull() ?: "BCP"
                                )
                                viewModel.createTransaction(req,
                                    onSuccess = { txnId -> onNavigateToTransaction(txnId) },
                                    onError   = { err -> Toast.makeText(context, err, Toast.LENGTH_LONG).show() }
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    // ── Matching dialog ───────────────────────────────────────────────────────
    showBuyDialog?.let { offer ->
        MatchingDialog(
            offer = offer,
            onDismiss = { showBuyDialog = null },
            onConfirm = { amount ->
                val req = com.example.p2p.data.remote.model.CreateTransactionRequest(
                    offer_id = offer.id,
                    amount_from = amount,
                    amount_to = amount * offer.price_per_unit,
                    buyer_payment_account = "Mi Cuenta BCP",
                    vendor_payment_account = offer.payment_methods?.firstOrNull() ?: "BCP"
                )
                viewModel.createTransaction(req,
                    onSuccess = { txnId -> showBuyDialog = null; onNavigateToTransaction(txnId) },
                    onError   = { err -> Toast.makeText(context, err, Toast.LENGTH_LONG).show() }
                )
            }
        )
    }
}

// ─── TopBar ──────────────────────────────────────────────────────────────────

@Composable
private fun MarketTopBar(
    exchangeRates: List<ExchangeRate> = emptyList(),
    unreadCount: Int = 0,
    onNavigateToNotifications: () -> Unit = {}
) {
    // Pares relevantes a mostrar en el ticker (→ PEN)
    val targetPairs = listOf("USD", "EUR", "USDT", "COP", "MXN", "ARS")
    val rateMap = exchangeRates.associateBy { "${it.from_currency}_${it.to_currency}" }

    // Busca tasa directa o calcula cruzada via USD
    fun getRateToPen(from: String): Double? {
        rateMap["${from}_PEN"]?.let { return it.rate }
        // Cruzada: from→USD y USD→PEN
        val fromToUsd = rateMap["${from}_USD"]?.rate
        val usdToPen  = rateMap["USD_PEN"]?.rate
        if (fromToUsd != null && usdToPen != null) return fromToUsd * usdToPen
        return null
    }

    val tickerItems: List<Pair<String, String>> = run {
        val fromApi = targetPairs.mapNotNull { from ->
            val rate = getRateToPen(from) ?: return@mapNotNull null
            from to "S/${String.format("%.3f", rate)}"
        }
        fromApi.ifEmpty {
            // Fallback solo si la API no respondió aún
            listOf("USD" to "Cargando...", "EUR" to "Cargando...")
        }
    }

    Surface(color = Primary, shadowElevation = 6.dp) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Peru", color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
                Text("Exchange", color = PrimaryMint, fontWeight = FontWeight.Black, fontSize = 20.sp)
                Spacer(Modifier.weight(1f))
                IconButton(
                    onClick = onNavigateToNotifications,
                    modifier = Modifier.size(36.dp)
                ) {
                    BadgedBox(
                        badge = {
                            if (unreadCount > 0) {
                                Badge(
                                    containerColor = DangerColor,
                                    contentColor = Color.White
                                ) {
                                    Text(
                                        text = if (unreadCount > 99) "99+" else unreadCount.toString(),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notificaciones", tint = Color.White, modifier = Modifier.size(22.dp))
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.15f))
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 7.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                tickerItems.forEach { (currency, rate) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(currency, color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Text(rate, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("▲", color = PrimaryMint, fontSize = 9.sp)
                    }
                }
            }
        }
    }
}

// ─── Filter Section ───────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterSection(
    fiatOptions: List<String>,
    currencyOptions: List<String>,
    selectedFiat: String,
    selectedCurrency: String,
    onFiatChange: (String) -> Unit,
    onCurrencyChange: (String) -> Unit
) {
    Surface(color = SurfaceColor, shadowElevation = 1.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(Icons.Default.FilterList, contentDescription = null, tint = Primary, modifier = Modifier.size(16.dp))
            Text("Filtrar:", fontSize = 12.sp, color = TextMuted, fontWeight = FontWeight.Medium)
            FilterDropdown(
                label = "Tengo",
                selected = selectedFiat,
                options = fiatOptions,
                onSelect = onFiatChange,
                modifier = Modifier.weight(1f)
            )
            Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = Primary, modifier = Modifier.size(20.dp))
            FilterDropdown(
                label = "Quiero",
                selected = selectedCurrency,
                options = currencyOptions,
                onSelect = onCurrencyChange,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterDropdown(
    label: String,
    selected: String,
    options: List<String>,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, if (expanded) Primary else BorderColor, RoundedCornerShape(8.dp))
                .background(BackgroundApp)
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(label, fontSize = 9.sp, color = TextMuted)
                Text(selected, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextMain)
            }
            Icon(
                if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = if (expanded) Primary else TextMuted,
                modifier = Modifier.size(16.dp)
            )
        }
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { opt ->
                DropdownMenuItem(
                    text = {
                        Text(
                            opt,
                            fontWeight = if (opt == selected) FontWeight.Bold else FontWeight.Normal,
                            color = if (opt == selected) Primary else TextMain
                        )
                    },
                    onClick = { onSelect(opt); expanded = false }
                )
            }
        }
    }
}

// ─── Action Row ───────────────────────────────────────────────────────────────

@Composable
private fun ActionRow(isLoading: Boolean, onMatchingClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        OutlinedButton(
            onClick = onMatchingClick,
            enabled = !isLoading,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = WarningColor),
            border = androidx.compose.foundation.BorderStroke(1.dp, WarningColor),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(15.dp))
            Spacer(Modifier.width(4.dp))
            Text("Matching Automático", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
            Text("Mejor precio", fontSize = 11.sp, color = TextMuted)
        }
    }
}

// ─── Offers Header ────────────────────────────────────────────────────────────

@Composable
private fun OffersHeader(count: Int, from: String, to: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Ofertas $to → $from", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextMain)
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .background(Primary)
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text("$count", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// ─── Offer Card ───────────────────────────────────────────────────────────────

@Composable
private fun OfferCard(
    offer: Offer,
    isBestRate: Boolean = false,
    onConfirmBuy: (Double) -> Unit
) {
    val isPartial = offer.offer_type == "partial"
    var isExpanded by remember { mutableStateOf(false) }
    var buyAmountText by remember { mutableStateOf("") }
    val buyAmountDouble = buyAmountText.toDoubleOrNull() ?: 0.0

    val isAmountValid = if (isPartial) {
        buyAmountDouble >= offer.min_transaction &&
        buyAmountDouble <= (offer.max_transaction ?: offer.available_amount) &&
        buyAmountDouble <= offer.available_amount
    } else true

    val initials = offer.vendor?.full_name?.trim()?.split(" ")
        ?.filter { it.isNotEmpty() }?.take(2)
        ?.map { it.first().uppercaseChar() }
        ?.joinToString("") ?: "??"

    Card(
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceColor),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            // Borde superior sutil
            HorizontalDivider(color = BorderColor, thickness = 0.5.dp)

            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Mejor tasa badge
                if (isBestRate) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(WarningColor.copy(alpha = 0.1f))
                            .border(1.dp, WarningColor.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = WarningColor, modifier = Modifier.size(12.dp))
                        Text("Mejor tasa del mercado", color = WarningColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Vendor + estado
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier.size(40.dp).clip(CircleShape)
                            .background(Brush.linearGradient(listOf(Primary, PrimaryLight))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(initials, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                offer.vendor?.full_name ?: "Vendedor",
                                fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextMain
                            )
                            if (offer.vendor?.kyc_verified == true) {
                                Icon(Icons.Default.Verified, contentDescription = null, tint = Primary, modifier = Modifier.size(13.dp))
                            }
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Text("★ ${offer.vendor?.rating ?: 4.9}", fontSize = 11.sp, color = WarningColor, fontWeight = FontWeight.SemiBold)
                            Text("${offer.vendor?.total_transactions ?: 0} ops", fontSize = 11.sp, color = TextMuted)
                            Text("98%", fontSize = 11.sp, color = SuccessColor, fontWeight = FontWeight.Medium)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50.dp))
                            .background(SuccessColor.copy(alpha = 0.1f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("En línea", color = SuccessColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Tasa + disponible
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Tasa", fontSize = 10.sp, color = TextMuted)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (isPartial) Primary.copy(alpha = 0.1f) else SuccessColor.copy(alpha = 0.1f))
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    if (isPartial) "PARCIAL" else "COMPLETA",
                                    color = if (isPartial) Primary else SuccessColor,
                                    fontSize = 8.sp, fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Text(
                            "${offer.fiat_currency} ${String.format("%.3f", offer.price_per_unit)}",
                            fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = TextMain
                        )
                    }
                    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(
                            "${String.format("%.2f", offer.available_amount)} ${offer.currency}",
                            fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextMain
                        )
                        Text("disponibles", fontSize = 10.sp, color = TextMuted)
                        if (isPartial) {
                            Text(
                                "Rango: ${offer.min_transaction.toInt()} – ${(offer.max_transaction ?: offer.available_amount).toInt()} ${offer.currency}",
                                fontSize = 10.sp, color = TextMuted
                            )
                        }
                    }
                }

                // Métodos + botón
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        offer.payment_methods?.take(2)?.forEach { method ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Primary.copy(alpha = 0.08f))
                                    .border(1.dp, Primary.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(method, color = Primary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                    if (isPartial) {
                        Button(
                            onClick = { isExpanded = !isExpanded },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Primary),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            elevation = ButtonDefaults.buttonElevation(0.dp)
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(if (isExpanded) "Cerrar" else "Elegir monto", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    } else {
                        Button(
                            onClick = { onConfirmBuy(offer.available_amount) },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessColor),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            elevation = ButtonDefaults.buttonElevation(0.dp)
                        ) {
                            Text("Comprar todo", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }

                // Panel expandible compra parcial
                if (isExpanded && isPartial) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(BackgroundApp)
                            .border(1.dp, BorderColor, RoundedCornerShape(10.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("¿Cuánto deseas comprar?", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextMain)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = buyAmountText,
                                onValueChange = { buyAmountText = it },
                                placeholder = { Text("0.00", fontSize = 13.sp, color = TextMuted) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Primary, unfocusedBorderColor = BorderColor,
                                    focusedTextColor = TextMain, unfocusedTextColor = TextMain, cursorColor = Primary
                                )
                            )
                            Text(offer.currency, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                        }

                        if (buyAmountDouble > 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SuccessColor.copy(alpha = 0.06f))
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Pagarás aprox.", fontSize = 10.sp, color = TextMuted)
                                    Text(
                                        "${offer.fiat_currency} ${String.format("%.2f", buyAmountDouble * offer.price_per_unit)}",
                                        fontSize = 15.sp, fontWeight = FontWeight.Bold, color = SuccessColor
                                    )
                                }
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                            }

                            when {
                                buyAmountDouble < offer.min_transaction ->
                                    Text("Mínimo: ${offer.min_transaction} ${offer.currency}", color = DangerColor, fontSize = 11.sp)
                                offer.max_transaction != null && buyAmountDouble > offer.max_transaction ->
                                    Text("Máximo: ${offer.max_transaction} ${offer.currency}", color = DangerColor, fontSize = 11.sp)
                                buyAmountDouble > offer.available_amount ->
                                    Text("Disponible: ${offer.available_amount} ${offer.currency}", color = DangerColor, fontSize = 11.sp)
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { onConfirmBuy(buyAmountDouble); isExpanded = false; buyAmountText = "" },
                                enabled = isAmountValid && buyAmountDouble > 0,
                                colors = ButtonDefaults.buttonColors(containerColor = SuccessColor),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f),
                                elevation = ButtonDefaults.buttonElevation(0.dp)
                            ) {
                                Text("Confirmar", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            OutlinedButton(
                                onClick = { isExpanded = false; buyAmountText = "" },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMuted),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
                            ) {
                                Text("Cancelar", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─── Matching Dialog ──────────────────────────────────────────────────────────

@Composable
private fun MatchingDialog(offer: Offer, onDismiss: () -> Unit, onConfirm: (Double) -> Unit) {
    val isPartial = offer.offer_type == "partial"
    var amountText by remember(offer.id) {
        mutableStateOf(if (isPartial) "" else offer.available_amount.toString())
    }
    val amountDouble = amountText.toDoubleOrNull() ?: 0.0
    val isValid = if (isPartial) {
        amountDouble >= offer.min_transaction &&
        amountDouble <= (offer.max_transaction ?: offer.available_amount) &&
        amountDouble <= offer.available_amount
    } else amountDouble == offer.available_amount

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceColor,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Bolt, contentDescription = null, tint = WarningColor)
                Text("Matching encontrado", fontWeight = FontWeight.Bold, color = TextMain)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(BackgroundApp)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text("Vendedor", fontSize = 10.sp, color = TextMuted)
                        Text(offer.vendor?.full_name ?: "Vendedor", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextMain)
                    }
                    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text("Tasa", fontSize = 10.sp, color = TextMuted)
                        Text("${offer.fiat_currency} ${String.format("%.3f", offer.price_per_unit)}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Primary)
                    }
                }
                if (isPartial) {
                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it },
                        label = { Text("Monto a comprar (${offer.currency})") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary, unfocusedBorderColor = BorderColor,
                            focusedTextColor = TextMain, cursorColor = Primary
                        )
                    )
                } else {
                    Text(
                        "Venta completa — debes comprar ${offer.available_amount} ${offer.currency}",
                        fontSize = 12.sp, color = WarningColor
                    )
                }
                if (amountDouble > 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SuccessColor.copy(alpha = 0.07f))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Pagarás", fontSize = 10.sp, color = TextMuted)
                            Text("${offer.fiat_currency} ${String.format("%.2f", amountDouble * offer.price_per_unit)}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = SuccessColor)
                        }
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Primary, modifier = Modifier.size(18.dp))
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(amountDouble) },
                enabled = isValid,
                colors = ButtonDefaults.buttonColors(containerColor = SuccessColor),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Confirmar compra", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = TextMuted) }
        }
    )
}
