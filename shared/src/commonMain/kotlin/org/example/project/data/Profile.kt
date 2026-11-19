package org.example.project.data

// Dados da conta mostrados no topo do perfil.
data class UserProfile(val name: String, val email: String, val phone: String)

// Iniciais do avatar: as duas primeiras letras do primeiro nome, como no protótipo e na Home.
fun avatarInitials(name: String): String = name.trim().substringBefore(' ').take(2).uppercase()

// Tema salvo nas configurações da conta (GET /me/settings).
enum class ThemeMode(val label: String, val apiValue: String) {
    Light("Claro", "light"),
    Dark("Escuro", "dark"),
    System("Sistema", "system"),
}

// Quais notificações o usuário quer receber (GET e PATCH /me/settings).
data class NotificationPrefs(val orders: Boolean = true, val promos: Boolean = true, val news: Boolean = false)
