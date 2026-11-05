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
    val Minus = icon("minus", false, "M5 12h14")
    val ArrowBack = icon("arrow_back", false, "M19 12H5", "M12 19l-7-7 7-7")
    val Clock = icon("clock", false, "M22 12a10 10 0 1 1-20 0 10 10 0 0 1 20 0z", "M12 6v6l4 2")
    val Bike = icon(
        "bike", false,
        "M22 17.5a3.5 3.5 0 1 1-7 0 3.5 3.5 0 0 1 7 0z",
        "M9 17.5a3.5 3.5 0 1 1-7 0 3.5 3.5 0 0 1 7 0z",
        "M16 5a1 1 0 1 1-2 0 1 1 0 0 1 2 0z",
        "M12 17.5V14l-3-3 4-3 2 3h2",
    )
    val Cart = icon(
        "cart", false,
        "M9 21a1 1 0 1 1-2 0 1 1 0 0 1 2 0z",
        "M20 21a1 1 0 1 1-2 0 1 1 0 0 1 2 0z",
        "M2 2h2l2.7 12.4a2 2 0 0 0 2 1.6h9.7a2 2 0 0 0 2-1.6L22 7H5.1",
    )
    val CartOff = icon(
        "cart_off", false,
        "M9 21a1 1 0 1 1-2 0 1 1 0 0 1 2 0z",
        "M20 21a1 1 0 1 1-2 0 1 1 0 0 1 2 0z",
        "M2 2h2l2.7 12.4a2 2 0 0 0 2 1.6h9.7a2 2 0 0 0 2-1.6L22 7H5.1",
        "M3 3l18 18",
    )
    val Trash = icon(
        "trash", false,
        "M3 6h18",
        "M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6",
        "M8 6V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2",
    )
    val Ticket = icon(
        "ticket", false,
        "M2 9a3 3 0 0 1 0 6v2a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2v-2a3 3 0 0 1 0-6V7a2 2 0 0 0-2-2H4a2 2 0 0 0-2 2z",
        "M13 5v2", "M13 11v2", "M13 17v2",
    )
    val Check = icon("check", false, "M20 6L9 17l-5-5")
    val ChevronDown = icon("chevron_down", false, "M6 9l6 6 6-6")
    val Star = icon(
        "star", true,
        "M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01z",
    )
}
