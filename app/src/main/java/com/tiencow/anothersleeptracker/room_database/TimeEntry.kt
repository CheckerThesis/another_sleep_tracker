/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

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