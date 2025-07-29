package com.sakhura.childcareapp.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.sakhura.childcareapp.data.database.entities.CareSession
import kotlinx.coroutines.flow.Flow

@Dao
interface CareSessionDao {
    @Query("SELECT * FROM care_sessions WHERE childId = :childId ORDER BY startTime DESC")
    fun getSessionsByChildId(childId: Long): Flow<List<CareSession>>

    @Query("SELECT * FROM care_sessions WHERE isCompleted = 0 LIMIT 1")
    suspend fun getActiveSession(): CareSession?

    @Query("SELECT SUM((endTime - startTime) / 3600000.0 * hourlyRate) FROM care_sessions WHERE childId = :childId AND isCompleted = 1")
    suspend fun getTotalEarningsByChild(childId: Long): Double?

    @Insert
    suspend fun insertSession(session: CareSession): Long

    @Update
    suspend fun updateSession(session: CareSession)

    @Delete
    suspend fun deleteSession(session: CareSession)
}