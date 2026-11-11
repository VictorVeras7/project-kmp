package org.example.project.ui.order

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.data.Courier
import org.example.project.data.Order
import org.example.project.data.OrderStep
import org.example.project.data.SampleOrder
import org.example.project.ui.checkout.CheckoutCard
import org.example.project.ui.checkout.CheckoutScaffold
import org.example.project.ui.checkout.MapPlaceholder
import org.example.project.ui.checkout.SoftIcon
import org.example.project.ui.theme.RangoColors
import org.example.project.ui.theme.RangoIcons
import org.example.project.ui.theme.RangoTheme

private val CourierAvatarColor = Color(0x8CA9C4EC)

@Composable
fun OrderTrackingScreen(
    order: Order,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // O status é atualizado pelo App, que consulta o pedido no servidor enquanto ele está em andamento.
    CheckoutScaffold(
        title = "Acompanhar pedido",
        subtitle = order.code,
        onBack = onBack,
        modifier = modifier,
        bottomBar = null,
    ) {
        MapPlaceholder(label = "mapa · rota em tempo real", height = 150.dp) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .pulseRing()
                    .background(RangoColors.Primary, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(RangoIcons.Bike, contentDescription = null, tint = Color.White, modifier = Modifier.size(27.dp))
            }
        }
        StatusCard(order)
        Timeline(order)
        order.courier?.let { CourierCard(it) }
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
            Text(order.hint, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
        Text(order.eta, color = RangoColors.Primary, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
private fun Timeline(order: Order) {
    CheckoutCard(spacing = 0.dp) {
        OrderStep.entries.forEach { step ->
            TimelineRow(
                step = step,
                time = order.stepTimes[step].orEmpty(),
                done = step < order.step,
                isCurrent = step == order.step,
                hasLine = step != OrderStep.entries.last(),
            )
        }
    }
}

@Composable
private fun TimelineRow(step: OrderStep, time: String, done: Boolean, isCurrent: Boolean, hasLine: Boolean) {
    Row(
        modifier = Modifier.height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(13.dp),
    ) {
        Column(modifier = Modifier.width(26.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            val dotColor = when {
                done -> RangoColors.Success
                isCurrent -> RangoColors.Primary
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .then(if (isCurrent) Modifier.pulseRing() else Modifier)
                    .background(dotColor, CircleShape)
                    .then(
                        if (done || isCurrent) Modifier else Modifier.border(1.5.dp, MaterialTheme.colorScheme.outline, CircleShape),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    if (done) RangoIcons.Check else step.icon,
                    contentDescription = null,
                    tint = if (done || isCurrent) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(15.dp),
                )
            }
            if (hasLine) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .weight(1f)
                        .heightIn(min = 26.dp)
                        .background(if (done) RangoColors.Success else MaterialTheme.colorScheme.outline),
                )
            }
        }
        Column(
            modifier = Modifier.weight(1f).padding(bottom = if (hasLine) 16.dp else 0.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                step.label,
                color = when {
                    isCurrent -> RangoColors.Primary
                    done -> MaterialTheme.colorScheme.onSurface
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                },
                fontSize = 14.sp,
                fontWeight = if (isCurrent) FontWeight.ExtraBold else FontWeight.Bold,
            )
            Text(
                when {
                    done -> time
                    isCurrent -> "agora"
                    else -> "previsto"
                },
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

// Entregador vindo do servidor. Os botões de mensagem e ligar ainda não têm ação, como no protótipo.
@Composable
private fun CourierCard(courier: Courier) {
    val shape = RoundedCornerShape(20.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, shape)
            .border(1.dp, MaterialTheme.colorScheme.outline, shape)
            .padding(15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(13.dp),
    ) {
        Box(Modifier.size(44.dp).background(CourierAvatarColor, CircleShape))
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(1.dp)) {
            Text(courier.name, color = MaterialTheme.colorScheme.onSurface, fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
            Text(
                "Entregador · ${courier.vehicle}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
        CourierButton(RangoIcons.Chat, "Mensagem", filled = false)
        CourierButton(RangoIcons.Phone, "Ligar", filled = true)
    }
}

@Composable
private fun CourierButton(icon: ImageVector, description: String, filled: Boolean) {
    val shape = RoundedCornerShape(13.dp)
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(shape)
            .background(if (filled) RangoColors.Primary else MaterialTheme.colorScheme.surface, shape)
            .then(if (filled) Modifier else Modifier.border(1.dp, MaterialTheme.colorScheme.outline, shape))
            .clickable {},
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            icon,
            contentDescription = description,
            tint = if (filled) Color.White else RangoColors.Primary,
            modifier = Modifier.size(20.dp),
        )
    }
}

// Anel laranja que se expande e some, igual à animação "ring" do protótipo.
@Composable
private fun Modifier.pulseRing(): Modifier {
    val transition = rememberInfiniteTransition(label = "ring")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1900, easing = LinearOutSlowInEasing)),
        label = "ringProgress",
    )
    return drawBehind {
        drawCircle(
            color = RangoColors.Primary.copy(alpha = 0.4f * (1f - progress)),
            radius = size.minDimension / 2 + 16.dp.toPx() * progress,
        )
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

@Preview
@Composable
private fun OrderTrackingScreenPreview() {
    RangoTheme(darkTheme = false) {
        OrderTrackingScreen(order = SampleOrder, onBack = {})
    }
}

@Preview
@Composable
private fun OrderTrackingScreenDarkPreview() {
    RangoTheme(darkTheme = true) {
        OrderTrackingScreen(
            order = SampleOrder.copy(step = OrderStep.OnTheWay, hint = "O entregador está levando seu pedido"),
            onBack = {},
        )
    }
}
