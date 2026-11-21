package org.example.project

import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.example.project.data.remote.LocalStore
import org.example.project.platform.LocalNotifications

// Android 17+ exige essa permissão para o app acessar o backend na rede local (10.0.2.2 no emulador).
private const val LocalNetworkPermission = "android.permission.ACCESS_LOCAL_NETWORK"
private const val Android17 = 37

class MainActivity : ComponentActivity() {
    private val requestLocalNetwork = registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        LocalStore.init(applicationContext)
        LocalNotifications.init(this)

        if (Build.VERSION.SDK_INT >= Android17 &&
            checkSelfPermission(LocalNetworkPermission) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocalNetwork.launch(LocalNetworkPermission)
        }

        setContent {
            // Ícones da barra de status e de navegação seguem o tema do app, não só o do aparelho.
            App(onDarkThemeChange = { dark ->
                val style = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { dark }
                enableEdgeToEdge(statusBarStyle = style, navigationBarStyle = style)
            })
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
