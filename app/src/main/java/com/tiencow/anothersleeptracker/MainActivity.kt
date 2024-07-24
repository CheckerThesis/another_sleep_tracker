package com.tiencow.anothersleeptracker

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.tiencow.anothersleeptracker.navigator.Navigation
import com.tiencow.anothersleeptracker.notification.ReminderNotificationService
import com.tiencow.anothersleeptracker.ui.theme.AnotherSleepTrackerTheme
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AnotherSleepTrackerTheme {
                Navigation()
            }
        }
    }
}