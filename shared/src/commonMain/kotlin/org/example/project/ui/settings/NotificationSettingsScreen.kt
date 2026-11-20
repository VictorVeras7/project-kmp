package org.example.project.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import org.example.project.data.NotificationPrefs
import org.example.project.ui.checkout.CheckoutScaffold

private class NotificationOption(
    val icon: ImageVector,
    val label: String,
    val hint: String,
    val checked: (NotificationPrefs) -> Boolean,
    val toggle: (NotificationPrefs) -> NotificationPrefs,
)

private val Options = emptyList<NotificationOption>()

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
