package org.example.project.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.data.ThemeMode
import org.example.project.ui.checkout.CheckoutScaffold
import org.example.project.ui.checkout.RadioDot
import org.example.project.ui.theme.RangoColors
import org.example.project.ui.theme.RangoIcons

private class ThemeOption(val mode: ThemeMode, val label: String, val hint: String, val icon: ImageVector)

// Ordem, textos e ícones do protótipo.
private val Options = listOf(
    ThemeOption(ThemeMode.Light, "Claro", "Fundo branco, laranja de destaque", RangoIcons.LightMode),
    ThemeOption(ThemeMode.Dark, "Escuro", "Fundo escuro, laranja preservado", RangoIcons.DarkMode),
    ThemeOption(ThemeMode.System, "Usar configuração do sistema", "Segue o tema do smartphone", RangoIcons.BrightnessAuto),
)

// Cores da miniatura de cada tema; fixas, porque mostram o tema e não seguem o atual.
private class ThemePreview(val name: String, val dark: Boolean, val bg: Color, val sub: Color, val line: Color, val text: Color)

private val Previews = listOf(
    ThemePreview("Claro", false, Color.White, Color(0xFFF7F7F7), Color(0xFFE8E8E8), Color(0xFF1F1F1F)),
    ThemePreview("Escuro", true, Color(0xFF17181B), Color(0xFF1E2024), Color(0xFF2A2C31), Color(0xFFF4F4F5)),
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
        Row(horizontalArrangement = Arrangement.spacedBy(11.dp)) {
            Previews.forEach { preview ->
                ThemePreviewCard(preview, selected = preview.dark == showsDark, modifier = Modifier.weight(1f))
            }
        }
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

// Miniatura de uma tela: barra laranja, card, linha de texto e botão.
@Composable
private fun ThemePreviewCard(preview: ThemePreview, selected: Boolean, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(18.dp)
    Column(
        modifier = modifier
            .background(preview.bg, shape)
            .border(2.dp, if (selected) RangoColors.Primary else MaterialTheme.colorScheme.outline, shape)
            .padding(13.dp),
        verticalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        Box(Modifier.fillMaxWidth(0.6f).height(8.dp).background(RangoColors.Primary, RoundedCornerShape(4.dp)))
        Box(
            Modifier
                .fillMaxWidth()
                .height(34.dp)
                .background(preview.sub, RoundedCornerShape(10.dp))
                .border(1.dp, preview.line, RoundedCornerShape(10.dp)),
        )
        Box(Modifier.fillMaxWidth(0.75f).height(8.dp).background(preview.line, RoundedCornerShape(4.dp)))
        Box(Modifier.fillMaxWidth().height(22.dp).background(RangoColors.Primary, RoundedCornerShape(8.dp)))
        Text(preview.name, color = preview.text, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
    }
}
