package org.example.project.ui.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
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
import kotlinx.coroutines.delay
import org.example.project.data.Addon
import org.example.project.data.CartItem
import org.example.project.data.CouponCode
import org.example.project.data.DeliveryAddress
import org.example.project.data.PaymentMethod
import org.example.project.data.Product
import org.example.project.data.SampleAddons
import org.example.project.data.SampleAddresses
import org.example.project.data.SampleProducts
import org.example.project.data.add
import org.example.project.data.formatMoney
import org.example.project.data.totals
import org.example.project.ui.theme.RangoColors
import org.example.project.ui.theme.RangoIcons
import org.example.project.ui.theme.RangoTheme

// Tempo simulado de processamento do pagamento, como no protótipo.
private const val ConfirmDelayMs = 1300L

@Composable
fun CheckoutScreen(
    items: List<CartItem>,
    couponApplied: Boolean,
    address: DeliveryAddress,
    payment: PaymentMethod,
    onBack: () -> Unit,
    onChangeAddress: () -> Unit,
    onChangePayment: () -> Unit,
    onOrderConfirmed: () -> Unit,
    modifier: Modifier = Modifier,
    products: List<Product> = SampleProducts,
    addons: List<Addon> = SampleAddons,
) {
    val totals = items.totals(couponApplied)
    var processing by remember { mutableStateOf(false) }
    val confirm by rememberUpdatedState(onOrderConfirmed)

    LaunchedEffect(processing) {
        if (processing) {
            delay(ConfirmDelayMs)
            confirm()
        }
    }

    CheckoutScaffold(
        title = "Finalizar pedido",
        onBack = { if (!processing) onBack() },
        modifier = modifier,
        bottomBar = {
            PrimaryButton(
                text = if (processing) "Processando pagamento..." else "Confirmar pedido",
                onClick = { processing = true },
                enabled = !processing,
                leading = if (processing) {
                    {
                        CircularProgressIndicator(
                            color = Color.White,
                            trackColor = Color.White.copy(alpha = 0.35f),
                            strokeWidth = 2.5.dp,
                            modifier = Modifier.size(19.dp),
                        )
                    }
                } else {
                    null
                },
            )
        },
    ) {
        CheckoutCard {
            CardTitleRow(RangoIcons.Pin, "Endereço de entrega", onChange = onChangeAddress)
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(address.label, color = RangoColors.Primary, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                Text(address.line, color = MaterialTheme.colorScheme.onSurface, fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
                Text(address.city, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
        }

        CheckoutCard {
            CardTitleRow(RangoIcons.Wallet, "Forma de pagamento", onChange = onChangePayment)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(11.dp)) {
                SoftIcon(payment.icon)
                Column {
                    Text(payment.label, color = MaterialTheme.colorScheme.onSurface, fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
                    Text(
                        payment.hint,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }

        CheckoutCard(spacing = 12.dp) {
            Text("Resumo do pedido", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.5.sp, fontWeight = FontWeight.ExtraBold)
            items.forEach { item ->
                val product = products.firstOrNull { it.id == item.productId } ?: return@forEach
                OrderLine(
                    item = item,
                    product = product,
                    addonNames = addons.filter { it.id in item.addonIds }.joinToString(" · ") { it.name },
                )
            }
            Divider()
            SummaryRow("Subtotal", formatMoney(totals.subtotal), MaterialTheme.colorScheme.onSurface)
            SummaryRow(
                "Taxa de entrega",
                if (totals.delivery == 0.0) "Grátis" else formatMoney(totals.delivery),
                if (totals.delivery == 0.0) RangoColors.Success else MaterialTheme.colorScheme.onSurface,
            )
            if (totals.discount > 0.0) {
                SummaryRow("Desconto · $CouponCode", "- ${formatMoney(totals.discount)}", RangoColors.Success)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Total", color = MaterialTheme.colorScheme.onSurface, fontSize = 14.5.sp, fontWeight = FontWeight.ExtraBold)
                Text(formatMoney(totals.total), color = RangoColors.Primary, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@Composable
private fun CardTitleRow(icon: ImageVector, title: String, onChange: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            Icon(icon, contentDescription = null, tint = RangoColors.Primary, modifier = Modifier.size(19.dp))
            Text(title, color = MaterialTheme.colorScheme.onSurface, fontSize = 13.5.sp, fontWeight = FontWeight.ExtraBold)
        }
        Text(
            "Alterar",
            color = RangoColors.Primary,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable(onClick = onChange),
        )
    }
}

@Composable
private fun OrderLine(item: CartItem, product: Product, addonNames: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(11.dp)) {
        Box(
            Modifier
                .size(36.dp)
                .background(Color.hsv(product.hue, 0.3f, 0.97f), RoundedCornerShape(11.dp)),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "${item.quantity}× ${product.name}",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (addonNames.isNotEmpty()) {
                Text(
                    "+ $addonNames",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
        Text(
            formatMoney(item.unitPrice * item.quantity),
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun SummaryRow(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Text(value, color = valueColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

private val PreviewCart = emptyList<CartItem>()
    .add(SampleProducts[0], 1, setOf("a1"))
    .add(SampleProducts[11], 2)

@Preview
@Composable
private fun CheckoutScreenPreview() {
    RangoTheme(darkTheme = false) {
        CheckoutScreen(
            items = PreviewCart,
            couponApplied = true,
            address = SampleAddresses.first(),
            payment = PaymentMethod.Pix,
            onBack = {},
            onChangeAddress = {},
            onChangePayment = {},
            onOrderConfirmed = {},
        )
    }
}

@Preview
@Composable
private fun CheckoutScreenDarkPreview() {
    RangoTheme(darkTheme = true) {
        CheckoutScreen(
            items = PreviewCart,
            couponApplied = false,
            address = SampleAddresses[1],
            payment = PaymentMethod.Credit,
            onBack = {},
            onChangeAddress = {},
            onChangePayment = {},
            onOrderConfirmed = {},
        )
    }
}
