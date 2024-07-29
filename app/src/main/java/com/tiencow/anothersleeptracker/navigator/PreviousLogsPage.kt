@file:OptIn(ExperimentalMaterial3Api::class)

package com.tiencow.anothersleeptracker.navigator

import android.graphics.Color.rgb
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tiencow.anothersleeptracker.StopwatchViewModel
import com.tiencow.anothersleeptracker.room_database.TimeEntry
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tiencow.anothersleeptracker.TimeEntryEvent
import com.tiencow.anothersleeptracker.data_store.DataStoreHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SleepDurationViewModel @Inject constructor(
    dataStoreHelper: DataStoreHelper,
) : ViewModel() {
    val wantedSleepTime: StateFlow<Long> = dataStoreHelper.getDuration()
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0L)
}

@Composable
fun PreviousLogsPage(
    viewModel: StopwatchViewModel = hiltViewModel(),
    sleepDurationViewModel: SleepDurationViewModel = hiltViewModel()
) {
    val timeEntries by viewModel.timeEntries.collectAsState(initial = emptyList())
    println("PreviousLogs: $timeEntries")
    val wantedTime by sleepDurationViewModel.wantedSleepTime.collectAsState()


    LazyColumn {
        items(
            items = timeEntries,
            key = { it.id } // Assuming TimeEntry has an 'id' property
        ) { timeEntry ->
            EntryItem(
                timeEntry = timeEntry,
                wantedTime = wantedTime,
                onRemove = { removedTimeEntry ->
                    viewModel.onEvent(TimeEntryEvent.DeleteTimeEntry(removedTimeEntry))
                }
            )
        }
    }

}

@Composable
fun Entry(timeEntry: TimeEntry, wantedTime: Long) {
    val color: Color = if (timeEntry.duration > Duration.ofMinutes(wantedTime)) {
        Color(rgb(90, 186, 17))
    } else {
        Color.Red
    }
    ListItem(
        modifier = Modifier.clip(MaterialTheme.shapes.small),
        headlineContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .padding(5.dp)
                        .fillMaxWidth(),
                ) {
                    Row {
                        Icon(
                            Icons.Default.Timelapse,
                            contentDescription = "Timelapse",
                            tint = color,
                            modifier = Modifier
                                .padding(3.dp)
                                .size(40.dp)
                        )
                        Text(
                            formatDuration(timeEntry.duration),
                            fontSize = 40.sp,
                            modifier = Modifier.padding(horizontal = 5.dp),
                        )
                    }
                    Column {
                        Text(formatDate(timeEntry.endDateTime), fontSize = 15.sp)
                        Text(formatDate(timeEntry.startDateTime), fontSize = 15.sp)
                    }
                }
            }
        },
    )
}

fun formatDuration(duration: Duration): String {
    val hours = duration.toHours()
    val minutes = duration.toMinutes() % 60
    val seconds = duration.seconds % 60
    return String.format("%d:%02d:%02d", hours, minutes, seconds)
}

fun formatDate(date: LocalDateTime): String {
    return date.format(DateTimeFormatter.ofPattern("MMMM d"))
}

@Composable
fun EntryItem(
    timeEntry: TimeEntry,
    wantedTime: Long,
    modifier: Modifier = Modifier,
    onRemove: (TimeEntry) -> Unit
) {
    val context = LocalContext.current
    val currentItem by rememberUpdatedState(timeEntry)
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            when (it) {
                SwipeToDismissBoxValue.EndToStart -> {
                    onRemove(currentItem)
                    Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show()
                    true
                }

                else -> false
            }
        },
        positionalThreshold = { it * .25f }
    )

    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue != SwipeToDismissBoxValue.Settled) {
            dismissState.reset()
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        modifier = modifier,
        backgroundContent = { DeleteBackground(dismissState) },
        content = {
            Entry(timeEntry, wantedTime)
        },
    )
}

@Composable
fun DeleteBackground(dismissState: SwipeToDismissBoxState) {
    val color = when (dismissState.dismissDirection) {
        SwipeToDismissBoxValue.EndToStart -> Color.Red
        SwipeToDismissBoxValue.Settled -> Color.Transparent
        else -> Color.Red
    }
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
            .padding(12.dp, 8.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete",
            tint = Color.White
        )
    }
}