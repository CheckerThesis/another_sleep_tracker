package com.tiencow.anothersleeptracker.data_store

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataStoreHelper @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private val START_TIME_KEY = longPreferencesKey("start_time") // store key in DataStore
    private val IS_RUNNING_KEY = booleanPreferencesKey("is_running") // store key in DataStore
    private val DURATION_KEY = longPreferencesKey("duration") // store key in DataStore

    suspend fun saveStartTime(startTime: Long) {
        dataStore.edit { preferences ->
            preferences[START_TIME_KEY] = startTime
        }
    }

    suspend fun saveIsRunning(isRunning: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_RUNNING_KEY] = isRunning
        }
    }

    suspend fun getStartTime(): Long {
        return dataStore.data.map { preferences ->
            preferences[START_TIME_KEY] ?: 0L
        }.first() // get start time from DataStore
    }

    suspend fun getIsRunning(): Boolean {
        return dataStore.data.map { preferences ->
            preferences[IS_RUNNING_KEY] ?: false
        }.first() // get isRunning from DataStore
    }

    suspend fun reset() {
        dataStore.edit { preferences ->
            preferences[START_TIME_KEY] = 0L
            preferences[IS_RUNNING_KEY] = false
        }
    }

    fun getIsRunningFlow(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[IS_RUNNING_KEY] ?: false
        }
    }

    fun getDuration(): Flow<Long> {
        return dataStore.data.map { preferences ->
            preferences[DURATION_KEY] ?: 0L
        }
    }

    suspend fun saveDuration(duration: Long) {
        dataStore.edit { preferences ->
            preferences[DURATION_KEY] = duration
        }
    }
}