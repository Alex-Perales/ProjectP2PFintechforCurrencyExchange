package com.example.p2p.presentation.rating

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.p2p.ui.theme.*

@Composable
fun RatingScreen() {
    var commentText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundApp)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {

        // Star icon in circle
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(WarningColor.copy(alpha = 0.12f))
                .border(2.dp, WarningColor.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = WarningColor,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "¿Cómo fue la operación?",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextMain,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Califica a Victor Vendedor",
            fontSize = 13.sp,
            color = TextMuted,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Rating card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 3.dp, shape = RoundedCornerShape(20.dp))
                .clip(RoundedCornerShape(20.dp))
                .background(SurfaceColor)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // 5 stars row — 4 filled, 1 empty (visual static)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(4) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = WarningColor,
                        modifier = Modifier.size(36.dp)
                    )
                }
                Icon(
                    imageVector = Icons.Outlined.StarOutline,
                    contentDescription = null,
                    tint = BorderColor,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Selecciona tu calificación",
                fontSize = 12.sp,
                color = TextMuted,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Comment label
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "Comentario (opcional)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextMain
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Comment text field
            OutlinedTextField(
                value = commentText,
                onValueChange = { commentText = it },
                placeholder = {
                    Text(
                        text = "¿Cómo fue la experiencia con este vendedor?",
                        fontSize = 13.sp,
                        color = TextMuted
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                shape = RoundedCornerShape(12.dp),
                minLines = 3,
                maxLines = 5,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = BorderColor,
                    focusedTextColor = TextMain,
                    unfocusedTextColor = TextMain,
                    cursorColor = Primary
                )
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Submit button
        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary)
        ) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Enviar Calificación",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Ghost skip button
        TextButton(
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Omitir por ahora",
                fontSize = 14.sp,
                color = TextMuted
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
