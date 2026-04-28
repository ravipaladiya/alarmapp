package com.lumen.alarm.data.local.dao

import androidx.room.*
import com.lumen.alarm.data.local.entity.AlarmEntity
import com.lumen.alarm.data.local.entity.AlarmHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {

    // ── Alarms ────────────────────────────────────────────────────────────────

    @Query("SELECT * FROM alarms ORDER BY hour ASC, minute ASC")
    fun observeAllAlarms(): Flow<List<AlarmEntity>>

    @Query("SELECT * FROM alarms WHERE isEnabled = 1 ORDER BY nextTriggerAt ASC")
    fun observeEnabledAlarms(): Flow<List<AlarmEntity>>

    @Query("SELECT * FROM alarms WHERE id = :id")
    suspend fun getAlarmById(id: Long): AlarmEntity?

    @Query("SELECT * FROM alarms WHERE id = :id")
    fun observeAlarmById(id: Long): Flow<AlarmEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarm: AlarmEntity): Long

    @Update
    suspend fun updateAlarm(alarm: AlarmEntity)

    @Query("DELETE FROM alarms WHERE id = :id")
    suspend fun deleteAlarmById(id: Long)

    @Query("UPDATE alarms SET isEnabled = :enabled WHERE id = :id")
    suspend fun setAlarmEnabled(id: Long, enabled: Boolean)

    @Query("UPDATE alarms SET nextTriggerAt = :nextTriggerAt WHERE id = :id")
    suspend fun updateNextTrigger(id: Long, nextTriggerAt: Long)

    @Query("UPDATE alarms SET snoozeCount = :count WHERE id = :id")
    suspend fun updateSnoozeCount(id: Long, count: Int)

    @Query("SELECT * FROM alarms WHERE isEnabled = 1 ORDER BY nextTriggerAt ASC LIMIT 1")
    fun observeNextAlarm(): Flow<AlarmEntity?>

    @Query("SELECT * FROM alarms WHERE isEnabled = 1")
    suspend fun getAllEnabledAlarms(): List<AlarmEntity>

    // ── History ───────────────────────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: AlarmHistoryEntity): Long

    @Query("SELECT * FROM alarm_history ORDER BY scheduledTime DESC LIMIT :limit")
    suspend fun getRecentHistory(limit: Int = 30): List<AlarmHistoryEntity>

    @Query("SELECT * FROM alarm_history WHERE alarmId = :alarmId ORDER BY scheduledTime DESC")
    fun observeHistoryForAlarm(alarmId: Long): Flow<List<AlarmHistoryEntity>>

    @Query("SELECT COUNT(*) FROM alarm_history WHERE dismissType != 'MISSED' AND scheduledTime >= :since")
    suspend fun getDismissedCount(since: Long): Int

    @Query("SELECT COUNT(*) FROM alarm_history WHERE scheduledTime >= :since")
    suspend fun getTotalCount(since: Long): Int

    @Query("SELECT AVG(snoozeCount) FROM alarm_history WHERE scheduledTime >= :since")
    suspend fun getAvgSnoozeCount(since: Long): Float

    @Query("SELECT SUM(coinsEarned) FROM alarm_history")
    suspend fun getTotalCoinsEarned(): Int
}
