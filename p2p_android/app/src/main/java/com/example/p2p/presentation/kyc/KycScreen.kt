package com.example.p2p.presentation.kyc

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.p2p.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KycScreen(
    onNavigateBack: () -> Unit = {}
) {
    // 1 = front DNI active, 2 = back DNI, 3 = selfie
    var currentStep by remember { mutableIntStateOf(1) }

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundApp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ── Top Bar ───────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = TextMain
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Verificación KYC",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextMain
                )
            }

            // ── Step Indicators ───────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 28.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                KycStepDot(stepIndex = 1, currentStep = currentStep, label = "Frente")
                KycStepLine(isCompleted = currentStep > 1)
                KycStepDot(stepIndex = 2, currentStep = currentStep, label = "Dorso")
                KycStepLine(isCompleted = currentStep > 2)
                KycStepDot(stepIndex = 3, currentStep = currentStep, label = "Selfie")
            }

            // ── Step 1: DNI Frontal ───────────────────────────
            KycStepCard(
                isActive = currentStep == 1,
                isCompleted = currentStep > 1,
                stepNumber = 1
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Icon
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(Primary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CreditCard,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(Modifier.height(14.dp))

                    Text(
                        text = "DNI · Cara Frontal",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMain
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = "Toca para capturar o subir la parte\ndelantera de tu DNI",
                        fontSize = 13.sp,
                        color = TextMuted,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    Spacer(Modifier.height(16.dp))

                    // DNI Preview Upload Zone
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .border(
                                width = 2.dp,
                                color = Primary.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .background(Primary.copy(alpha = 0.04f))
                            .padding(12.dp)
                    ) {
                        // Simulated DNI card
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFF0F4FF))
                                .border(1.dp, BorderColor, RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            // DNI header strip
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(18.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFFD32F2F)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "REPÚBLICA DEL PERÚ",
                                    fontSize = 7.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    letterSpacing = 0.6.sp
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 22.dp),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.Top
                            ) {
                                // Fake photo
                                Box(
                                    modifier = Modifier
                                        .size(width = 42.dp, height = 54.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFFBDBDBD)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "FOTO", fontSize = 7.sp, color = Color(0xFF757575))
                                }

                                Spacer(Modifier.width(10.dp))

                                // DNI data
                                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                    Text(
                                        text = "MENDOZA LÓPEZ",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1A237E)
                                    )
                                    Text(
                                        text = "Carlos Andrés",
                                        fontSize = 8.sp,
                                        color = TextMain
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "DNI",
                                            fontSize = 7.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextMuted
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            text = "72345678",
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextMain
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(18.dp))

                    Button(
                        onClick = { currentStep = 2 },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                    ) {
                        Text(
                            "Continuar",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Step 2: DNI Dorso ─────────────────────────────
            KycStepCard(
                isActive = currentStep == 2,
                isCompleted = currentStep > 2,
                stepNumber = 2,
                modifier = Modifier.alpha(if (currentStep >= 2) 1f else 0.45f)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                if (currentStep >= 2) Primary.copy(alpha = 0.12f)
                                else BorderColor
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Flip,
                            contentDescription = null,
                            tint = if (currentStep >= 2) Primary else TextMuted,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(Modifier.height(14.dp))

                    Text(
                        text = "DNI · Cara Posterior",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (currentStep >= 2) TextMain else TextMuted
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = "Toca para capturar o subir la parte\ntrasera de tu DNI",
                        fontSize = 13.sp,
                        color = TextMuted,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    if (currentStep == 2) {
                        Spacer(Modifier.height(16.dp))

                        // Back side upload zone
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .border(
                                    width = 2.dp,
                                    color = Primary.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .background(Primary.copy(alpha = 0.04f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Flip,
                                    contentDescription = null,
                                    tint = Primary.copy(alpha = 0.5f),
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = "Cara posterior del DNI",
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )
                            }
                        }

                        Spacer(Modifier.height(18.dp))

                        Button(
                            onClick = { currentStep = 3 },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Primary)
                        ) {
                            Text(
                                "Continuar",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Step 3: Selfie ────────────────────────────────
            KycStepCard(
                isActive = currentStep == 3,
                isCompleted = false,
                stepNumber = 3,
                modifier = Modifier.alpha(if (currentStep >= 3) 1f else 0.45f)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                if (currentStep >= 3) Primary.copy(alpha = 0.12f)
                                else BorderColor
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = if (currentStep >= 3) Primary else TextMuted,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(Modifier.height(14.dp))

                    Text(
                        text = "Selfie de Verificación",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (currentStep >= 3) TextMain else TextMuted
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = "Tómate una foto sosteniendo tu DNI\njunto a tu rostro",
                        fontSize = 13.sp,
                        color = TextMuted,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    if (currentStep == 3) {
                        Spacer(Modifier.height(16.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .border(
                                    width = 2.dp,
                                    color = Primary.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .background(Primary.copy(alpha = 0.04f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint = Primary.copy(alpha = 0.5f),
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = "Toca para abrir la cámara",
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )
                            }
                        }

                        Spacer(Modifier.height(18.dp))

                        Button(
                            onClick = { /* visual only — submission */ },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryMint)
                        ) {
                            Text(
                                "Enviar Verificación",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF004D40)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Info note
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(WarningColor.copy(alpha = 0.08f))
                    .padding(14.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text(text = "!", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = WarningColor)
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "Tus documentos son procesados de forma segura y encriptada. Solo se utilizan para verificar tu identidad.",
                    fontSize = 11.sp,
                    color = TextMuted,
                    lineHeight = 17.sp
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

// ── Reusable composables ─────────────────────────────────

@Composable
private fun KycStepDot(
    stepIndex: Int,
    currentStep: Int,
    label: String
) {
    val isCompleted = currentStep > stepIndex
    val isActive = currentStep == stepIndex

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isCompleted -> SuccessColor
                        isActive -> Primary
                        else -> BorderColor
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text(
                    text = "$stepIndex",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isActive) Color.White else TextMuted
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            color = when {
                isCompleted -> SuccessColor
                isActive -> Primary
                else -> TextMuted
            },
            fontWeight = if (isActive || isCompleted) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
private fun KycStepLine(isCompleted: Boolean) {
    Box(
        modifier = Modifier
            .padding(horizontal = 4.dp, vertical = 0.dp)
            .padding(bottom = 18.dp)
            .width(40.dp)
            .height(2.dp)
            .background(if (isCompleted) SuccessColor else BorderColor)
    )
}

@Composable
private fun KycStepCard(
    isActive: Boolean,
    isCompleted: Boolean,
    stepNumber: Int,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isCompleted -> SuccessColor.copy(alpha = 0.04f)
                isActive -> SurfaceColor
                else -> SurfaceColor
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isActive) 6.dp else 2.dp
        ),
        border = if (isActive) {
            androidx.compose.foundation.BorderStroke(1.5.dp, Primary.copy(alpha = 0.4f))
        } else if (isCompleted) {
            androidx.compose.foundation.BorderStroke(1.dp, SuccessColor.copy(alpha = 0.3f))
        } else null
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            content = content
        )
    }
}
