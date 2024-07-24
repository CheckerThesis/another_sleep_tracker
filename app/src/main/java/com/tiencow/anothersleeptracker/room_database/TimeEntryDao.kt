package com.tiencow.anothersleeptracker.room_database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface TimeEntryDao {
    @Upsert
    suspend fun upsertTimeEntry(timeEntry: TimeEntry)

    @Delete
    suspend fun deleteTimeEntry(timeEntry: TimeEntry)

    @Query("SELECT * FROM TimeEntry ORDER BY startDateTime DESC")
    fun getAllTimeEntries(): Flow<List<TimeEntry>>
}