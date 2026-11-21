package org.example.project.data.remote

import platform.Foundation.NSUserDefaults

actual val ApiBaseUrl: String = "http://localhost:3333"

actual object LocalStore {
    // Prefixo para não misturar com outras chaves do NSUserDefaults (a sessão continua em "rango.refresh_token").
    private const val Prefix = "rango."
    private val defaults get() = NSUserDefaults.standardUserDefaults

    actual fun read(key: String): String? = defaults.stringForKey(Prefix + key)

    actual fun write(key: String, value: String?) {
        if (value == null) defaults.removeObjectForKey(Prefix + key) else defaults.setObject(value, Prefix + key)
    }
}
