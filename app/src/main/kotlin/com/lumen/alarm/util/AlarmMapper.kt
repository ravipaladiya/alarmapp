package com.lumen.alarm.util

import com.lumen.alarm.data.local.entity.AlarmEntity
import com.lumen.alarm.domain.model.Alarm

fun AlarmEntity.toAlarm(): Alarm = Alarm(
    id = id,
    hour = hour,
    minute = minute,
    label = label,
    isEnabled = isEnabled,
    repeatDays = repeatDays,
    soundUri = soundUri,
    soundName = soundName,
    vibrationPattern = vibrationPattern,
    snoozeMinutes = snoozeMinutes,
    snoozeMaxCount = snoozeMaxCount,
    alarmType = alarmType,
    volumeRampUp = volumeRampUp,
    flashlight = flashlight,
    challengeType = challengeType,
    photoVerificationUri = photoVerificationUri,
    isSmartWake = isSmartWake,
    smartWakeWindow = smartWakeWindow,
    goalId = goalId,
    createdAt = createdAt,
    nextTriggerAt = nextTriggerAt,
)

fun Alarm.toEntity(): AlarmEntity = AlarmEntity(
    id = id,
    hour = hour,
    minute = minute,
    label = label,
    isEnabled = isEnabled,
    repeatDays = repeatDays,
    soundUri = soundUri,
    soundName = soundName,
    vibrationPattern = vibrationPattern,
    snoozeMinutes = snoozeMinutes,
    snoozeMaxCount = snoozeMaxCount,
    alarmType = alarmType,
    volumeRampUp = volumeRampUp,
    flashlight = flashlight,
    challengeType = challengeType,
    photoVerificationUri = photoVerificationUri,
    isSmartWake = isSmartWake,
    smartWakeWindow = smartWakeWindow,
    goalId = goalId,
    createdAt = createdAt,
    nextTriggerAt = nextTriggerAt,
)
