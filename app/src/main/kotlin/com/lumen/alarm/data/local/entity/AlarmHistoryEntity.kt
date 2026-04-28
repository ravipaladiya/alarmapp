package com.lumen.alarm.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.lumen.alarm.domain.model.DismissType

@Entity(
    tableName = "alarm_history",
    foreignKeys = [ForeignKey(
        entity = AlarmEntity::class,
        parentColumns = ["id"],
        childColumns = ["alarmId"],
        onDelete = ForeignKey.SET_NULL,
    )],
    indices = [Index("alarmId")]
)
data class AlarmHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val alarmId: Long?,
    val alarmLabel: String,
    val scheduledTime: Long,
    val actualWakeTime: Long,
    val snoozeCount: Int = 0,
    val dismissType: DismissType = DismissType.NORMAL,
    val coinsEarned: Int = 0,
    val streakDay: Int = 0,
)
