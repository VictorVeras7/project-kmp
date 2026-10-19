package org.example.project.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object RangoColors {
    val Primary = Color(0xFFFF6B00)
    val PrimarySoft = Color(0xFFFF8A34)
    val Success = Color(0xFF22A447)
    val Error = Color(0xFFE53935)
}

private val LightColors = lightColorScheme(
    primary = RangoColors.Primary,
    onPrimary = Color.White,
    secondary = RangoColors.PrimarySoft,
    background = Color.White,
    onBackground = Color(0xFF1F1F1F),
    surface = Color.White,
    onSurface = Color(0xFF1F1F1F),
    surfaceVariant = Color(0xFFF7F7F7),
    onSurfaceVariant = Color(0xFF737373),
    outline = Color(0xFFE8E8E8),
    error = RangoColors.Error,
)

private val DarkColors = darkColorScheme(
    primary = RangoColors.Primary,
    onPrimary = Color.White,
    secondary = RangoColors.PrimarySoft,
    background = Color(0xFF121212),
    onBackground = Color(0xFFF2F2F2),
    surface = Color(0xFF121212),
    onSurface = Color(0xFFF2F2F2),
    surfaceVariant = Color(0xFF1E1E1E),
    onSurfaceVariant = Color(0xFFA3A3A3),
    outline = Color(0xFF2E2E2E),
    error = RangoColors.Error,
)

@Composable
fun RangoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
