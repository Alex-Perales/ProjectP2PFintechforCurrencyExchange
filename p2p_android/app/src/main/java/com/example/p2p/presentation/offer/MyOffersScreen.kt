package com.example.p2p.presentation.offer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.p2p.data.remote.model.Offer
import com.example.p2p.ui.theme.BackgroundApp
import com.example.p2p.ui.theme.BorderColor
import com.example.p2p.ui.theme.DangerColor
import com.example.p2p.ui.theme.Primary
import com.example.p2p.ui.theme.SuccessColor
import com.example.p2p.ui.theme.SurfaceColor
import com.example.p2p.ui.theme.TextMain
import com.example.p2p.ui.theme.TextMuted
import com.example.p2p.ui.theme.WarningColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyOffersScreen(
    viewModel: MyOffersViewModel? = null,
    onBack: () -> Unit = {},
    onPublishClick: () -> Unit = {}
) {
    val uiState by viewModel?.uiState?.collectAsState(initial = MyOffersUiState()) ?: remember { mutableStateOf(MyOffersUiState()) }

    LaunchedEffect(Unit) {
        viewModel?.loadMyOffers()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mis Ofertas",
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
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceColor)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                Button(
                    onClick = onPublishClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        tint = Color.White,
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Publicar Nueva Oferta",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                    )
                }
            }
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
            if (uiState.isLoading && uiState.offers.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        androidx.compose.material3.CircularProgressIndicator(color = Primary)
                    }
                }
            } else if (uiState.offers.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        Text(text = "No tienes ofertas publicadas", color = TextMuted)
                    }
                }
            } else {
                item {
                    Text(
                        text = "${uiState.offers.size} ofertas publicadas",
                        fontSize = 13.sp,
                        color = TextMuted,
                    )
                    Spacer(Modifier.height(4.dp))
                }
                items(uiState.offers) { offer ->
                    OfferCard(
                        offer = offer,
                        onPauseResume = {
                            if (offer.status == "active") viewModel?.pauseOffer(offer.id)
                            else viewModel?.resumeOffer(offer.id)
                        },
                        onDelete = { viewModel?.deleteOffer(offer.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun OfferCard(
    offer: Offer,
    onPauseResume: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceColor),
        border = BorderStroke(1.dp, BorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Currency pair badge + status badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Primary)
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                ) {
                    Text(
                        text = "${offer.currency}→${offer.fiat_currency}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                }

                val isActive = offer.status == "active"
                val statusText = if (isActive) "Activa" else "Pausada"
                val statusColor = if (isActive) SuccessColor else WarningColor
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(statusColor.copy(alpha = 0.12f))
                        .border(1.dp, statusColor.copy(alpha = 0.4f), RoundedCornerShape(50.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text(
                        text = statusText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Rate + available
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                Text(
                    text = "S/ ${offer.price_per_unit}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = TextMain,
                )
                Text(
                    text = "${offer.available_amount} disponibles",
                    fontSize = 13.sp,
                    color = TextMuted,
                )
            }

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Min S/ ${offer.min_transaction} · Max S/ ${offer.max_transaction ?: offer.amount}",
                fontSize = 12.sp,
                color = TextMuted,
            )

            Spacer(Modifier.height(14.dp))

            // Action buttons
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val isActive = offer.status == "active"
                OutlinedButton(
                    onClick = onPauseResume,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, WarningColor),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.height(36.dp),
                ) {
                    Text(
                        text = if (isActive) "Pausar" else "Reanudar",
                        fontSize = 12.sp,
                        color = WarningColor,
                    )
                }
                OutlinedButton(
                    onClick = onDelete,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, DangerColor),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.height(36.dp),
                ) {
                    Text(text = "Eliminar", fontSize = 12.sp, color = DangerColor)
                }
            }
        }
    }
}
