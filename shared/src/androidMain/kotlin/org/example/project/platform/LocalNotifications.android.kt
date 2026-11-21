package org.example.project.platform

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import org.example.project.shared.R

private const val OrdersChannelId = "orders"

actual object LocalNotifications {
    private var context: Context? = null
    private var permissionLauncher: ActivityResultLauncher<String>? = null

    // Chamado pela MainActivity no onCreate: o pedido de permissão precisa ser registrado antes da tela abrir.
    fun init(activity: ComponentActivity) {
        context = activity.applicationContext
        permissionLauncher = activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) {}
        // Android 8+: toda notificação precisa de um canal, que o usuário pode desligar nas configurações.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(OrdersChannelId, "Pedidos", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Etapas do pedido em andamento"
            }
            activity.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    actual fun requestPermission() {
        // Até o Android 12 a permissão é concedida na instalação.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || hasPermission()) return
        permissionLauncher?.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    actual fun show(id: Int, title: String, body: String) {
        val context = context ?: return
        if (!hasPermission()) return
        // Tocar na notificação abre o app.
        val launch = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val tap = launch?.let { PendingIntent.getActivity(context, id, it, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT) }
        val notification = NotificationCompat.Builder(context, OrdersChannelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(tap)
            .build()
        NotificationManagerCompat.from(context).notify(id, notification)
    }

    private fun hasPermission(): Boolean {
        val context = context ?: return false
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    }
}
