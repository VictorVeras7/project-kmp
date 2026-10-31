package org.example.project.ui.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.data.Addon
import org.example.project.data.CartItem
import org.example.project.data.CouponCode
import org.example.project.data.Product
import org.example.project.data.SampleAddons
import org.example.project.data.SampleProducts
import org.example.project.data.formatMoney
import org.example.project.data.itemCount
import org.example.project.data.totals
import org.example.project.ui.theme.RangoColors
import org.example.project.ui.theme.RangoIcons

@Composable
fun CartScreen(
    items: List<CartItem>,
    coupon: String,
    couponApplied: Boolean,
    onBack: () -> Unit,
    onClear: () -> Unit,
    onChangeQuantity: (key: String, delta: Int) -> Unit,
    onRemove: (key: String) -> Unit,
    onOpenProduct: (String) -> Unit,
    onCouponChange: (String) -> Unit,
    onApplyCoupon: () -> Unit,
    onExplore: () -> Unit,
    modifier: Modifier = Modifier,
    products: List<Product> = SampleProducts,
    addons: List<Addon> = SampleAddons,
) {
    val totals = items.totals(couponApplied)
    val count = items.itemCount()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .windowInsetsPadding(WindowInsets.safeDrawing),
    ) {
        CartHeader(
            summary = if (count == 0) "nenhum item" else "${itemsLabel(count)} · ${formatMoney(totals.subtotal)}",
            showClear = items.isNotEmpty(),
            onBack = onBack,
            onClear = onClear,
        )
        if (items.isEmpty()) {
            EmptyCart(onExplore = onExplore, modifier = Modifier.weight(1f))
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(start = 18.dp, end = 18.dp, top = 16.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                items.forEach { item ->
                    val product = products.firstOrNull { it.id == item.productId } ?: return@forEach
                    CartLine(
                        item = item,
                        product = product,
                        addonNames = addons.filter { it.id in item.addonIds }.joinToString(" · ") { it.name },
                        onOpen = { onOpenProduct(product.id) },
                        onMinus = { onChangeQuantity(item.key, -1) },
                        onPlus = { onChangeQuantity(item.key, 1) },
                        onRemove = { onRemove(item.key) },
                    )
                }
                CouponRow(
                    value = coupon,
                    applied = couponApplied,
                    onValueChange = onCouponChange,
                    onApply = onApplyCoupon,
                )
            }
        }
    }
}

private fun itemsLabel(count: Int) = if (count == 1) "1 item" else "$count itens"

@Composable
private fun CartHeader(summary: String, showClear: Boolean, onBack: () -> Unit, onClear: () -> Unit) {
    Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 18.dp, end = 18.dp, top = 6.dp, bottom = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            val shape = RoundedCornerShape(13.dp)
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(shape)
                    .background(MaterialTheme.colorScheme.surface, shape)
                    .border(1.dp, MaterialTheme.colorScheme.outline, shape)
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    RangoIcons.ArrowBack,
                    contentDescription = "Voltar",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(21.dp),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Carrinho", color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                Text(
                    summary,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            if (showClear) {
                Text(
                    "Limpar",
                    color = RangoColors.Error,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable(onClick = onClear),
                )
            }
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.outline))
    }
}

@Composable
private fun EmptyCart(onExplore: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterVertically),
    ) {
        val borderColor = MaterialTheme.colorScheme.outline
        Box(
            modifier = Modifier
                .size(132.dp)
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(44.dp))
                .drawBehind {
                    drawRoundRect(
                        color = borderColor,
                        cornerRadius = CornerRadius(44.dp.toPx()),
                        style = Stroke(
                            width = 1.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 4.dp.toPx())),
                        ),
                    )
                },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                RangoIcons.CartOff,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(58.dp),
            )
        }
        Text(
            "Seu carrinho está vazio",
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 19.sp,
            fontWeight = FontWeight.ExtraBold,
        )
        Text(
            "Explore os restaurantes e adicione seus pratos favoritos.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 13.5.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 250.dp),
        )
        Button(
            onClick = onExplore,
            modifier = Modifier.padding(top = 6.dp).height(50.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        ) {
            Text("Explorar produtos", fontSize = 14.5.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun CartLine(
    item: CartItem,
    product: Product,
    addonNames: String,
    onOpen: () -> Unit,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
    onRemove: () -> Unit,
) {
    val shape = RoundedCornerShape(20.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, shape)
            .background(MaterialTheme.colorScheme.surface, shape)
            .border(1.dp, MaterialTheme.colorScheme.outline, shape)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        val imageShape = RoundedCornerShape(16.dp)
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(imageShape)
                .background(Color.hsv(product.hue, 0.3f, 0.97f), imageShape)
                .clickable(onClick = onOpen),
        )
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Text(
                    product.name,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Icon(
                    RangoIcons.Trash,
                    contentDescription = "Remover",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(19.dp).clickable(onClick = onRemove),
                )
            }
            Text(
                "${formatMoney(item.unitPrice)} · un.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
            )
            if (addonNames.isNotEmpty()) {
                Text("+ $addonNames", color = RangoColors.Primary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
            if (item.notes.isNotEmpty()) {
                Text(
                    "“${item.notes}”",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Medium,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LineStepper(item.quantity, onMinus, onPlus)
                Text(
                    formatMoney(item.unitPrice * item.quantity),
                    color = RangoColors.Primary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
        }
    }
}

@Composable
private fun LineStepper(quantity: Int, onMinus: () -> Unit, onPlus: () -> Unit) {
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceVariant, shape)
            .border(1.dp, MaterialTheme.colorScheme.outline, shape)
            .padding(3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        LineStepperButton(RangoIcons.Minus, "Diminuir", onMinus)
        Text(
            quantity.toString(),
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 13.5.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(min = 22.dp),
        )
        LineStepperButton(RangoIcons.Plus, "Aumentar", onPlus)
    }
}

@Composable
private fun LineStepperButton(icon: ImageVector, description: String, onClick: () -> Unit) {
    val shape = RoundedCornerShape(9.dp)
    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface, shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = description, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(17.dp))
    }
}

@Composable
private fun CouponRow(
    value: String,
    applied: Boolean,
    onValueChange: (String) -> Unit,
    onApply: () -> Unit,
) {
    val shape = RoundedCornerShape(15.dp)
    val borderColor = if (applied) RangoColors.Success else MaterialTheme.colorScheme.outline
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, shape)
                    .border(1.5.dp, borderColor, shape)
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(9.dp),
            ) {
                Icon(
                    RangoIcons.Ticket,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(19.dp),
                )
                BasicTextField(
                    value = value,
                    onValueChange = { onValueChange(it.uppercase()) },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Characters,
                        imeAction = ImeAction.Done,
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    decorationBox = { inner ->
                        Box {
                            if (value.isEmpty()) {
                                Text(
                                    "Cupom promocional",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                            inner()
                        }
                    },
                )
                if (applied) {
                    Box(
                        modifier = Modifier.size(19.dp).background(RangoColors.Success, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(RangoIcons.Check, contentDescription = "Cupom aplicado", tint = Color.White, modifier = Modifier.size(12.dp))
                    }
                }
            }
            Box(
                modifier = Modifier
                    .height(50.dp)
                    .clip(shape)
                    .border(1.5.dp, RangoColors.Primary, shape)
                    .clickable(onClick = onApply)
                    .padding(horizontal = 18.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text("Aplicar", color = RangoColors.Primary, fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
            }
        }
        if (applied) {
            Text(
                "Cupom $CouponCode aplicado · 10% off.",
                color = RangoColors.Success,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 4.dp),
            )
        }
    }
}
