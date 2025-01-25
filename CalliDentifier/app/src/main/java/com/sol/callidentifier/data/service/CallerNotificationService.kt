package com.sol.callidentifier.data.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.sol.callidentifier.R

class CallerNotificationService(private val context: Context) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, 
                "Caller Identification", 
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showCallerNotification(result: CallerIdentificationResult) {
        Log.d("CallerID", "Showing notification for result: $result")

        val notification = when (result) {
            is CallerIdentificationResult.Identified -> {
                createNotification(
                    "Incoming Call",
                    "Call from ${result.contact.name} (${result.contact.phoneNumber})"
                )
            }
            is CallerIdentificationResult.Blocked -> {
                createNotification(
                    "Blocked Call",
                    "Blocked call from ${result.contact.name} (${result.contact.phoneNumber})"
                )
            }
            CallerIdentificationResult.PotentialSpam -> {
                createNotification(
                    "Potential Spam Call",
                    "Incoming call from a potential spam number"
                )
            }
            CallerIdentificationResult.Unknown -> {
                createNotification(
                    "Unknown Caller",
                    "Incoming call from an unknown number"
                )
            }
        }

        // Use a unique notification ID for each call
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun createNotification(title: String, content: String) = 
        NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

    companion object {
        private const val CHANNEL_ID = "caller_identification_channel"
    }
} 