package org.example.project.data.remote

// Endereço do backend em cada plataforma (o emulador Android enxerga a máquina em 10.0.2.2).
expect val ApiBaseUrl: String

// Guarda o refresh token entre aberturas do app, quando "Manter conectado" está marcado.
expect object SessionStorage {
    fun readRefreshToken(): String?
    fun saveRefreshToken(token: String?)
}
