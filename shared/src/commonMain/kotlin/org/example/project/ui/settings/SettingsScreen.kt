package org.example.project.ui.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.data.ThemeMode
import org.example.project.ui.checkout.CheckoutScaffold

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
