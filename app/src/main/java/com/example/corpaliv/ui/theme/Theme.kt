package com.example.corpaliv.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val EsquemaClaro = lightColorScheme(
    primary = ColorPrimario,
    onPrimary = ColorSobrePrimario,
    secondary = ColorSecundario,
    onSecondary = ColorSobreSecundario,
    tertiary = ColorTerciario,
    onTertiary = ColorSobreTerciario,
    background = ColorFondo,
    onBackground = ColorSobreFondo,
    surface = ColorFondo,
    onSurface = ColorSobreFondo,
    surfaceVariant = ColorTarjeta,
    onSurfaceVariant = ColorSobreFondo,
    surfaceTint = ColorPrimario,
    primaryContainer = ColorContenedorPrimario,
    onPrimaryContainer = ColorSobreContenedorPrimario,
    secondaryContainer = ColorSeleccion,
    onSecondaryContainer = ColorSobreSeleccion,
    tertiaryContainer = ColorContenedorTerciario,
    onTertiaryContainer = ColorSobreContenedorTerciario,
    surfaceContainerLowest = ColorSuperficieMasBaja,
    surfaceContainerLow = ColorSuperficieBaja,
    surfaceContainer = ColorSuperficieContenedor,
    surfaceContainerHigh = ColorSuperficieAlta,
    surfaceContainerHighest = ColorTarjeta,
    outline = ColorContorno,
    outlineVariant = ColorContornoVariante
)

private val EsquemaOscuro = darkColorScheme(
    primary = ColorPrimarioOscuro,
    onPrimary = ColorSobrePrimarioOscuro,
    secondary = ColorSecundarioOscuro,
    onSecondary = ColorSobreSecundarioOscuro,
    tertiary = ColorTerciarioOscuro,
    onTertiary = ColorSobreTerciarioOscuro,
    background = ColorFondoOscuro,
    onBackground = ColorSobreFondoOscuro,
    surface = ColorFondoOscuro,
    onSurface = ColorSobreFondoOscuro,
    surfaceVariant = ColorTarjetaOscuro,
    onSurfaceVariant = ColorSobreFondoOscuro,
    surfaceTint = ColorPrimarioOscuro,
    primaryContainer = ColorContenedorPrimarioOscuro,
    onPrimaryContainer = ColorSobreContenedorPrimarioOscuro,
    secondaryContainer = ColorSeleccionOscuro,
    onSecondaryContainer = ColorSobreSeleccionOscuro,
    tertiaryContainer = ColorContenedorTerciarioOscuro,
    onTertiaryContainer = ColorSobreContenedorTerciarioOscuro,
    surfaceContainerLowest = ColorSuperficieMasBajaOscuro,
    surfaceContainerLow = ColorSuperficieBajaOscuro,
    surfaceContainer = ColorSuperficieContenedorOscuro,
    surfaceContainerHigh = ColorSuperficieAltaOscuro,
    surfaceContainerHighest = ColorTarjetaOscuro,
    outline = ColorContornoOscuro,
    outlineVariant = ColorContornoVarianteOscuro
)

@Composable
fun CORPALIVTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> EsquemaOscuro
        else -> EsquemaClaro
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}