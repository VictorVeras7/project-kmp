package org.example.project.ui.settings

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import org.example.project.data.ThemeMode
import org.example.project.ui.checkout.CheckoutScaffold
import org.example.project.ui.checkout.RadioDot
import org.example.project.ui.theme.RangoIcons

private class ThemeOption(val mode: ThemeMode, val label: String, val hint: String, val icon: ImageVector)

// Ordem, textos e ícones do protótipo.
private val Options = listOf(
    ThemeOption(ThemeMode.Light, "Claro", "Fundo branco, laranja de destaque", RangoIcons.LightMode),
    ThemeOption(ThemeMode.Dark, "Escuro", "Fundo escuro, laranja preservado", RangoIcons.DarkMode),
    ThemeOption(ThemeMode.System, "Usar configuração do sistema", "Segue o tema do smartphone", RangoIcons.BrightnessAuto),
)

@Composable
fun ThemeScreen(
    theme: ThemeMode,
    onSelect: (ThemeMode) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Em "Sistema", a miniatura marcada é a do tema que o aparelho está usando.
    val showsDark = when (theme) {
        ThemeMode.Light -> false
        ThemeMode.Dark -> true
        ThemeMode.System -> isSystemInDarkTheme()
    }
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
