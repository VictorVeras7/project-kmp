package org.example.project

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import org.example.project.ui.login.LoginScreen
import org.example.project.ui.signup.SignupScreen
import org.example.project.ui.theme.RangoTheme
import org.example.project.ui.welcome.WelcomeScreen

private enum class Screen { Welcome, Login, Signup }

@Composable
@Preview
fun App() {
    var screen by remember { mutableStateOf(Screen.Welcome) }

    RangoTheme {
        when (screen) {
            Screen.Welcome -> WelcomeScreen(
                onLogin = { screen = Screen.Login },
                onSignup = { screen = Screen.Signup },
            )
            Screen.Login -> LoginScreen(
                onBack = { screen = Screen.Welcome },
                onLoginSuccess = {},
                onForgotPassword = {},
                onSignup = { screen = Screen.Signup },
            )
            Screen.Signup -> SignupScreen(
                onBack = { screen = Screen.Welcome },
                onSignupSuccess = { _, _, _ -> },
                onLogin = { screen = Screen.Login },
            )
        }
    }
}
