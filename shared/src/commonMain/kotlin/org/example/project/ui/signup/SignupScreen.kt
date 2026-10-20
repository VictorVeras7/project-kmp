package org.example.project.ui.signup

import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.example.project.ui.components.AuthField
import org.example.project.ui.components.BackButton
import org.example.project.ui.components.RangoCheckbox

private val EmailPattern = Regex("^[^@\\s]+@[^@\\s]+\\.[a-z]{2,}$", RegexOption.IGNORE_CASE)

private enum class SignupField { Name, Email, Phone, Password, ConfirmPassword }

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
    var acceptedTerms by remember { mutableStateOf(false) }
    var touched by remember { mutableStateOf(false) }
    var termsError by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun validate(): Map<SignupField, String> = buildMap {
        if (name.trim().length < 3) put(SignupField.Name, "Informe seu nome completo.")
        if (!EmailPattern.matches(email)) put(SignupField.Email, "E-mail inválido.")
        if (phone.count { it.isDigit() } < 10) put(SignupField.Phone, "Telefone incompleto.")
        if (password.length < 6) put(SignupField.Password, "Mínimo de 6 caracteres.")
        if (confirmPassword.isEmpty() || confirmPassword != password) {
            put(SignupField.ConfirmPassword, "As senhas não coincidem.")
        }
    }

    val errors = if (touched) validate() else emptyMap()

    fun submit() {
        if (loading) return
        touched = true
        termsError = !acceptedTerms
        if (validate().isNotEmpty() || !acceptedTerms) return

        scope.launch {
            loading = true
            delay(1100)
            loading = false
            onSignupSuccess(name.trim(), email, phone)
        }
    }

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
                    error = errors[SignupField.Name].orEmpty(),
                    keyboardType = KeyboardType.Text,
                    valid = touched && SignupField.Name !in errors && name.isNotEmpty(),
                )
                AuthField(
                    label = "E-mail",
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "voce@email.com",
                    error = errors[SignupField.Email].orEmpty(),
                    keyboardType = KeyboardType.Email,
                    valid = touched && SignupField.Email !in errors && email.isNotEmpty(),
                )
                AuthField(
                    label = "Telefone",
                    value = phone,
                    onValueChange = { phone = it },
                    placeholder = "(11) 90000-0000",
                    error = errors[SignupField.Phone].orEmpty(),
                    keyboardType = KeyboardType.Phone,
                    valid = touched && SignupField.Phone !in errors && phone.isNotEmpty(),
                )
                AuthField(
                    label = "Senha",
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Mínimo 6 caracteres",
                    error = errors[SignupField.Password].orEmpty(),
                    keyboardType = KeyboardType.Password,
                    visualTransformation = PasswordVisualTransformation(),
                    valid = touched && SignupField.Password !in errors && password.isNotEmpty(),
                )
                AuthField(
                    label = "Confirmar senha",
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    placeholder = "Repita a senha",
                    error = errors[SignupField.ConfirmPassword].orEmpty(),
                    keyboardType = KeyboardType.Password,
                    visualTransformation = PasswordVisualTransformation(),
                    valid = touched && SignupField.ConfirmPassword !in errors && confirmPassword.isNotEmpty(),
                )
                TermsRow(
                    checked = acceptedTerms,
                    error = termsError && !acceptedTerms,
                    onToggle = {
                        acceptedTerms = !acceptedTerms
                        termsError = false
                    },
                )
            }
        }
    }
}

@Composable
private fun TermsRow(checked: Boolean, error: Boolean, onToggle: () -> Unit) {
    val primary = MaterialTheme.colorScheme.primary
    val highlight = SpanStyle(color = primary, fontWeight = FontWeight.Bold)
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.clickable(onClick = onToggle).padding(vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            RangoCheckbox(checked = checked)
            Text(
                text = buildAnnotatedString {
                    append("Li e aceito os ")
                    withStyle(highlight) { append("Termos de uso") }
                    append(" e a ")
                    withStyle(highlight) { append("Política de privacidade") }
                    append(".")
                },
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                fontWeight = FontWeight.Medium,
            )
        }
        if (error) {
            Text(
                text = "Aceite os termos para continuar.",
                color = MaterialTheme.colorScheme.error,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}
