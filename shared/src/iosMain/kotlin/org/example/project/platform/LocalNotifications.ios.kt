package org.example.project.platform

import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotification
import platform.UserNotifications.UNNotificationPresentationOptionBanner
import platform.UserNotifications.UNNotificationPresentationOptionSound
import platform.UserNotifications.UNNotificationPresentationOptions
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNUserNotificationCenter
import platform.UserNotifications.UNUserNotificationCenterDelegateProtocol
import platform.darwin.NSObject

// Escrito sem biblioteca, para medir o custo real do ponto de plataforma. Só compila e roda no Mac.
actual object LocalNotifications {
    private val center get() = UNUserNotificationCenter.currentNotificationCenter()

    // Por padrão o iOS não mostra notificações com o app aberto; o delegate libera o banner.
    private val foregroundDelegate = ForegroundDelegate()

    actual fun requestPermission() {
        center.delegate = foregroundDelegate
        // Só mostra o pedido do sistema na primeira vez; depois devolve a resposta salva.
        center.requestAuthorizationWithOptions(
            UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge,
        ) { _, _ -> }
    }

    actual fun show(id: Int, title: String, body: String) {
        center.delegate = foregroundDelegate
        val content = UNMutableNotificationContent().apply {
            setTitle(title)
            setBody(body)
            setSound(UNNotificationSound.defaultSound())
        }
        // Sem gatilho: aparece na hora. O mesmo identificador substitui a notificação anterior do pedido.
        val request = UNNotificationRequest.requestWithIdentifier("order-$id", content, null)
        center.addNotificationRequest(request, null)
    }
}

private class ForegroundDelegate : NSObject(), UNUserNotificationCenterDelegateProtocol {
    override fun userNotificationCenter(
        center: UNUserNotificationCenter,
        willPresentNotification: UNNotification,
        withCompletionHandler: (UNNotificationPresentationOptions) -> Unit,
    ) {
        withCompletionHandler(UNNotificationPresentationOptionBanner or UNNotificationPresentationOptionSound)
    }
}
