package org.example.project.data.remote

// Endereço do backend em cada plataforma (o emulador Android enxerga a máquina em 10.0.2.2).
expect val ApiBaseUrl: String

// Armazenamento chave-valor do aparelho: SharedPreferences no Android e NSUserDefaults no iOS.
// Guarda a sessão ("Manter conectado") e as cópias locais do catálogo e do carrinho.
expect object LocalStore {
    fun read(key: String): String?

    // null apaga a chave.
    fun write(key: String, value: String?)
}

// Guarda o refresh token entre aberturas do app, quando "Manter conectado" está marcado.
object SessionStorage {
    private const val RefreshTokenKey = "refresh_token"

    fun readRefreshToken(): String? = LocalStore.read(RefreshTokenKey)

    fun saveRefreshToken(token: String?) = LocalStore.write(RefreshTokenKey, token)
}
