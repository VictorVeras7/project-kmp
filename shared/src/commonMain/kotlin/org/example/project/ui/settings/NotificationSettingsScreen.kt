package org.example.project.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.example.project.data.NotificationPrefs
import org.example.project.ui.checkout.CheckoutScaffold
import org.example.project.ui.theme.RangoIcons
import org.example.project.ui.theme.RangoTheme

private class NotificationOption(
    val icon: ImageVector,
    val label: String,
    val hint: String,
    val checked: (NotificationPrefs) -> Boolean,
    val toggle: (NotificationPrefs) -> NotificationPrefs,
)

// Mesmas três opções, ícones e dicas do protótipo.
private val Options = listOf(
    NotificationOption(RangoIcons.Receipt, "Pedidos", "Confirmação, preparo e entrega", { it.orders }, { it.copy(orders = !it.orders) }),
    NotificationOption(RangoIcons.Tag, "Promoções", "Cupons e ofertas exclusivas", { it.promos }, { it.copy(promos = !it.promos) }),
    NotificationOption(RangoIcons.Campaign, "Novidades", "Novos restaurantes e recursos", { it.news }, { it.copy(news = !it.news) }),
)

@Composable
fun NotificationSettingsScreen(
    prefs: NotificationPrefs,
    onChange: (NotificationPrefs) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CheckoutScaffold(
        title = "Notificações",
        onBack = onBack,
        bottomBar = null,
        modifier = modifier,
        contentSpacing = 12.dp,
    ) {
        SettingsCard {
            Options.forEachIndexed { index, option ->
                SettingsRow(
                    icon = option.icon,
                    label = option.label,
                    onClick = { onChange(option.toggle(prefs)) },
                    first = index == 0,
                    hint = option.hint,
                    detailed = true,
                    role = Role.Switch,
                ) {
                    RangoSwitch(checked = option.checked(prefs))
                }
            }
        }
    }
}

@Preview
@Composable
private fun NotificationSettingsScreenPreview() {
    RangoTheme(darkTheme = false) {
        NotificationSettingsScreen(prefs = NotificationPrefs(), onChange = {}, onBack = {})
    }
}

@Preview
@Composable
private fun NotificationSettingsScreenDarkPreview() {
    RangoTheme(darkTheme = true) {
        NotificationSettingsScreen(prefs = NotificationPrefs(promos = false, news = true), onChange = {}, onBack = {})
    }
}
