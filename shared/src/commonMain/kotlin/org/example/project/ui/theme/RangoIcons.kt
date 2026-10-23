package org.example.project.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

// Ícones de traço (24x24), tingidos via Icon(tint = ...).
private fun icon(name: String, filled: Boolean = false, vararg paths: String): ImageVector =
    ImageVector.Builder(name, 24.dp, 24.dp, 24f, 24f).apply {
        paths.forEach { d ->
            addPath(
                pathData = addPathNodes(d),
                fill = if (filled) SolidColor(Color.Black) else null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            )
        }
    }.build()

object RangoIcons {
    val Search = icon("search", false, "M21 21l-4.35-4.35", "M19 11a8 8 0 1 1-16 0 8 8 0 0 1 16 0z")
    val Bell = icon(
        "bell", false,
        "M6 8a6 6 0 0 1 12 0c0 7 3 9 3 9H3s3-2 3-9",
        "M10.3 21a1.94 1.94 0 0 0 3.4 0",
    )
    val Bag = icon(
        "bag", false,
        "M6 2L3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4z",
        "M3 6h18",
        "M16 10a4 4 0 0 1-8 0",
    )
    val Pin = icon(
        "pin", false,
        "M20 10c0 6-8 12-8 12S4 16 4 10a8 8 0 0 1 16 0z",
        "M15 10a3 3 0 1 1-6 0 3 3 0 0 1 6 0z",
    )
    private const val HeartPath =
        "M19 14c1.49-1.46 3-3.21 3-5.5A5.5 5.5 0 0 0 16.5 3c-1.76 0-3 .5-4.5 2-1.5-1.5-2.74-2-4.5-2A5.5 5.5 0 0 0 2 8.5c0 2.3 1.5 4.05 3 5.5l7 7z"
    val Heart = icon("heart", false, HeartPath)
    val HeartFilled = icon("heart_filled", true, HeartPath)
    val Plus = icon("plus", false, "M12 5v14", "M5 12h14")
    val ChevronDown = icon("chevron_down", false, "M6 9l6 6 6-6")
    val Star = icon(
        "star", true,
        "M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01z",
    )
}
