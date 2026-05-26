package com.example.p2p.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AppColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F7FA),
    onPrimaryContainer = TextMain,
    secondary = PrimaryLight,
    onSecondary = Color.White,
    background = BackgroundApp,
    onBackground = TextMain,
    surface = SurfaceColor,
    onSurface = TextMain,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = TextMuted,
    error = DangerColor,
    onError = Color.White,
    outline = BorderColor,
)

@Composable
fun P2PTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = Typography,
        content = content
    )
}
