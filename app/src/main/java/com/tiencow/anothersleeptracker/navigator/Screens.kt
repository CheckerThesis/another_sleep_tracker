/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.tiencow.anothersleeptracker.navigator

sealed class Screens(val route: String) {
    object Stopwatch : Screens("stopwatch_page")
    object PreviousLogs : Screens("previous_logs_page")
    object Settings : Screens("settings_page")
    object About : Screens("about_page")
}