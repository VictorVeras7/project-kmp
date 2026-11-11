package org.example.project.data.remote

import android.content.Context
import android.content.SharedPreferences

actual val ApiBaseUrl: String = "http://10.0.2.2:3333"

actual object SessionStorage {
    private const val RefreshTokenKey = "refresh_token"
    private var prefs: SharedPreferences? = null

    // Chamado pela MainActivity antes de montar a tela.
    fun init(context: Context) {
        prefs = context.getSharedPreferences("rango_session", Context.MODE_PRIVATE)
    }

    actual fun readRefreshToken(): String? = prefs?.getString(RefreshTokenKey, null)

    actual fun saveRefreshToken(token: String?) {
        prefs?.edit()?.apply {
            if (token == null) remove(RefreshTokenKey) else putString(RefreshTokenKey, token)
        }?.apply()
    }
}
