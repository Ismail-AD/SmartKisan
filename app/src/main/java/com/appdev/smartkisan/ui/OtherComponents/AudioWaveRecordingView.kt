package com.appdev.smartkisan.ui.OtherComponents

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.appdev.smartkisan.R
import kotlinx.coroutines.launch
import kotlin.math.sin

@Composable
fun AudioWaveRecordingView(
    amplitude: Float,
    isRecording: Boolean,
    onStopRecording: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Colors for the visualization
    val primaryColor = Color(0xff68BB59)
    val secondaryColor = Color(0xFFE4F9DD)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Listening...",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Audio wave visualization
        AudioWaveVisualization(
            amplitude = amplitude,
            isRecording = isRecording,
            primaryColor = primaryColor,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Stop recording button
        IconButton(
            onClick = onStopRecording,
            modifier = Modifier
                .size(60.dp)
                .background(primaryColor, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Stop Recording",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun AudioWaveVisualization(
    amplitude: Float,
    isRecording: Boolean,
    primaryColor: Color,
    modifier: Modifier = Modifier
) {
    val baseLineCount = 60 // Number of lines in the audio wave

    // Animation phases for each line to create a natural wave effect
    val phaseShifts = remember {
        List(baseLineCount) { idx -> idx * (360f / baseLineCount) }
    }

    // Animation value for wave movement
    val animationPhase = remember { Animatable(0f) }

    // Start animation when recording
    LaunchedEffect(isRecording) {
        if (isRecording) {
            // Reset and start continuous animation
            animationPhase.snapTo(0f)
            launch {
                animationPhase.animateTo(
                    targetValue = 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(2000, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    )
                )
            }
        } else {
            // Stop animation when not recording
            animationPhase.stop()
        }
    }

    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val centerY = canvasHeight / 2

        // Space between lines
        val lineSpacing = canvasWidth / (baseLineCount - 1)

        // Draw each line of the audio wave
        for (i in 0 until baseLineCount) {
            val x = i * lineSpacing

            // Calculate height based on amplitude and phase
            val phase = (phaseShifts[i] + animationPhase.value) % 360
            val amplitudeMultiplier = if (isRecording) {
                // Dynamic height based on amplitude and a sine wave for natural movement
                val base = sin(Math.toRadians(phase.toDouble())).toFloat() * 0.5f + 0.5f
                (base * 0.4f + amplitude * 0.6f).coerceIn(0.1f, 1f)
            } else {
                0.1f // Minimal height when not recording
            }

            // Line height is a percentage of canvas height
            val lineHeight = canvasHeight * amplitudeMultiplier * 0.8f

            // Line color with decreasing alpha based on distance from center
            val normalizedPosition = (i.toFloat() / baseLineCount) * 2f
            val distanceFromCenter = if (normalizedPosition > 1f) 2f - normalizedPosition else normalizedPosition
            val alpha = 0.3f + distanceFromCenter * 0.7f

            // Draw the vertical line
            drawLine(
                color = primaryColor.copy(alpha = alpha),
                start = Offset(x, centerY - lineHeight / 2),
                end = Offset(x, centerY + lineHeight / 2),
                strokeWidth = 4f,
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
fun SpeechRecordingButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(40.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Mic,
            contentDescription = "Voice Input",
            tint = Color(0xff68BB59),
            modifier = Modifier.size(24.dp)
        )
    }
}