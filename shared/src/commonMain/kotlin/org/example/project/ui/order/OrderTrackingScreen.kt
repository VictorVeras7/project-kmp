package org.example.project.ui.order

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.example.project.data.Order
import org.example.project.data.OrderStep
import org.example.project.data.TrackingIntervalMs
import org.example.project.ui.checkout.CheckoutScaffold
import org.example.project.ui.checkout.MapPlaceholder
import org.example.project.ui.checkout.SoftIcon
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
        StatusCard(order)
    }
}

@Composable
private fun StatusCard(order: Order) {
    val shape = RoundedCornerShape(20.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(3.dp, shape)
            .background(MaterialTheme.colorScheme.surface, shape)
            .border(1.dp, MaterialTheme.colorScheme.outline, shape)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(13.dp),
    ) {
        SoftIcon(order.step.icon, size = 46.dp, iconSize = 24.dp)
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(order.step.label, color = MaterialTheme.colorScheme.onSurface, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
            Text(order.step.hint, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
        Text(order.eta, color = RangoColors.Primary, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
    }
}


private val OrderStep.icon: ImageVector
    get() = when (this) {
        OrderStep.Received -> RangoIcons.Receipt
        OrderStep.Confirmed -> RangoIcons.TaskAlt
        OrderStep.Preparing -> RangoIcons.Skillet
        OrderStep.OnTheWay -> RangoIcons.Bike
        OrderStep.Delivered -> RangoIcons.Home
    }
