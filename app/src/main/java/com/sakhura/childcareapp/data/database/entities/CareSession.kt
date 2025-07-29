package com.sakhura.childcareapp.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "care_sessions")
data class CareSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val childId: Long,
    val startTime: Long,
    val endTime: Long?,
    val hourlyRate: Double,
    val notes: String = "",
    val isCompleted: Boolean = false
) {
    fun getTotalHours(): Double {
        return if (endTime != null) {
            (endTime - startTime) / 3600000.0
        } else 0.0
    }

    fun getTotalAmount(): Double {
        return getTotalHours() * hourlyRate
    }
}