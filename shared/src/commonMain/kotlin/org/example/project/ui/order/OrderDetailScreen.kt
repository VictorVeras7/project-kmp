package org.example.project.ui.order

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.example.project.data.OrderDetail
import org.example.project.data.OrderLine
import org.example.project.data.SampleOrderDetail
import org.example.project.data.formatMoney
import org.example.project.ui.checkout.CheckoutCard
import org.example.project.ui.checkout.CheckoutScaffold
import org.example.project.ui.checkout.Divider
import org.example.project.ui.checkout.PrimaryButton
import org.example.project.ui.checkout.icon
import org.example.project.ui.theme.RangoColors
import org.example.project.ui.theme.RangoIcons
import org.example.project.ui.theme.RangoTheme

@Composable
fun OrderDetailScreen(
    order: OrderDetail,
    onBack: () -> Unit,
    // Troca o carrinho pelos itens do pedido; o App abre o carrinho se der certo.
    onReorder: suspend () -> Unit,
    modifier: Modifier = Modifier,
) {
    var reordering by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    CheckoutScaffold(
        title = "Detalhes do pedido",
        subtitle = order.code,
        onBack = onBack,
        modifier = modifier,
        contentSpacing = 13.dp,
        trailing = { StatusChip(order.status, order.statusLabel) },
        bottomBar = {
            PrimaryButton(
                text = if (reordering) "Adicionando..." else "Pedir novamente",
                enabled = !reordering,
                onClick = {
                    scope.launch {
                        reordering = true
                        onReorder()
                        reordering = false
                    }
                },
                leading = { Icon(RangoIcons.Replay, contentDescription = null, modifier = Modifier.size(21.dp)) },
            )
        },
    ) {
        CheckoutCard(spacing = 12.dp) {
            Text("Produtos", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.5.sp, fontWeight = FontWeight.ExtraBold)
            order.items.forEach { ProductRow(it) }
        }

        CheckoutCard(spacing = 13.dp) {
            DetailInfoRow(RangoIcons.Pin, "Endereço de entrega", order.address)
            DetailInfoRow(order.payment?.icon ?: RangoIcons.Wallet, "Pagamento", order.paymentLabel)
            DetailInfoRow(RangoIcons.Clock, "Realizado em", order.date)
            DetailInfoRow(RangoIcons.Flag, "Status final", order.statusLabel)
        }

        CheckoutCard(spacing = 10.dp) {
            ValueRow("Subtotal", formatMoney(order.totals.subtotal))
            ValueRow("Taxa de entrega", if (order.totals.delivery == 0.0) "Grátis" else formatMoney(order.totals.delivery))
            if (order.totals.discount > 0.01) {
                val label = order.couponCode?.let { "Desconto · $it" } ?: "Desconto"
                ValueRow(label, "- ${formatMoney(order.totals.discount)}")
            }
            Divider()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Total pago", color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                Text(formatMoney(order.totals.total), color = RangoColors.Primary, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@Composable
private fun ProductRow(line: OrderLine) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(11.dp)) {
        Box(Modifier.size(38.dp).background(Color.hsv(line.hue, 0.3f, 0.97f), RoundedCornerShape(12.dp)))
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(1.dp)) {
            Text(
                "${line.quantity}× ${line.name}",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(line.restaurant, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp, fontWeight = FontWeight.Medium)
        }
        Text(formatMoney(line.total), color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

// Ícone laranja em quadrado cinza, rótulo em caixa alta e valor embaixo.
@Composable
private fun DetailInfoRow(icon: ImageVector, label: String, value: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(11.dp)) {
        Box(
            modifier = Modifier.size(34.dp).background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(11.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = RangoColors.Primary, modifier = Modifier.size(18.dp))
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(1.dp)) {
            Text(
                label.uppercase(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.4.sp,
            )
            Text(value, color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun ValueRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Text(value, color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}
