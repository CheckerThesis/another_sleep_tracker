package com.tiencow.anothersleeptracker.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.BroadcastReceiver
import androidx.core.app.NotificationCompat
import com.tiencow.anothersleeptracker.MainActivity
import com.tiencow.anothersleeptracker.R
import com.tiencow.anothersleeptracker.StopwatchController
import com.tiencow.anothersleeptracker.data_store.DataStoreHelper
import com.tiencow.anothersleeptracker.navigator.SettingsViewModel
import com.tiencow.anothersleeptracker.notification.ReminderNotificationService.Companion.REMINDER_CHANNEL_ID
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ReminderNotificationReceiver : BroadcastReceiver() {
    @Inject
    lateinit var stopwatchController: StopwatchController

    @Inject
    lateinit var reminderNotificationService: ReminderNotificationService

    @Inject
    lateinit var dataStoreHelper: DataStoreHelper

    override fun onReceive(context: Context, intent: Intent?) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val hours = intent?.getIntExtra("hours", -1) ?: -1
        val minutes = intent?.getIntExtra("minutes", -1) ?: -1

        CoroutineScope(Dispatchers.Default).launch {
            val isRunning = dataStoreHelper.getIsRunning()

            // Schedule the next notification
            if (hours != -1 && minutes != -1) {
                reminderNotificationService.scheduleNotification(hours, minutes, isRunning)
            }

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    ReminderNotificationService.REMINDER_CHANNEL_ID,
                    ReminderNotificationService.CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                channel.description = "Used to remind to enable/disable sleep tracker"
                notificationManager.createNotificationChannel(channel)
            }

            val activityIntent = Intent(context, MainActivity::class.java)
            val activityPendingIntent = PendingIntent.getActivity(
                context,
                0,
                activityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            when (intent?.action) {
                ReminderNotificationService.ACTION_SHOW_NOTIFICATION -> {
                    val actionIntent =
                        Intent(context, ReminderNotificationReceiver::class.java).apply {
                            action =
                                if (isRunning) ReminderNotificationService.ACTION_STOP else ReminderNotificationService.ACTION_START
                        }
                    val actionPendingIntent = PendingIntent.getBroadcast(
                        context,
                        if (isRunning) 2 else 1,
                        actionIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )

                    val notification = NotificationCompat.Builder(
                        context,
                        ReminderNotificationService.REMINDER_CHANNEL_ID
                    )
                        .setContentTitle("Sleep Tracker Reminder")
                        .setContentText(if (isRunning) "Stop tracking your sleep?" else "Start tracking your sleep?")
                        .setSmallIcon(R.drawable.ic_sleep_reminder)
                        .setContentIntent(activityPendingIntent)
                        .addAction(
                            R.drawable.ic_sleep_reminder,
                            if (isRunning) "Stop" else "Start",
                            actionPendingIntent
                        )
                        .setAutoCancel(true)
                        .build()

                    notificationManager.notify(
                        ReminderNotificationService.NOTIFICATION_ID,
                        notification
                    )
                }

                ReminderNotificationService.ACTION_START -> {
                    CoroutineScope(Dispatchers.Default).launch {
                        stopwatchController.startStopwatch()
                    }
                    notificationManager.cancel(ReminderNotificationService.NOTIFICATION_ID)
                }

                ReminderNotificationService.ACTION_STOP -> {
                    CoroutineScope(Dispatchers.Default).launch {
                        stopwatchController.stopStopwatch()
                    }
                    notificationManager.cancel(ReminderNotificationService.NOTIFICATION_ID)
                }
            }
        }
    }
}