package com.lumen.alarm.data.repository

import com.lumen.alarm.data.local.dao.AlarmDao
import com.lumen.alarm.data.local.entity.AlarmEntity
import com.lumen.alarm.data.local.entity.AlarmHistoryEntity
import com.lumen.alarm.domain.model.Alarm
import com.lumen.alarm.domain.model.AlarmType
import com.lumen.alarm.domain.model.InsightsData
import com.lumen.alarm.util.AlarmScheduler
import com.lumen.alarm.util.toAlarm
import com.lumen.alarm.util.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmRepository @Inject constructor(
    private val dao: AlarmDao,
    private val scheduler: AlarmScheduler,
) {

    fun observeAllAlarms(): Flow<List<Alarm>> =
        dao.observeAllAlarms().map { list -> list.map { it.toAlarm() } }

    fun observeNextAlarm(): Flow<Alarm?> =
        dao.observeNextAlarm().map { it?.toAlarm() }

    fun observeAlarmById(id: Long): Flow<Alarm?> =
        dao.observeAlarmById(id).map { it?.toAlarm() }

    suspend fun getAlarmById(id: Long): Alarm? = dao.getAlarmById(id)?.toAlarm()

    suspend fun saveAlarm(alarm: Alarm): Long {
        val entity = alarm.toEntity()
        val id = dao.insertAlarm(entity)
        val saved = alarm.copy(id = id)
        if (saved.isEnabled) {
            val nextTrigger = scheduler.scheduleAlarm(saved)
            dao.updateNextTrigger(id, nextTrigger)
        }
        return id
    }

    suspend fun updateAlarm(alarm: Alarm) {
        scheduler.cancelAlarm(alarm.id)
        val entity = alarm.toEntity()
        dao.updateAlarm(entity)
        if (alarm.isEnabled) {
            val nextTrigger = scheduler.scheduleAlarm(alarm)
            dao.updateNextTrigger(alarm.id, nextTrigger)
        }
    }

    suspend fun deleteAlarm(id: Long) {
        scheduler.cancelAlarm(id)
        dao.deleteAlarmById(id)
    }

    suspend fun setAlarmEnabled(id: Long, enabled: Boolean) {
        dao.setAlarmEnabled(id, enabled)
        val alarm = dao.getAlarmById(id)?.toAlarm() ?: return
        if (enabled) {
            val nextTrigger = scheduler.scheduleAlarm(alarm)
            dao.updateNextTrigger(id, nextTrigger)
        } else {
            scheduler.cancelAlarm(id)
        }
    }

    suspend fun rescheduleAll() {
        val alarms = dao.getAllEnabledAlarms()
        alarms.forEach { entity ->
            val alarm = entity.toAlarm()
            val nextTrigger = scheduler.scheduleAlarm(alarm)
            dao.updateNextTrigger(alarm.id, nextTrigger)
        }
    }

    suspend fun recordDismiss(
        alarmId: Long,
        alarmLabel: String,
        scheduledTime: Long,
        snoozeCount: Int,
        coinsEarned: Int,
        streakDay: Int,
    ) {
        dao.insertHistory(
            AlarmHistoryEntity(
                alarmId = alarmId,
                alarmLabel = alarmLabel,
                scheduledTime = scheduledTime,
                actualWakeTime = System.currentTimeMillis(),
                snoozeCount = snoozeCount,
                coinsEarned = coinsEarned,
                streakDay = streakDay,
            )
        )
    }

    suspend fun getInsights(): InsightsData {
        val thirtyDaysAgo = Instant.now().minus(30, ChronoUnit.DAYS).toEpochMilli()
        val total = dao.getTotalCount(thirtyDaysAgo)
        val dismissed = dao.getDismissedCount(thirtyDaysAgo)
        val avgSnoozes = dao.getAvgSnoozeCount(thirtyDaysAgo)
        val wakeRate = if (total > 0) dismissed.toFloat() / total else 0f
        return InsightsData(
            wakeUpRate = wakeRate,
            avgSnoozes = avgSnoozes,
        )
    }

    suspend fun getTotalCoins(): Int = dao.getTotalCoinsEarned()
}
