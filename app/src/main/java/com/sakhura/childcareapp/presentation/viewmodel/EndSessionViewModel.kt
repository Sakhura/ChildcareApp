package com.sakhura.childcareapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakhura.childcareapp.data.repository.ChildcareRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EndSessionViewModel @Inject constructor(
    private val repository: ChildcareRepository
) : ViewModel() {

    fun endSession(sessionId: Long, notes: String) {
        viewModelScope.launch {
            repository.endCareSession(sessionId, notes)
        }
    }
}