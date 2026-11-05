package org.example.project.ui.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.data.DeliveryAddress
import org.example.project.data.newAddress
import org.example.project.ui.theme.RangoColors
import org.example.project.ui.theme.RangoIcons

private val MapColor = Color(0xFFCFE3D8)

@Composable
fun AddressScreen(
    addresses: List<DeliveryAddress>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    onSaveAddress: (DeliveryAddress) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showForm by remember { mutableStateOf(false) }

    CheckoutScaffold(
        title = "Endereço de entrega",
        onBack = onBack,
        modifier = modifier,
        contentSpacing = 12.dp,
        bottomBar = { PrimaryButton("Usar este endereço", onClick = onBack) },
    ) {
        MapPlaceholder()
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
        if (showForm) {
            NewAddressForm(
                onSave = { address ->
                    onSaveAddress(address)
                    showForm = false
                },
            )
        }
        AddAddressButton(onClick = { showForm = !showForm })
    }
}

@Composable
private fun MapPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(132.dp)
            .background(MapColor, RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(RangoIcons.Pin, contentDescription = null, tint = RangoColors.Error, modifier = Modifier.size(34.dp))
        Text(
            "mapa · localização",
            color = Color.Black.copy(alpha = 0.5f),
            fontSize = 9.5.sp,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 12.dp, bottom = 10.dp)
                .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(6.dp))
                .padding(horizontal = 7.dp, vertical = 3.dp),
        )
    }
}

@Composable
private fun NewAddressForm(onSave: (DeliveryAddress) -> Unit) {
    var cep by remember { mutableStateOf("") }
    var street by remember { mutableStateOf("") }
    var complement by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    // Só marca os campos obrigatórios depois de tentar salvar.
    var tried by remember { mutableStateOf(false) }

    CheckoutCard(highlighted = true) {
        Text("Novo endereço", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.5.sp, fontWeight = FontWeight.ExtraBold)
        FormField(cep, { cep = it }, "CEP", keyboardType = KeyboardType.Number)
        FormField(street, { street = it }, "Rua e número", error = tried && street.isBlank())
        FormField(complement, { complement = it }, "Complemento")
        FormField(district, { district = it }, "Bairro e cidade", error = tried && district.isBlank())
        if (tried && (street.isBlank() || district.isBlank())) {
            Text(
                "Preencha a rua e o bairro.",
                color = RangoColors.Error,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Button(
            onClick = {
                tried = true
                newAddress(street, complement, district)?.let(onSave)
            },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(15.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        ) {
            Text("Salvar endereço", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
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
