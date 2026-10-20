package org.example.project.ui.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.example.project.ui.theme.RangoColors

private const val DemoPassword = "rango123"
private val EmailPattern = Regex("^[^@\\s]+@[^@\\s]+\\.[a-z]{2,}$", RegexOption.IGNORE_CASE)

private enum class LoginStatus { Idle, Loading, Success }

@Composable
fun LoginScreen(
    onBack: () -> Unit,
    onLoginSuccess: (keepConnected: Boolean) -> Unit,
    onForgotPassword: () -> Unit,
    onSignup: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var keepConnected by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var status by remember { mutableStateOf(LoginStatus.Idle) }
    val scope = rememberCoroutineScope()

    fun submit() {
        if (status != LoginStatus.Idle) return
        emailError = when {
            email.isBlank() -> "Informe seu e-mail."
            !EmailPattern.matches(email) -> "E-mail inválido."
            else -> ""
        }
        passwordError = if (password.isEmpty()) "Informe sua senha." else ""
        if (emailError.isNotEmpty() || passwordError.isNotEmpty()) return

        scope.launch {
            status = LoginStatus.Loading
            delay(1000)
            if (password != DemoPassword) {
                status = LoginStatus.Idle
                passwordError = "Senha incorreta. Tente novamente."
                return@launch
            }
            status = LoginStatus.Success
            delay(700)
            onLoginSuccess(keepConnected)
            password = ""
            status = LoginStatus.Idle
        }
    }

    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
        Column(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Vertical))
                .imePadding(),
        ) {
            BackButton(onClick = onBack, modifier = Modifier.padding(start = 22.dp, top = 8.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(start = 22.dp, end = 22.dp, top = 20.dp, bottom = 24.dp),
            ) {
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
                Spacer(Modifier.height(24.dp))
                Column(verticalArrangement = Arrangement.spacedBy(15.dp)) {
                    LoginField(
                        label = "E-mail",
                        value = email,
                        onValueChange = { email = it },
                        placeholder = "voce@email.com",
                        error = emailError,
                        keyboardType = KeyboardType.Email,
                    )
                    LoginField(
                        label = "Senha",
                        value = password,
                        onValueChange = { password = it },
                        placeholder = "Sua senha",
                        error = passwordError,
                        keyboardType = KeyboardType.Password,
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        trailing = {
                            Text(
                                text = if (showPassword) "Ocultar" else "Mostrar",
                                modifier = Modifier.clickable { showPassword = !showPassword },
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        },
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        KeepConnected(checked = keepConnected, onToggle = { keepConnected = !keepConnected })
                        Text(
                            text = "Esqueci minha senha",
                            modifier = Modifier.clickable(onClick = onForgotPassword),
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    LoginButton(status = status, onClick = ::submit)
                }
            }
        }
    }
}

@Composable
private fun BackButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text("←", color = MaterialTheme.colorScheme.onBackground, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun LoginField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    error: String,
    keyboardType: KeyboardType,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailing: (@Composable () -> Unit)? = null,
) {
    val borderColor = if (error.isNotEmpty()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline
    Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(15.dp))
                .border(BorderStroke(1.5.dp, borderColor), RoundedCornerShape(15.dp))
                .padding(horizontal = 15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                visualTransformation = visualTransformation,
                decorationBox = { inner ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                        inner()
                    }
                },
            )
            trailing?.invoke()
        }
        if (error.isNotEmpty()) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun KeepConnected(checked: Boolean, onToggle: () -> Unit) {
    val primary = MaterialTheme.colorScheme.primary
    Row(
        modifier = Modifier.clickable(onClick = onToggle),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .size(21.dp)
                .background(if (checked) primary else Color.Transparent, RoundedCornerShape(7.dp))
                .border(1.5.dp, if (checked) primary else MaterialTheme.colorScheme.outline, RoundedCornerShape(7.dp)),
            contentAlignment = Alignment.Center,
        ) {
            if (checked) Text("✓", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
        }
        Text(
            text = "Manter conectado",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun LoginButton(status: LoginStatus, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(54.dp),
        shape = RoundedCornerShape(17.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (status == LoginStatus.Success) RangoColors.Success else MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            if (status == LoginStatus.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(19.dp),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.35f),
                    strokeWidth = 2.5.dp,
                )
            }
            if (status == LoginStatus.Success) {
                Text("✓", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            }
            Text(
                text = when (status) {
                    LoginStatus.Idle -> "Entrar"
                    LoginStatus.Loading -> "Entrando..."
                    LoginStatus.Success -> "Bem-vinda!"
                },
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
