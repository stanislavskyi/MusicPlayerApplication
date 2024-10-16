package com.hfad.musicplayerapplication.presentation.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.hfad.musicplayerapplication.R
import com.hfad.musicplayerapplication.presentation.MainActivity
import com.hfad.musicplayerapplication.presentation.screens.HomeFragment
import com.hfad.musicplayerapplication.presentation.screens.HomeFragment.Companion


class PushService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("Tag", "FCM Token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.notification?.let { notification ->
            val title = notification.title ?: "Default title"
            val body = notification.body ?: "Default body"
            showHandsUpNotification(title, body)
        }
    }

    private fun showHandsUpNotification(title: String, body: String) {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID_HEADS_UP_NOTIFICATION,
                CHANNEL_NAME_HEADS_UP_NOTIFICATION,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(this, CHANNEL_ID_HEADS_UP_NOTIFICATION)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)
            .setFullScreenIntent(pendingIntent, true)
            .build()

        notificationManager.notify(6666, builder)
    }

    companion object {
        private const val CHANNEL_ID_HEADS_UP_NOTIFICATION = "channel_id_heads_up_notification"
        private const val CHANNEL_NAME_HEADS_UP_NOTIFICATION = "channel_name_heads_up_notification"
    }
}