package org.example.project.data.remote

import android.content.Context
import android.content.SharedPreferences

actual val ApiBaseUrl: String = "http://10.0.2.2:3333"

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
