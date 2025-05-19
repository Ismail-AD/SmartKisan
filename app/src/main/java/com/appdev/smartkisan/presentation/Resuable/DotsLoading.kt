package com.appdev.smartkisan.presentation.Resuable

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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