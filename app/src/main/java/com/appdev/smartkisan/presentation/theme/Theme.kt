package com.appdev.smartkisan.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = Color.Black, onBackground = Color.White,
    surface = Color(0xDF0E3636),
    inverseOnSurface = Color(0xff24272E),
    surfaceTint = Color.White,
    surfaceVariant = backOfBarNight,
    tertiaryContainer = Color(0xFFD6F9CC),
    inverseSurface = Color(0xFFA0A0A0),
    inversePrimary =  Color(0xFF333333),
    onTertiary = Color(0xFF294D24).copy(alpha = 0.4f),
    surfaceContainerHigh = Color(0xDF0E3636),
    surfaceContainerLow = Color(0xff68BB59),
    surfaceContainerLowest = Color(0xDF0E3636)



)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = screenBack , onBackground = Color.Black,
    surface =  Color.LightGray.copy(alpha = 0.4f),
    inverseOnSurface = Color(0xffE5EBF7),
    surfaceTint = Color.White,
    surfaceVariant = backOfBar,
    tertiaryContainer = Color(0xFF68BB59),
    inverseSurface = Color(0xFFBABCBE),
    inversePrimary =  Color(0xFF333333),
    onTertiary = Color(0xFF76B947).copy(alpha = 0.2f),
    surfaceContainerHigh = Color.White,
    surfaceContainerLow = buttonColor,
    surfaceContainerLowest = Color.LightGray.copy(alpha = 0.4f)
    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun SmartKisanTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current


    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}