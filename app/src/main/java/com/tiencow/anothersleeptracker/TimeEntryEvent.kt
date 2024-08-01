/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.tiencow.anothersleeptracker

import com.tiencow.anothersleeptracker.room_database.TimeEntry

sealed interface TimeEntryEvent {
    object StartStopwatch : TimeEntryEvent
    object StopStopwatch : TimeEntryEvent
    data class EditTimeEntry(val timeEntry: TimeEntry): TimeEntryEvent
    data class DeleteTimeEntry(val timeEntry: TimeEntry): TimeEntryEvent
}