package org.example.project.ui.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun LoginScreen(
    onBack: () -> Unit,
    onLoginSuccess: (keepConnected: Boolean) -> Unit,
    onForgotPassword: () -> Unit,
    onSignup: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
        Column(modifier = Modifier.padding(start = 22.dp, end = 22.dp, top = 20.dp, bottom = 24.dp)) {
            Text(
                text = "Bem-vinda de volta",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 27.sp,
                fontWeight = FontWeight.ExtraBold,
            )
            Spacer(Modifier.height(7.dp))
            Text(
                text = "Entre para continuar seus pedidos.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}
