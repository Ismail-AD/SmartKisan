package com.appdev.smartkisan.presentation.Resuable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.appdev.smartkisan.R

@Composable
fun CustomLoader(text: String = "Wait for a while...") {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Dialog(onDismissRequest = { /*TODO*/ }) {
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .background(
                            color = Color.Black,
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Load the Lottie animation
                        val composition by rememberLottieComposition(
                            spec = LottieCompositionSpec.RawRes(R.raw.circleloader)
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
                            modifier = Modifier.size(80.dp)
                        )

                        Text(
                            text = text,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(vertical = 7.dp),
                            color = MaterialTheme.colorScheme.surfaceTint
                        )
                    }
                }
            }
        }
    }
}