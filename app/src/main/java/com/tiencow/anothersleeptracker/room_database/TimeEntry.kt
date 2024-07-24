package com.tiencow.anothersleeptracker.room_database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Duration
import java.time.LocalDateTime

@Entity
data class TimeEntry(
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val duration: Duration,

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)