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
    val Wallet = icon(
        "wallet", false,
        "M19 7V4a1 1 0 0 0-1-1H5a2 2 0 0 0 0 4h15a1 1 0 0 1 1 1v4h-3a2 2 0 0 0 0 4h3a1 1 0 0 0 1-1v-2a1 1 0 0 0-1-1",
        "M3 5v14a2 2 0 0 0 2 2h15a1 1 0 0 0 1-1v-4",
    )
    val QrCode = icon(
        "qr_code", false,
        "M3 3h5v5H3z", "M16 3h5v5h-5z", "M3 16h5v5H3z",
        "M21 16h-3a2 2 0 0 0-2 2v3", "M21 21v.01", "M12 7v3a2 2 0 0 1-2 2H7",
        "M3 12h.01", "M12 3h.01", "M12 16v.01", "M16 12h1", "M21 12v.01", "M12 21v-1",
    )
    private const val CardPath = "M4 5h16a2 2 0 0 1 2 2v10a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V7a2 2 0 0 1 2-2z"
    val CreditCard = icon("credit_card", false, CardPath, "M2 10h20")
    val DebitCard = icon("debit_card", false, CardPath, "M2 10h20", "M6 15h4")
    val Cash = icon(
        "cash", false,
        "M4 6h16a2 2 0 0 1 2 2v8a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2z",
        "M14 12a2 2 0 1 1-4 0 2 2 0 0 1 4 0z",
        "M6 12h.01", "M18 12h.01",
    )
    val Edit = icon("edit", false, "M17 3a2.83 2.83 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5z", "M15 5l4 4")
    val PinPlus = icon(
        "pin_plus", false,
        "M20 10c0 6-8 12-8 12S4 16 4 10a8 8 0 0 1 16 0z",
        "M12 7v6", "M9 10h6",
    )
    val Lock = icon(
        "lock", false,
        "M5 11h14a2 2 0 0 1 2 2v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-7a2 2 0 0 1 2-2z",
        "M7 11V7a5 5 0 0 1 10 0v4",
    )
    val User = icon(
        "user", false,
        "M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2",
        "M16 7a4 4 0 1 1-8 0 4 4 0 0 1 8 0z",
    )
    val Calendar = icon(
        "calendar", false,
        "M5 4h14a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2z",
        "M16 2v4", "M8 2v4", "M3 10h18",
    )
    val ChevronDown = icon("chevron_down", false, "M6 9l6 6 6-6")
    val Star = icon(
        "star", true,
        "M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01z",
    )
    val Receipt = icon(
        "receipt", false,
        "M4 2v20l2-1 2 1 2-1 2 1 2-1 2 1 2-1 2 1V2l-2 1-2-1-2 1-2-1-2 1-2-1-2 1z",
        "M8 8h8", "M8 12h8", "M8 16h5",
    )
    val TaskAlt = icon("task_alt", false, "M22 12a10 10 0 1 1-20 0 10 10 0 0 1 20 0z", "M8.5 12l2.5 2.5 5-5")
    val Skillet = icon(
        "skillet", false,
        "M2 12h20",
        "M20 12v8a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2v-8",
        "M4 8l16-4",
        "M8.86 6.78l-.45-1.81a2 2 0 0 1 1.45-2.43l1.94-.48a2 2 0 0 1 2.43 1.46l.45 1.8",
    )
    val Home = icon("home", false, "M3 10l9-7 9 7v10a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z", "M9 22V12h6v10")
    val Chat = icon("chat", false, "M7.9 20A9 9 0 1 0 4 16.1L2 22z")
    val Phone = icon(
        "phone", false,
        "M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67" +
            "A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6" +
            "l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z",
    )
}
