package com.tiencow.anothersleeptracker.room_database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@TypeConverters(Converters::class)
@Database(entities = [
    TimeEntry::class],
    version = 1
)
abstract class TimeEntryDatabase: RoomDatabase() {
    abstract fun timeEntryDao(): TimeEntryDao
}