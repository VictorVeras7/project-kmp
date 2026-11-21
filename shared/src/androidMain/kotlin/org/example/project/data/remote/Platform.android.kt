package org.example.project.data.remote

import android.content.Context
import android.content.SharedPreferences

// localhost no próprio aparelho, redirecionado para a máquina com `adb reverse tcp:3333 tcp:3333`.
// Funciona no emulador e no celular pelo cabo, sem depender do Wi-Fi nem do IP da máquina.
actual val ApiBaseUrl: String = "http://localhost:3333"

actual object LocalStore {
    private var prefs: SharedPreferences? = null

    // Chamado pela MainActivity antes de montar a tela. O nome do arquivo é o mesmo da sessão,
    // para quem já tinha "Manter conectado" continuar entrando direto.
    fun init(context: Context) {
        prefs = context.getSharedPreferences("rango_session", Context.MODE_PRIVATE)
    }

    actual fun read(key: String): String? = prefs?.getString(key, null)

    actual fun write(key: String, value: String?) {
        prefs?.edit()?.apply {
            if (value == null) remove(key) else putString(key, value)
        }?.apply()
    }
}
