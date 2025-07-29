package com.sakhura.childcareapp.data.repository

import com.sakhura.childcareapp.data.database.dao.CareSessionDao
import com.sakhura.childcareapp.data.database.dao.ChildDao
import com.sakhura.childcareapp.data.database.dao.ParentDao
import com.sakhura.childcareapp.data.database.entities.CareSession
import com.sakhura.childcareapp.data.database.entities.Child
import com.sakhura.childcareapp.data.database.entities.Parent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChildcareRepository @Inject constructor(
    private val childDao: ChildDao,
    private val parentDao: ParentDao,
    private val careSessionDao: CareSessionDao
) {
    fun getAllChildren(): Flow<List<Child>> = childDao.getAllChildren()

    fun getChildById(id: Long): Flow<Child> = childDao.getChildById(id)

    suspend fun insertChild(child: Child, parents: List<Parent>): Long {
        val childId = childDao.insertChild(child)
        parents.forEach { parent ->
            parentDao.insertParent(parent.copy(childId = childId))
        }
        return childId
    }

    fun getParentsByChildId(childId: Long): Flow<List<Parent>> =
        parentDao.getParentsByChildId(childId)

    suspend fun startCareSession(childId: Long): Long {
        val child = childDao.getChildById(childId).first()
        val session = CareSession(
            childId = childId,
            startTime = System.currentTimeMillis(),
            endTime = null,
            hourlyRate = child.hourlyRate
        )
        return careSessionDao.insertSession(session)
    }

    suspend fun endCareSession(sessionId: Long, notes: String) {
        val session = careSessionDao.getActiveSession()
        session?.let {
            val updatedSession = it.copy(
                endTime = System.currentTimeMillis(),
                notes = notes,
                isCompleted = true
            )
            careSessionDao.updateSession(updatedSession)
        }
    }

    fun getSessionsByChildId(childId: Long): Flow<List<CareSession>> =
        careSessionDao.getSessionsByChildId(childId)

    suspend fun getActiveSession(): CareSession? = careSessionDao.getActiveSession()

    suspend fun getTotalEarningsByChild(childId: Long): Double =
        careSessionDao.getTotalEarningsByChild(childId) ?: 0.0
}