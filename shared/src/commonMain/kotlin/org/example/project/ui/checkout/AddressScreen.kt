package org.example.project.ui.checkout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.data.DeliveryAddress
import org.example.project.ui.theme.RangoColors
import org.example.project.ui.theme.RangoIcons

@Composable
fun AddressScreen(
    addresses: List<DeliveryAddress>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CheckoutScaffold(
        title = "Endereço de entrega",
        onBack = onBack,
        modifier = modifier,
        contentSpacing = 12.dp,
        bottomBar = { PrimaryButton("Usar este endereço", onClick = onBack) },
    ) {
        addresses.forEachIndexed { index, address ->
            SelectableRow(selected = index == selectedIndex, onClick = { onSelect(index) }) {
                RadioDot(selected = index == selectedIndex)
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(address.label, color = RangoColors.Primary, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                    Text(address.line, color = MaterialTheme.colorScheme.onSurface, fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
                    Text(
                        address.city,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
                Icon(
                    RangoIcons.Edit,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(19.dp),
                )
            }
        }
        AddAddressButton(onClick = {})
    }
}

@Composable
private fun AddAddressButton(onClick: () -> Unit) {
    val borderColor = MaterialTheme.colorScheme.outline
    val shape = RoundedCornerShape(16.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(shape)
            .drawBehind {
                drawRoundRect(
                    color = borderColor,
                    cornerRadius = CornerRadius(16.dp.toPx()),
                    style = Stroke(
                        width = 1.5.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 4.dp.toPx())),
                    ),
                )
            }
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(RangoIcons.PinPlus, contentDescription = null, tint = RangoColors.Primary, modifier = Modifier.size(20.dp))
        Text(
            "Cadastrar novo endereço",
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 13.5.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}
