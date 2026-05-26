package com.example.p2p.presentation.cards

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.p2p.ui.theme.BbvaColor
import com.example.p2p.ui.theme.BcpColor
import com.example.p2p.ui.theme.BorderColor
import com.example.p2p.ui.theme.DangerColor
import com.example.p2p.ui.theme.InterbankColor
import com.example.p2p.ui.theme.PlinColor
import com.example.p2p.ui.theme.Primary
import com.example.p2p.ui.theme.SurfaceColor
import com.example.p2p.ui.theme.TextMain
import com.example.p2p.ui.theme.TextMuted
import com.example.p2p.ui.theme.YapeColor

// ---------------------------------------------------------------------------
// Data model
// ---------------------------------------------------------------------------

private data class BankAccount(
    val bankName: String,
    val accountNumber: String,
    val cci: String,
    val color: Color,
)

private val sampleAccounts = listOf(
    BankAccount(
        bankName = "BCP",
        accountNumber = "002-1910098765432",
        cci = "002-191-0098765432-12",
        color = BcpColor,
    ),
    BankAccount(
        bankName = "Interbank",
        accountNumber = "898-333-1234567890",
        cci = "003-898-333123456-78",
        color = InterbankColor,
    ),
)

private data class BankChip(val name: String, val color: Color)

private val bankChips = listOf(
    BankChip("BCP", BcpColor),
    BankChip("Interbank", InterbankColor),
    BankChip("BBVA", BbvaColor),
    BankChip("Yape", YapeColor),
    BankChip("Plin", PlinColor),
)

// ---------------------------------------------------------------------------
// Screen
// ---------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BankAccountsScreen(onBack: () -> Unit = {}) {
    var selectedBank by remember { mutableStateOf("BCP") }
    var accountNumber by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mis Cuentas Bancarias",
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
            // Existing accounts
            items(sampleAccounts.size) { index ->
                BankAccountCard(account = sampleAccounts[index])
            }

            // Divider + add section
            item {
                Spacer(Modifier.height(4.dp))
                HorizontalDivider(color = BorderColor)
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Agregar Cuenta Bancaria",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextMain,
                )
                Spacer(Modifier.height(16.dp))
            }

            // Bank selector chips
            item {
                Text(
                    text = "Selecciona tu banco",
                    fontSize = 13.sp,
                    color = TextMuted,
                    fontWeight = FontWeight.Medium,
                )
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    bankChips.forEach { chip ->
                        val isSelected = chip.name == selectedBank
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50.dp))
                                .background(if (isSelected) Primary else SurfaceColor)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) Primary else BorderColor,
                                    shape = RoundedCornerShape(50.dp),
                                )
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = chip.name,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isSelected) Color.White else TextMuted,
                            )
                        }
                    }
                }
            }

            // Account number field
            item {
                Spacer(Modifier.height(4.dp))
                OutlinedTextField(
                    value = accountNumber,
                    onValueChange = { accountNumber = it },
                    label = { Text("Número de Cuenta / CCI", fontSize = 13.sp) },
                    placeholder = {
                        Text(
                            "Ej. 002-191-0098765432-12",
                            fontSize = 13.sp,
                            color = TextMuted.copy(alpha = 0.6f),
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = BorderColor,
                        focusedLabelColor = Primary,
                        unfocusedLabelColor = TextMuted,
                        cursorColor = Primary,
                    ),
                )
            }

            // Add button
            item {
                Spacer(Modifier.height(4.dp))
                Button(
                    onClick = {},
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
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Agregar Cuenta",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Bank Account Card
// ---------------------------------------------------------------------------

@Composable
private fun BankAccountCard(account: BankAccount) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceColor),
        border = BorderStroke(1.dp, BorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Bank initial circle
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(account.color),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = account.bankName.first().toString(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                )
            }

            Spacer(Modifier.width(14.dp))

            // Bank name + numbers
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = account.bankName,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = TextMain,
                )
                Text(
                    text = account.accountNumber,
                    fontSize = 13.sp,
                    color = TextMuted,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "CCI: ${account.cci}",
                    fontSize = 11.sp,
                    color = TextMuted.copy(alpha = 0.7f),
                )
            }

            // Delete button
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Eliminar",
                    tint = DangerColor,
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Preview
// ---------------------------------------------------------------------------

@Preview(showBackground = true)
@Composable
fun BankAccountsScreenPreview() {
    BankAccountsScreen()
}
