package com.lumen.alarm.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.lumen.alarm.data.local.Converters
import com.lumen.alarm.domain.model.AlarmType
import com.lumen.alarm.domain.model.ChallengeType
import com.lumen.alarm.domain.model.VibrationPattern
import java.time.DayOfWeek

@Entity(tableName = "alarms")
@TypeConverters(Converters::class)
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val hour: Int,
    val minute: Int,
    val label: String = "",
    val isEnabled: Boolean = true,
    val repeatDays: Set<DayOfWeek> = emptySet(),
    val soundUri: String = "",
    val soundName: String = "Default",
    val vibrationPattern: VibrationPattern = VibrationPattern.STANDARD,
    val snoozeMinutes: Int = 5,
    val snoozeMaxCount: Int = 3,
    val alarmType: AlarmType = AlarmType.NORMAL,
    val volumeRampUp: Boolean = true,
    val flashlight: Boolean = false,
    val challengeType: ChallengeType = ChallengeType.NONE,
    val photoVerificationUri: String = "",
    val isSmartWake: Boolean = false,
    val smartWakeWindow: Int = 30,
    val goalId: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val nextTriggerAt: Long = 0,
    val snoozeCount: Int = 0,
    val lastDismissedAt: Long = 0,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val locationName: String = "",
    val locationRadius: Int = 200,
    val locationTriggerOnLeave: Boolean = true,
)
