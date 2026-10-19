package org.example.project.ui.theme

import androidx.compose.material3.MaterialTheme
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

@Composable
fun RangoTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = LightColors,
        content = content,
    )
}
