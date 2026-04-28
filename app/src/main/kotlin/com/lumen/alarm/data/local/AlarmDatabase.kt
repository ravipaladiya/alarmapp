package com.lumen.alarm.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.lumen.alarm.data.local.dao.AlarmDao
import com.lumen.alarm.data.local.entity.AlarmEntity
import com.lumen.alarm.data.local.entity.AlarmHistoryEntity

@Database(
    entities = [AlarmEntity::class, AlarmHistoryEntity::class],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class AlarmDatabase : RoomDatabase() {

    abstract fun alarmDao(): AlarmDao

    companion object {
        private const val DATABASE_NAME = "lumen_alarms.db"

        fun create(context: Context): AlarmDatabase =
            Room.databaseBuilder(context, AlarmDatabase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build()
    }
}
