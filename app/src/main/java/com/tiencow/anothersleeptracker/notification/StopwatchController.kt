/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.tiencow.anothersleeptracker.notification

import com.tiencow.anothersleeptracker.data_store.DataStoreHelper
import com.tiencow.anothersleeptracker.room_database.TimeEntry
import com.tiencow.anothersleeptracker.room_database.TimeEntryDao
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StopwatchController @Inject constructor(
    private val dataStoreHelper: DataStoreHelper,
    private val timeEntryDao: TimeEntryDao
) {
    suspend fun startStopwatch() {
        dataStoreHelper.saveStartTime(System.currentTimeMillis())
        dataStoreHelper.saveIsRunning(true)
    }

    suspend fun stopStopwatch(): Duration? {
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

            return duration
        }
        return Duration.ZERO
    }
}