package org.example.project.ui.signup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.ui.components.AuthField
import org.example.project.ui.components.BackButton

@Composable
fun SignupScreen(
    onBack: () -> Unit,
    onSignupSuccess: (name: String, email: String, phone: String) -> Unit,
    onLogin: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
        Column(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Vertical))
                .imePadding(),
        ) {
            Row(
                modifier = Modifier.padding(start = 22.dp, end = 22.dp, top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                BackButton(onClick = onBack)
                Text(
                    text = "Criar conta",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(start = 22.dp, end = 22.dp, top = 18.dp, bottom = 26.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                AuthField(
                    label = "Nome completo",
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "Maria Silva",
                    error = "",
                    keyboardType = KeyboardType.Text,
                )
                AuthField(
                    label = "E-mail",
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "voce@email.com",
                    error = "",
                    keyboardType = KeyboardType.Email,
                )
                AuthField(
                    label = "Telefone",
                    value = phone,
                    onValueChange = { phone = it },
                    placeholder = "(11) 90000-0000",
                    error = "",
                    keyboardType = KeyboardType.Phone,
                )
                AuthField(
                    label = "Senha",
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Mínimo 6 caracteres",
                    error = "",
                    keyboardType = KeyboardType.Password,
                    visualTransformation = PasswordVisualTransformation(),
                )
                AuthField(
                    label = "Confirmar senha",
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    placeholder = "Repita a senha",
                    error = "",
                    keyboardType = KeyboardType.Password,
                    visualTransformation = PasswordVisualTransformation(),
                )
            }
        }
    }
}
