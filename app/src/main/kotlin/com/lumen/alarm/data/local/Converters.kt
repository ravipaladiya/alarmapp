package com.lumen.alarm.data.local

import androidx.room.TypeConverter
import com.lumen.alarm.domain.model.AlarmType
import com.lumen.alarm.domain.model.ChallengeType
import com.lumen.alarm.domain.model.DismissType
import com.lumen.alarm.domain.model.VibrationPattern
import java.time.DayOfWeek

class Converters {

    @TypeConverter
    fun fromDayOfWeekSet(days: Set<DayOfWeek>): String =
        days.joinToString(",") { it.value.toString() }

    @TypeConverter
    fun toDayOfWeekSet(value: String): Set<DayOfWeek> =
        if (value.isBlank()) emptySet()
        else value.split(",").mapNotNull { v ->
            v.trim().toIntOrNull()?.let { DayOfWeek.of(it) }
        }.toSet()

    @TypeConverter
    fun fromVibrationPattern(pattern: VibrationPattern): String = pattern.name

    @TypeConverter
    fun toVibrationPattern(name: String): VibrationPattern =
        VibrationPattern.entries.firstOrNull { it.name == name } ?: VibrationPattern.STANDARD

    @TypeConverter
    fun fromAlarmType(type: AlarmType): String = type.name

    @TypeConverter
    fun toAlarmType(name: String): AlarmType =
        AlarmType.entries.firstOrNull { it.name == name } ?: AlarmType.NORMAL

    @TypeConverter
    fun fromChallengeType(type: ChallengeType): String = type.name

    @TypeConverter
    fun toChallengeType(name: String): ChallengeType =
        ChallengeType.entries.firstOrNull { it.name == name } ?: ChallengeType.NONE

    @TypeConverter
    fun fromDismissType(type: DismissType): String = type.name

    @TypeConverter
    fun toDismissType(name: String): DismissType =
        DismissType.entries.firstOrNull { it.name == name } ?: DismissType.NORMAL
}
