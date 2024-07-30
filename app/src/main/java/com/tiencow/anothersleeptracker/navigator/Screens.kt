package com.tiencow.anothersleeptracker.navigator

sealed class Screens(val route: String) {
    object Stopwatch : Screens("stopwatch_page")
    object PreviousLogs : Screens("previous_logs_page")
    object Settings : Screens("settings_page")
    object About : Screens("about_page")
}