package com.example.p2p.presentation.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.p2p.ui.theme.*

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    // Navigate on success
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) onLoginSuccess()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundApp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // ── Logo ──────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Primary, PrimaryMint)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "PE",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 26.sp
                )
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Bienvenido",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextMain
            )
            Text(
                text = "Inicia sesión para continuar",
                fontSize = 13.sp,
                color = TextMuted,
                modifier = Modifier.padding(top = 4.dp, bottom = 28.dp)
            )

            // ── Card ──────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    // Error banner
                    AnimatedVisibility(visible = uiState.error != null) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = DangerColor.copy(alpha = 0.08f)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = uiState.error ?: "",
                                color = DangerColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    // Email field
                    Text(
                        "EMAIL",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        letterSpacing = 0.8.sp,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    OutlinedTextField(
                        value = uiState.email,
                        onValueChange = viewModel::onEmailChange,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null, tint = TextMuted)
                        },
                        placeholder = { Text("tu@email.com", color = TextMuted, fontSize = 13.sp) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = BorderColor,
                            focusedContainerColor = SurfaceColor,
                            unfocusedContainerColor = SurfaceColor
                        ),
                        singleLine = true
                    )

                    Spacer(Modifier.height(12.dp))

                    // Password field
                    Text(
                        "CONTRASEÑA",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        letterSpacing = 0.8.sp,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    OutlinedTextField(
                        value = uiState.password,
                        onValueChange = viewModel::onPasswordChange,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = TextMuted)
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff
                                    else Icons.Default.Visibility,
                                    contentDescription = "Toggle password",
                                    tint = TextMuted
                                )
                            }
                        },
                        placeholder = { Text("••••••••", color = TextMuted, fontSize = 13.sp) },
                        visualTransformation = if (passwordVisible) VisualTransformation.None
                        else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                viewModel.login()
                            }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = BorderColor,
                            focusedContainerColor = SurfaceColor,
                            unfocusedContainerColor = SurfaceColor
                        ),
                        singleLine = true
                    )

                    Spacer(Modifier.height(20.dp))

                    // Login button
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.login()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        enabled = !uiState.isLoading
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                "Iniciar Sesión",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Footer hint
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Primary.copy(alpha = 0.06f)
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        "CREDENCIALES DE PRUEBA",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(Modifier.height(6.dp))
                    listOf(
                        "comprador@peruexchange.com  /  Comprador123!",
                        "vendedor@peruexchange.com   /  Vendedor123!",
                        "admin@peruexchange.com      /  Admin123!"
                    ).forEach { hint ->
                        Text(hint, fontSize = 10.sp, color = TextMuted, modifier = Modifier.padding(top = 2.dp))
                    }
                }
            }
        }
    }
}
