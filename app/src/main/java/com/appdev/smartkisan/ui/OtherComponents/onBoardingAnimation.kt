package com.appdev.smartkisan.ui.OtherComponents

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.appdev.smartkisan.R

@Composable
fun AnimationComposable(modifier: Modifier = Modifier, size: Dp = 100.dp, resourceId: Int) {
    // Change from Column to Box and use the passed modifier
    Box(
        modifier = modifier,
    ) {
        // Load the Lottie animation
        val composition by rememberLottieComposition(
            spec = LottieCompositionSpec.RawRes(resourceId)
        )

        // Configure the animation
        val progress by animateLottieCompositionAsState(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            isPlaying = true,
            speed = 1.0f,
            restartOnPlay = false
        )

        // Display the animation
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(size)
        )
    }
}