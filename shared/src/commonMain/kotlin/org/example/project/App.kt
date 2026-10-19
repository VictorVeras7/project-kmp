package org.example.project

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.example.project.ui.theme.RangoTheme
import org.example.project.ui.welcome.WelcomeScreen

@Composable
@Preview
fun App() {
    RangoTheme {
        WelcomeScreen(onLogin = {}, onSignup = {})
    }
}
