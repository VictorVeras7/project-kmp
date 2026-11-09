package org.example.project.ui.order

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.example.project.data.Order
import org.example.project.data.OrderStep
import org.example.project.data.TrackingIntervalMs
import org.example.project.ui.checkout.CheckoutScaffold
import org.example.project.ui.checkout.MapPlaceholder
import org.example.project.ui.theme.RangoColors
import org.example.project.ui.theme.RangoIcons

@Composable
fun OrderTrackingScreen(
    order: Order,
    onAdvance: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Avança uma etapa a cada 5 s até a entrega, como no protótipo.
    val advance by rememberUpdatedState(onAdvance)
    LaunchedEffect(order.id, order.step) {
        if (order.step != OrderStep.Delivered) {
            delay(TrackingIntervalMs)
            advance()
        }
    }

    CheckoutScaffold(
        title = "Acompanhar pedido",
        subtitle = order.id,
        onBack = onBack,
        modifier = modifier,
        bottomBar = null,
    ) {
        MapPlaceholder(label = "mapa · rota em tempo real", height = 150.dp) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(RangoColors.Primary, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(RangoIcons.Bike, contentDescription = null, tint = Color.White, modifier = Modifier.size(27.dp))
            }
        }
    }
}

