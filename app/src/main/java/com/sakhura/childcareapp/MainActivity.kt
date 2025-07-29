package com.sakhura.childcareapp

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideChildcareDatabase(@ApplicationContext context: Context): ChildcareDatabase {
        return Room.databaseBuilder(
            context,
            ChildcareDatabase::class.java,
            "childcare_database"
        ).build()
    }

    @Provides
    fun provideChildDao(database: ChildcareDatabase): ChildDao {
        return database.childDao()
    }

    @Provides
    fun provideParentDao(database: ChildcareDatabase): ParentDao {
        return database.parentDao()
    }

    @Provides
    fun provideCareSessionDao(database: ChildcareDatabase): CareSessionDao {
        return database.careSessionDao()
    }

    // ✅ AGREGAR: Provider para ChildcareRepository
    @Provides
    @Singleton
    fun provideChildcareRepository(
        childDao: ChildDao,
        parentDao: ParentDao,
        careSessionDao: CareSessionDao
    ): ChildcareRepository {
        return ChildcareRepository(childDao, parentDao, careSessionDao)
    }
}