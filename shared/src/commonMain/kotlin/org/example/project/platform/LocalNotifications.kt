package org.example.project.platform

// Notificação local do sistema. Não há push do servidor: o app avisa quando o polling do pedido vê uma etapa nova.
expect object LocalNotifications {
    // Pede a permissão de notificações (Android 13+ e iOS). Não faz nada se ela já foi respondida.
    fun requestPermission()

    // Mostra (ou atualiza, com o mesmo id) uma notificação. Sem permissão, não aparece nada.
    fun show(id: Int, title: String, body: String)
}
