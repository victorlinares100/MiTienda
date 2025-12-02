package com.example.mitienda.theme



import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Definimos qué colores usa el tema Claro (Light)
private val LightColorScheme = lightColorScheme(
    primary = BluePrimary,
    onPrimary = Color.White,
    secondary = BlueSecondary,
    onSecondary = Color.White,
    background = BlueDarkBackground, // Por defecto el fondo será azul oscuro
    surface = WhiteCard,             // Las tarjetas serán blancas
    onSurface = TextBlack,           // Texto sobre blanco es negro
    error = ErrorRed
)

// Esta es la función que envolverá tu App
@Composable
fun MiTiendaTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        // typography = Typography, // Aquí podrías añadir tipografías si tuvieras Type.kt
        content = content
    )
}