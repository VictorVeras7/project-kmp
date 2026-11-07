package org.example.project.ui.checkout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.data.PaymentMethod
import org.example.project.ui.theme.RangoColors
import org.example.project.ui.theme.RangoIcons
import org.example.project.ui.theme.RangoTheme

@Composable
fun PaymentScreen(
    selected: PaymentMethod,
    onSelect: (PaymentMethod) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CheckoutScaffold(
        title = "Forma de pagamento",
        onBack = onBack,
        modifier = modifier,
        contentSpacing = 11.dp,
        bottomBar = { PrimaryButton("Confirmar forma de pagamento", onClick = onBack) },
    ) {
        PaymentMethod.entries.forEach { method ->
            SelectableRow(selected = method == selected, onClick = { onSelect(method) }) {
                SoftIcon(method.icon, size = 42.dp, iconSize = 22.dp)
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(method.label, color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(
                        method.hint,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
                RadioDot(selected = method == selected)
            }
        }
        if (selected.needsCard) CardForm()
    }
}

@Composable
private fun CardForm() {
    var number by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var expiry by remember { mutableStateOf("") }

    CheckoutCard {
        Text("Dados do cartão", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
        FormField(number, { number = it }, "Número do cartão", icon = RangoIcons.CreditCard, keyboardType = KeyboardType.Number)
        FormField(name, { name = it }, "Nome impresso no cartão", icon = RangoIcons.User)
        FormField(expiry, { expiry = it }, "Validade e CVV", icon = RangoIcons.Calendar, keyboardType = KeyboardType.Number)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(RangoIcons.Lock, contentDescription = null, tint = RangoColors.Success, modifier = Modifier.size(17.dp))
            Text(
                "dados enviados por conexão segura",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
            )
        }
    }
}

@Preview
@Composable
private fun PaymentScreenPreview() {
    RangoTheme(darkTheme = false) {
        PaymentScreen(selected = PaymentMethod.Pix, onSelect = {}, onBack = {})
    }
}

@Preview
@Composable
private fun PaymentScreenDarkPreview() {
    RangoTheme(darkTheme = true) {
        PaymentScreen(selected = PaymentMethod.Credit, onSelect = {}, onBack = {})
    }
}
