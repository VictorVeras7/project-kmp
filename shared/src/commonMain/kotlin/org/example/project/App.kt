package org.example.project

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import org.example.project.ui.home.HomeScreen
import org.example.project.ui.login.LoginScreen
import org.example.project.ui.signup.SignupScreen
import org.example.project.ui.theme.RangoTheme
import org.example.project.ui.welcome.WelcomeScreen

private enum class Screen { Welcome, Login, Signup, Home }

@Composable
@Preview
fun App() {
    var screen by remember { mutableStateOf(Screen.Welcome) }
    var cartCount by remember { mutableStateOf(0) }
    var favoriteIds by remember { mutableStateOf(setOf<String>()) }

    RangoTheme {
        when (screen) {
            Screen.Welcome -> WelcomeScreen(
                onLogin = { screen = Screen.Login },
                onSignup = { screen = Screen.Signup },
            )
            Screen.Login -> LoginScreen(
                onBack = { screen = Screen.Welcome },
                onLoginSuccess = { screen = Screen.Home },
                onForgotPassword = {},
                onSignup = { screen = Screen.Signup },
            )
            Screen.Signup -> SignupScreen(
                onBack = { screen = Screen.Welcome },
                onSignupSuccess = { _, _, _ -> screen = Screen.Home },
                onLogin = { screen = Screen.Login },
            )
            Screen.Home -> HomeScreen(
                userName = "Marina Alves",
                address = "Rua das Palmeiras, 128",
                cartCount = cartCount,
                favoriteIds = favoriteIds,
                onToggleFavorite = { id ->
                    favoriteIds = if (id in favoriteIds) favoriteIds - id else favoriteIds + id
                },
                onAddToCart = { cartCount++ },
                onOpenProduct = {},
                onOpenCart = {},
                onOpenNotifications = {},
                onOpenSearch = {},
            )
        }
    }
}
