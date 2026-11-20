package org.example.project.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import org.example.project.data.ThemeMode
import org.example.project.ui.checkout.CheckoutScaffold
import org.example.project.ui.checkout.RadioDot

private class ThemeOption(val mode: ThemeMode, val label: String, val hint: String, val icon: ImageVector)

private val Options = emptyList<ThemeOption>()

@Composable
fun ThemeScreen(
    theme: ThemeMode,
    onSelect: (ThemeMode) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CheckoutScaffold(
        title = "Tema",
        onBack = onBack,
        bottomBar = null,
        modifier = modifier,
        contentSpacing = 16.dp,
    ) {
        SettingsCard {
            Options.forEachIndexed { index, option ->
                SettingsRow(
                    icon = option.icon,
                    label = option.label,
                    onClick = { onSelect(option.mode) },
                    first = index == 0,
                    hint = option.hint,
                    detailed = true,
                    role = Role.RadioButton,
                ) {
                    RadioDot(selected = option.mode == theme)
                }
            }
        }
    }
}
