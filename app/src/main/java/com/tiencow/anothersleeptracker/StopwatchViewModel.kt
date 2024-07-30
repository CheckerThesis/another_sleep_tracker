package com.tiencow.anothersleeptracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tiencow.anothersleeptracker.data_store.DataStoreHelper
import com.tiencow.anothersleeptracker.notification.ReminderNotificationService
import com.tiencow.anothersleeptracker.room_database.TimeEntry
import com.tiencow.anothersleeptracker.room_database.TimeEntryDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class StopwatchViewModel @Inject constructor(
    private val timeEntryDao: TimeEntryDao,
    private val dataStoreHelper: DataStoreHelper,
) : ViewModel() {
    private val _state = MutableStateFlow<StopwatchState>(StopwatchState.Idle) // current state of Stopwatch
    val state: StateFlow<StopwatchState> = _state.asStateFlow() // exposed state as read-only for observers

    val isRunning: StateFlow<Boolean> = dataStoreHelper.getIsRunningFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val timeEntries: StateFlow<List<TimeEntry>> = timeEntryDao.getAllTimeEntries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _time = MutableStateFlow("00:00:00")
    val time: StateFlow<String> = _time.asStateFlow()

    private var job: Job? = null

    init {
        viewModelScope.launch {
            isRunning.collect { running ->
                if (running) {
                    startUpdatingTime()
                } else {
                    stopUpdatingTime()
                }
            }
        }
    }

    private fun startUpdatingTime() {
        job?.cancel()
        job = viewModelScope.launch {
            while (true) {
                val startTime = dataStoreHelper.getStartTime()
                val currentTime = System.currentTimeMillis()
                val elapsedTime = currentTime - startTime
                _time.value = formatTime(elapsedTime)
                delay(1000) // Update every second
            }
        }
    }

    private fun stopUpdatingTime() {
        job?.cancel()
    }

    fun onEvent(event: TimeEntryEvent) {
        when (event) {
            is TimeEntryEvent.StartStopwatch -> {
                viewModelScope.launch {
                    dataStoreHelper.saveStartTime(System.currentTimeMillis())
                    dataStoreHelper.saveIsRunning(true)
                }
            }
            is TimeEntryEvent.StopStopwatch -> {
                viewModelScope.launch {
                    stopStopwatch()
                }
            }
            is TimeEntryEvent.EditTimeEntry -> editTimeEntry(event.timeEntry)
            is TimeEntryEvent.DeleteTimeEntry -> deleteTimeEntry(event.timeEntry)
        }
    }

    private suspend fun stopStopwatch() {
        val startTime = dataStoreHelper.getStartTime()

        if (startTime != 0L) {
            val endTime = System.currentTimeMillis()
            val duration = Duration.between(
                Instant.ofEpochMilli(startTime),
                Instant.ofEpochMilli(endTime)
            )

            val timeEntry = TimeEntry(
                startDateTime = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(startTime),
                    ZoneId.systemDefault()
                ),
                endDateTime = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(endTime),
                    ZoneId.systemDefault()
                ),
                duration = duration
            )

            timeEntryDao.upsertTimeEntry(timeEntry)

            dataStoreHelper.reset()

            _state.value = StopwatchState.Stopped(duration)
        }
    }

    fun getIsRunning(): Boolean {
        return isRunning.value
    }

    private fun editTimeEntry(timeEntry: TimeEntry) {
        viewModelScope.launch {
            timeEntryDao.upsertTimeEntry(timeEntry)
        }
    }

    private fun deleteTimeEntry(timeEntry: TimeEntry) {
        viewModelScope.launch {
            timeEntryDao.deleteTimeEntry(timeEntry)
        }
    }

    private fun formatTime(timeMillis: Long): String {
        val totalSeconds = timeMillis / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}