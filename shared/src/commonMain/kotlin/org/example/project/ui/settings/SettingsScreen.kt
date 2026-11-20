package org.example.project.ui.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.data.ThemeMode
import org.example.project.ui.checkout.CheckoutScaffold
import org.example.project.ui.theme.RangoIcons
import org.example.project.ui.theme.RangoTheme

// Configurações: grupos Preferências, Conta, Dados e Sobre, na ordem do protótipo.
@Composable
fun SettingsScreen(
    theme: ThemeMode,
    onBack: () -> Unit,
    onTheme: () -> Unit,
    onNotifications: () -> Unit,
    onLanguage: () -> Unit,
    onPersonalData: () -> Unit,
    onAddresses: () -> Unit,
    onPayments: () -> Unit,
    onSync: () -> Unit,
    onClearLocalData: () -> Unit,
    onHelp: () -> Unit,
    onTerms: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CheckoutScaffold(
        title = "Configurações",
        onBack = onBack,
        bottomBar = null,
        modifier = modifier,
        contentSpacing = 18.dp,
    ) {
        SettingsGroup("Preferências") {
            SettingsRow(RangoIcons.DarkMode, "Tema", onTheme, first = true) {
                RowValue(theme.label)
                Chevron()
            }
            SettingsRow(RangoIcons.Bell, "Notificações", onNotifications, first = false) { Chevron() }
            SettingsRow(RangoIcons.Language, "Idioma", onLanguage, first = false) {
                RowValue("Português")
                Chevron()
            }
        }
        SettingsGroup("Conta") {
            SettingsRow(RangoIcons.User, "Dados pessoais", onPersonalData, first = true) { Chevron() }
            SettingsRow(RangoIcons.Pin, "Endereços", onAddresses, first = false) { Chevron() }
            SettingsRow(RangoIcons.Wallet, "Formas de pagamento", onPayments, first = false) { Chevron() }
        }
        SettingsGroup("Dados e conexão") {
            SettingsRow(RangoIcons.Sync, "Sincronizar agora", onSync, first = true, hint = "busca de novo pedidos, carrinho e favoritos") {
                Chevron()
            }
            SettingsRow(
                RangoIcons.Trash,
                "Limpar dados locais",
                onClearLocalData,
                first = false,
                hint = "sessão salva neste aparelho",
                danger = true,
            ) { Chevron() }
        }
        SettingsGroup("Sobre") {
            SettingsRow(RangoIcons.Help, "Central de ajuda", onHelp, first = true) { Chevron() }
            SettingsRow(RangoIcons.Description, "Termos e privacidade", onTerms, first = false) { Chevron() }
        }
        Text(
            "rango v1.0.0",
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun SettingsPreviewContent(theme: ThemeMode) {
    SettingsScreen(
        theme = theme,
        onBack = {},
        onTheme = {},
        onNotifications = {},
        onLanguage = {},
        onPersonalData = {},
        onAddresses = {},
        onPayments = {},
        onSync = {},
        onClearLocalData = {},
        onHelp = {},
        onTerms = {},
    )
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    RangoTheme(darkTheme = false) { SettingsPreviewContent(ThemeMode.Light) }
}

@Preview
@Composable
private fun SettingsScreenDarkPreview() {
    RangoTheme(darkTheme = true) { SettingsPreviewContent(ThemeMode.Dark) }
}
