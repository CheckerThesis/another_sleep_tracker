package com.tiencow.anothersleeptracker

import java.time.Duration

sealed class StopwatchState {
    object Idle : StopwatchState()
    data class Running(val startTime: Long) : StopwatchState()
    data class Stopped(val duration: Duration) : StopwatchState()
}
