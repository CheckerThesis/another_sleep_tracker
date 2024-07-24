package com.tiencow.anothersleeptracker

import com.tiencow.anothersleeptracker.room_database.TimeEntry

sealed interface TimeEntryEvent {
    object StartStopwatch : TimeEntryEvent
    object StopStopwatch : TimeEntryEvent
    data class EditTimeEntry(val timeEntry: TimeEntry): TimeEntryEvent
    data class DeleteTimeEntry(val timeEntry: TimeEntry): TimeEntryEvent
}