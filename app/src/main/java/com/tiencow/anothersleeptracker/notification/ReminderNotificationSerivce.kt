package com.tiencow.anothersleeptracker.notification

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.tiencow.anothersleeptracker.MainActivity
import com.tiencow.anothersleeptracker.R
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderNotificationService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun scheduleNotification(hours: Int, minutes: Int, isRunning: Boolean) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hours)
            set(Calendar.MINUTE, minutes)
            set(Calendar.SECOND, 0)
        }

        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        val intent = Intent(context, ReminderNotificationReceiver::class.java).apply {
            action = ACTION_SHOW_NOTIFICATION
            putExtra("isRunning", isRunning)
            putExtra("hours", hours)
            putExtra("minutes", minutes)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }

    companion object {
        const val REMINDER_CHANNEL_ID = "reminder_channel"
        const val NOTIFICATION_ID = 1
        const val ACTION_SHOW_NOTIFICATION = "ACTION_SHOW_NOTIFICATION"
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val CHANNEL_NAME = "Reminder Notification"
    }
}