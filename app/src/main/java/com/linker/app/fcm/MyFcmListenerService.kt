package com.linker.app.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.linker.app.core.network_api.repository.FcmRepository
import com.linker.app.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MyFcmListenerService : FirebaseMessagingService() {

    @Inject
    lateinit var fcmRepository: FcmRepository

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM_SERVICE", "New FCM token received: $token")

        serviceScope.launch {
            fcmRepository.registerToken(token)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // 🔴 Removing super.onMessageReceived(remoteMessage) to prevent propagation to other libraries (like Facebook SDK)
        // that might attempt to auto-parse fields like 'ta' which can cause NumberFormatException if they are floats.
        
        Log.d("FCM_SERVICE", "Message received from: ${remoteMessage.from}")

        // 🟢 Check if 'ta' field exists and log it for debugging
        remoteMessage.data["ta"]?.let {
            Log.d("FCM_SERVICE", "Detected 'ta' field in payload: $it")
        }

        // 🟢 Extract Title & Body from Notification block OR Data payload
        val title = remoteMessage.notification?.title
            ?: remoteMessage.data["title"]
            ?: "Local Job Finder"

        val body = remoteMessage.notification?.body
            ?: remoteMessage.data["message"]
            ?: "New notification update"

        val type = remoteMessage.data["notification_type"] ?: "GENERAL"

        Log.d("FCM_SERVICE", "Notification Title: $title, Body: $body, Type: $type")

        // 🟢 Build and post the notification banner to system tray
        showNotification(title, body)
    }

    private fun showNotification(title: String, message: String) {
        val channelId = "job_notifications_channel"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 1. Create Notification Channel for Android 8.0 (API 26+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Job Updates & Alerts",
                NotificationManager.IMPORTANCE_HIGH // 👈 Required for Heads-Up Banner Display
            ).apply {
                description = "Channel for job application alerts and updates"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // 2. Intent to open MainActivity when user taps notification
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 3. Build the System Notification
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // 👈 System icon (Replace with R.drawable.ic_notification if present)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // 👈 High priority for banner pop-up
            .setContentIntent(pendingIntent)

        // 4. Trigger system display
        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }
}