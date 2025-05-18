package com.appdev.smartkisan.ui.OtherComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.appdev.smartkisan.R


@Composable
fun DotsLoading(modifier: Modifier = Modifier) {
    // Change from Column to Box and use the passed modifier
    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopCenter
    ) {
        // Load the Lottie animation
        val composition by rememberLottieComposition(
            spec = LottieCompositionSpec.RawRes(R.raw.simpleloading)
        )

        // Configure the animation
        val progress by animateLottieCompositionAsState(
            composition = composition,
            iterations = LottieConstants.IterateForever, // Loop infinitely
            isPlaying = true,
            speed = 1.0f,
            restartOnPlay = false
        )

        // Display the animation
        LottieAnimation(
            composition = composition,
            progress = { progress },
        )
    }
}