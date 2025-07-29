package com.sakhura.childcareapp.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "children")
data class Child(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val age: Int,
    val hourlyRate: Double,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)