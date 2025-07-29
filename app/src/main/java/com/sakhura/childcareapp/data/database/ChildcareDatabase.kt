package com.sakhura.childcareapp.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.sakhura.childcareapp.data.database.dao.CareSessionDao
import com.sakhura.childcareapp.data.database.dao.ChildDao
import com.sakhura.childcareapp.data.database.dao.ParentDao
import com.sakhura.childcareapp.data.database.entities.CareSession
import com.sakhura.childcareapp.data.database.entities.Child
import com.sakhura.childcareapp.data.database.entities.Parent

@Database(
    entities = [Child::class, Parent::class, CareSession::class],
    version = 1,
    exportSchema = false
)
abstract class ChildcareDatabase : RoomDatabase() {
    abstract fun childDao(): ChildDao
    abstract fun parentDao(): ParentDao
    abstract fun careSessionDao(): CareSessionDao
}