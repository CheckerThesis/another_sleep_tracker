/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

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