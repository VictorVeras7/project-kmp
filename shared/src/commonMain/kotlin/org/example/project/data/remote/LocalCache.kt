package org.example.project.data.remote

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

// Cópias locais das respostas, para o app abrir sem rede: catálogo da Home (GET /home) e carrinho.
// O servidor continua sendo a fonte oficial; a cópia só é mostrada até a resposta dele chegar, ou sem rede.
class LocalCache {
    private val json = Json { ignoreUnknownKeys = true }

    var home: HomeDto?
        get() = read(HomeKey, HomeDto.serializer())
        set(value) = write(HomeKey, value, HomeDto.serializer())

    var cart: CartDto?
        get() = read(CartKey, CartDto.serializer())
        set(value) = write(CartKey, value, CartDto.serializer())

    // Ao sair da conta: não mostrar os dados do usuário anterior.
    fun clear() {
        LocalStore.write(HomeKey, null)
        LocalStore.write(CartKey, null)
    }

    // Cópia corrompida ou de uma versão antiga do app vale como "sem cópia".
    private fun <T> read(key: String, serializer: KSerializer<T>): T? =
        LocalStore.read(key)?.let { runCatching { json.decodeFromString(serializer, it) }.getOrNull() }

    private fun <T> write(key: String, value: T?, serializer: KSerializer<T>) {
        LocalStore.write(key, value?.let { json.encodeToString(serializer, it) })
    }

    private companion object {
        const val HomeKey = "cache_home"
        const val CartKey = "cache_cart"
    }
}
