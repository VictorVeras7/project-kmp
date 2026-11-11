package org.example.project.ui.order

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.data.Order
import org.example.project.data.SampleOrder
import org.example.project.data.formatMoney
import org.example.project.ui.checkout.Divider
import org.example.project.ui.checkout.PrimaryButton
import org.example.project.ui.theme.RangoColors
import org.example.project.ui.theme.RangoIcons
import org.example.project.ui.theme.RangoTheme

@Composable
fun OrderPlacedScreen(
    order: Order,
    onTrackOrder: () -> Unit,
    onBackHome: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(start = 22.dp, end = 22.dp, top = 24.dp, bottom = 22.dp),
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                SuccessBadge(modifier = Modifier.padding(top = 22.dp))
                Text(
                    "Pedido realizado\ncom sucesso!",
                    modifier = Modifier.padding(top = 20.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 24.sp,
                    lineHeight = 29.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.8).sp,
                    textAlign = TextAlign.Center,
                )
                Text(
                    "Você receberá notificações sobre cada etapa do preparo e da entrega.",
                    modifier = Modifier.padding(top = 9.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.5.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                )
                OrderSummary(order, modifier = Modifier.padding(top = 22.dp))
            }
            Column(
                modifier = Modifier.padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                PrimaryButton(
                    text = "Acompanhar pedido",
                    onClick = onTrackOrder,
                    leading = {
                        Icon(RangoIcons.Bike, contentDescription = null, modifier = Modifier.size(21.dp))
                    },
                )
                OutlinedButton(
                    onClick = onBackHome,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface),
                ) {
                    Text("Voltar ao início", fontSize = 14.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// Círculo verde com o check, entrando com o efeito "pop" do protótipo.
@Composable
private fun SuccessBadge(modifier: Modifier = Modifier) {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = keyframes {
                durationMillis = 500
                0.75f at 325
            },
        )
    }
    Box(
        modifier = modifier
            .size(104.dp)
            .graphicsLayer {
                // 0 -> 0,3x | 0,75 -> 1,12x | 1 -> 1x
                val p = progress.value
                val scale = if (p < 0.75f) 0.3f + p / 0.75f * 0.82f else 1.12f - (p - 0.75f) / 0.25f * 0.12f
                scaleX = scale
                scaleY = scale
                alpha = (p / 0.75f).coerceAtMost(1f)
            }
            .background(RangoColors.Success.copy(alpha = 0.12f), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier.size(48.dp).background(RangoColors.Success, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(RangoIcons.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
private fun OrderSummary(order: Order, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(20.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, shape)
            .border(1.dp, MaterialTheme.colorScheme.outline, shape)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        InfoRow("Número do pedido") {
            Text(
                order.code,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
            )
        }
        InfoRow("Previsão de entrega") {
            Text(order.eta, color = RangoColors.Primary, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
        }
        InfoRow("Pagamento") {
            Text(order.paymentLabel, color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        Divider()
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(order.itemsLabel, color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
            Text(formatMoney(order.total), color = RangoColors.Primary, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun InfoRow(label: String, value: @Composable () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        value()
    }
}

@Preview
@Composable
private fun OrderPlacedScreenPreview() {
    RangoTheme(darkTheme = false) {
        OrderPlacedScreen(order = SampleOrder, onTrackOrder = {}, onBackHome = {})
    }
}

@Preview
@Composable
private fun OrderPlacedScreenDarkPreview() {
    RangoTheme(darkTheme = true) {
        OrderPlacedScreen(order = SampleOrder.copy(paymentLabel = "Cartão de crédito"), onTrackOrder = {}, onBackHome = {})
    }
}
