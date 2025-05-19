package com.appdev.smartkisan.presentation.Resuable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    text: String,
    textColor: Color = Color.White,
    width: Float = 0.8f,
    startColor: Color = Color(0xFF66BB6A), // Lighter purple
    endColor: Color = Color(0xFF2E7D32),   // Darker purple
    height: Dp = 50.dp,
    fontSize: TextUnit = 18.sp,
    isBorderPreview: Boolean = false,
    borderColor: Color = Color(0xff7B70FF),
    isEnabled: Boolean = true
) {
    val gradient by remember {
        mutableStateOf(
            Brush.verticalGradient(
                colors = listOf(startColor, endColor)
            )
        )
    }

    Card(
        onClick = { onClick() },
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .fillMaxWidth(width)
            .height(height)
            .clip(RoundedCornerShape(8.dp)),
        enabled = isEnabled,
        colors = CardDefaults.cardColors(
            disabledContainerColor = Color.Gray,
            containerColor = Color.Transparent // Make card transparent to show gradient
        ),
        border = if (isBorderPreview) BorderStroke(2.dp, borderColor) else BorderStroke(
            0.dp,
            Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient), // Apply gradient here
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = fontSize,
                letterSpacing = 0.sp,
                color = textColor,
                textAlign = TextAlign.Center
            )
        }
    }
}