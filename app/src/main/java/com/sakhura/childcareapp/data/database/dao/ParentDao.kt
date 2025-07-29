package com.sakhura.childcareapp.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.sakhura.childcareapp.data.database.entities.Parent
import kotlinx.coroutines.flow.Flow

@Dao
interface ParentDao {
    @Query("SELECT * FROM parents WHERE childId = :childId")
    fun getParentsByChildId(childId: Long): Flow<List<Parent>>

    @Insert
    suspend fun insertParent(parent: Parent)

    @Update
    suspend fun updateParent(parent: Parent)

    @Delete
    suspend fun deleteParent(parent: Parent)
}