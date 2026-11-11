package org.example.project.data.remote

import platform.Foundation.NSUserDefaults

actual val ApiBaseUrl: String = "http://localhost:3333"

actual object SessionStorage {
    private const val RefreshTokenKey = "rango.refresh_token"
    private val defaults get() = NSUserDefaults.standardUserDefaults

    actual fun readRefreshToken(): String? = defaults.stringForKey(RefreshTokenKey)

    actual fun saveRefreshToken(token: String?) {
        if (token == null) defaults.removeObjectForKey(RefreshTokenKey) else defaults.setObject(token, RefreshTokenKey)
    }
}
