package com.tiencow.anothersleeptracker.dependency_injection

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.tiencow.anothersleeptracker.data_store.DataStoreHelper
import com.tiencow.anothersleeptracker.room_database.TimeEntryDao
import com.tiencow.anothersleeptracker.room_database.TimeEntryDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): TimeEntryDatabase {
        return Room.databaseBuilder(
            context,
            TimeEntryDatabase::class.java,
            "time_entry_database"
        ).build()
    }

    @Provides
    fun provideTimeEntryDao(db: TimeEntryDatabase): TimeEntryDao {
        return db.timeEntryDao()
    }

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile("stopwatch_preferences") }
        )
    }

    @Provides
    @Singleton
    fun provideDataStoreHelper(dataStore: DataStore<Preferences>): DataStoreHelper {
        return DataStoreHelper(dataStore)
    }
}