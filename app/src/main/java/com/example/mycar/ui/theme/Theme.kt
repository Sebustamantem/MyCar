package com.example.mycar.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Paleta clara para toda la app
private val MyCarColorScheme = lightColorScheme(
    primary = MyCarBlue,
    secondary = MyCarLightBlue,
    tertiary = MyCarGreen,
    background = MyCarLightGray,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = MyCarBlue,
    onBackground = MyCarBlack,
    onSurface = MyCarBlack,
    error = MyCarRed,
    onError = Color.White
)

// Tema global sin modo oscuro
@Composable
fun MyCarTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MyCarColorScheme,
        typography = Typography,
        content = content
    )
}
