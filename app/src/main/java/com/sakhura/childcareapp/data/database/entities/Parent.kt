package com.sakhura.childcareapp.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "parents")
data class Parent(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val childId: Long,
    val name: String,
    val phoneNumber: String,
    val relationship: String = "Padre/Madre"
)