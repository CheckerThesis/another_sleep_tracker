package com.tiencow.anothersleeptracker.navigator

import android.app.TimePickerDialog
import android.content.Context
import android.widget.ListView
import android.widget.NumberPicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tiencow.anothersleeptracker.StopwatchController
import com.tiencow.anothersleeptracker.data_store.DataStoreHelper
import com.tiencow.anothersleeptracker.notification.ReminderNotificationService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlin.time.Duration
import androidx.compose.material3.TimeInput
import androidx.navigation.NavController
import com.mikepenz.aboutlibraries.ui.compose.m3.Libraries
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val reminderNotificationService: ReminderNotificationService,
    private val dataStoreHelper: DataStoreHelper
) : ViewModel() {
    private val _wantedSleepTime = MutableStateFlow(0L)
    val wantedSleepTime: StateFlow<Long> = _wantedSleepTime.asStateFlow()

    init {
        viewModelScope.launch {
            dataStoreHelper.getDuration().collect { duration ->
                _wantedSleepTime.value = duration
            }
        }
    }

    val isRunning = dataStoreHelper.getIsRunningFlow()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            false
        )

    fun scheduleNotification(hourOfDay: Int, minute: Int, isRunning: Boolean) {
        viewModelScope.launch {
            reminderNotificationService.scheduleNotification(hourOfDay, minute, isRunning)
        }
    }

    fun updateWantedSleepTime(minutes: Long) {
        viewModelScope.launch {
            dataStoreHelper.saveDuration(minutes)
            _wantedSleepTime.value = minutes
        }
    }
}

@Composable
fun SettingsPage(
    viewModel: SettingsViewModel = hiltViewModel(),
    navController: NavController
) {
    var selectedTime by remember { mutableStateOf("") }
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val isRunning by viewModel.isRunning.collectAsState()

    val wantedTime by viewModel.wantedSleepTime.collectAsState()
    var showDurationPicker by remember { mutableStateOf(false) }

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            selectedTime = String.format("%02d:%02d", hourOfDay, minute)
            viewModel.scheduleNotification(hourOfDay, minute, isRunning)
            Toast.makeText(context, "Notification scheduled for $selectedTime", Toast.LENGTH_SHORT)
                .show()
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        false
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
    ) {
        SettingsItem(
            "Change repeating reminder time",
            if (selectedTime.isEmpty()) "Select Time" else "Selected Time: $selectedTime",
            { Icons.Default.Timer },
            { timePickerDialog.show() }
        )
        SettingsItem(
            "Wanted amount of sleep",
            "${wantedTime / 60} hours ${wantedTime % 60} minutes",
            { Icons.Default.Timer },
            { showDurationPicker = true }
        )
        SettingsItem(
            "About",
            onClick = {navController.navigate(Screens.About.route)}
        )
    }

    if (showDurationPicker) {
        DurationPickerDialog(
            onDismissRequest = { showDurationPicker = false },
            onConfirm = { hours, minutes ->
                val totalMinutes = hours * 60L + minutes
                viewModel.updateWantedSleepTime(totalMinutes)
                showDurationPicker = false
            },
            initialDuration = wantedTime.toInt()
        )
    }
}

@Composable
fun SettingsItem(
    title: String,
    description: String? = null,
    icon: @Composable (() -> Unit)? = null,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon?.invoke()
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.bodyLarge)
                if (description != null) {
                    Text(text = description, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DurationPickerDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (hours: Int, minutes: Int) -> Unit,
    initialDuration: Int
) {
    val state = rememberTimePickerState(
        initialHour = initialDuration / 60,
        initialMinute = initialDuration % 60
    )

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = {
                onConfirm(state.hour, state.minute)
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        },
        title = { Text("Select Duration") },
        text = {
            TimeInput(state = state)
        }
    )
}