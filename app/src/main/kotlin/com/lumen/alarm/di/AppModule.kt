package com.lumen.alarm.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.lumen.alarm.data.local.AlarmDatabase
import com.lumen.alarm.data.local.dao.AlarmDao
import com.lumen.alarm.data.repository.AlarmRepository
import com.lumen.alarm.data.repository.PreferencesRepository
import com.lumen.alarm.util.AlarmScheduler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "lumen_prefs")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AlarmDatabase =
        AlarmDatabase.create(context)

    @Provides
    @Singleton
    fun provideAlarmDao(db: AlarmDatabase): AlarmDao = db.alarmDao()

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        context.dataStore

    @Provides
    @Singleton
    fun provideAlarmScheduler(@ApplicationContext context: Context): AlarmScheduler =
        AlarmScheduler(context)

    @Provides
    @Singleton
    fun provideAlarmRepository(dao: AlarmDao, scheduler: AlarmScheduler): AlarmRepository =
        AlarmRepository(dao, scheduler)

    @Provides
    @Singleton
    fun providePreferencesRepository(dataStore: DataStore<Preferences>): PreferencesRepository =
        PreferencesRepository(dataStore)
}
