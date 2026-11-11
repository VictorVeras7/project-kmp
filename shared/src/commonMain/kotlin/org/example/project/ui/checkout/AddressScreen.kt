package org.example.project.ui.checkout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.example.project.data.DeliveryAddress
import org.example.project.data.SampleAddresses
import org.example.project.data.AddressForm
import org.example.project.data.addressForm
import org.example.project.data.splitDistrict
import org.example.project.ui.theme.RangoColors
import org.example.project.ui.theme.RangoIcons
import org.example.project.ui.theme.RangoTheme

@Composable
fun AddressScreen(
    addresses: List<DeliveryAddress>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    // Salva no servidor; devolve false se falhou, para manter o formulário aberto.
    onSaveAddress: suspend (AddressForm) -> Boolean,
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
        MapPlaceholder(label = "mapa · localização", height = 132.dp) {
            Icon(RangoIcons.Pin, contentDescription = null, tint = RangoColors.Error, modifier = Modifier.size(34.dp))
        }
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
                onSave = { form ->
                    if (onSaveAddress(form)) showForm = false
                },
            )
        }
        AddAddressButton(onClick = { showForm = !showForm })
    }
}

@Composable
private fun NewAddressForm(onSave: suspend (AddressForm) -> Unit) {
    var cep by remember { mutableStateOf("") }
    var street by remember { mutableStateOf("") }
    var complement by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    // Só marca os campos obrigatórios depois de tentar salvar.
    var tried by remember { mutableStateOf(false) }
    var saving by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val cepError = tried && cep.count { it.isDigit() } != 8
    val streetError = tried && street.isBlank()
    val districtError = tried && splitDistrict(district) == null

    CheckoutCard(highlighted = true) {
        Text("Novo endereço", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.5.sp, fontWeight = FontWeight.ExtraBold)
        FormField(cep, { cep = it }, "CEP", keyboardType = KeyboardType.Number, error = cepError)
        FormField(street, { street = it }, "Rua e número", error = streetError)
        FormField(complement, { complement = it }, "Complemento")
        FormField(district, { district = it }, "Bairro, cidade", error = districtError)
        if (cepError || streetError || districtError) {
            Text(
                when {
                    cepError -> "Informe um CEP com 8 dígitos."
                    streetError -> "Preencha a rua e o número."
                    else -> "Informe bairro e cidade separados por vírgula."
                },
                color = RangoColors.Error,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Button(
            onClick = {
                tried = true
                val form = addressForm(cep, street, complement, district)
                if (form != null && !saving) {
                    scope.launch {
                        saving = true
                        onSave(form)
                        saving = false
                    }
                }
            },
            enabled = !saving,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(15.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                disabledContentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        ) {
            Text(if (saving) "Salvando..." else "Salvar endereço", fontSize = 14.sp, fontWeight = FontWeight.Bold)
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

@Preview
@Composable
private fun AddressScreenPreview() {
    RangoTheme(darkTheme = false) {
        AddressScreen(addresses = SampleAddresses, selectedIndex = 0, onSelect = {}, onSaveAddress = { true }, onBack = {})
    }
}

@Preview
@Composable
private fun AddressScreenDarkPreview() {
    RangoTheme(darkTheme = true) {
        AddressScreen(addresses = SampleAddresses, selectedIndex = 1, onSelect = {}, onSaveAddress = { true }, onBack = {})
    }
}
