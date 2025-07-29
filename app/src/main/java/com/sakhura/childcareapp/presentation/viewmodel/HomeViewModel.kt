package com.sakhura.childcareapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakhura.childcareapp.data.database.entities.CareSession
import com.sakhura.childcareapp.data.database.entities.Child
import com.sakhura.childcareapp.data.repository.ChildcareRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ChildcareRepository
) : ViewModel() {

    val children: Flow<List<Child>> = repository.getAllChildren()

    private val _activeSession = MutableStateFlow<CareSession?>(null)
    val activeSession: StateFlow<CareSession?> = _activeSession.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadActiveSession()
    }

    private fun loadActiveSession() {
        viewModelScope.launch {
            _activeSession.value = repository.getActiveSession()
        }
    }

    fun startCareSession(childId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.startCareSession(childId)
                loadActiveSession()
            } catch (e: Exception) {
                // Manejar error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun endCareSession(notes: String = "") {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val activeSessionValue = _activeSession.value
                activeSessionValue?.let {
                    repository.endCareSession(it.id, notes)
                    loadActiveSession()
                }
            } catch (e: Exception) {
                // Manejar error
            } finally {
                _isLoading.value = false
            }
        }
    }
}