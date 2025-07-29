package com.sakhura.childcareapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sakhura.childcareapp.data.database.entities.Child
import com.sakhura.childcareapp.data.database.entities.Parent
import com.sakhura.childcareapp.data.repository.ChildcareRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddChildUiState(
    val childName: String = "",
    val childAge: String = "",
    val hourlyRate: String = "",
    val childNotes: String = "",
    val parents: List<ParentInput> = listOf(ParentInput()),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isChildSaved: Boolean = false
)

data class ParentInput(
    val name: String = "",
    val phoneNumber: String = "",
    val relationship: String = "Padre/Madre"
)

@HiltViewModel
class AddChildViewModel @Inject constructor(
    private val repository: ChildcareRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddChildUiState())
    val uiState: StateFlow<AddChildUiState> = _uiState.asStateFlow()

    fun updateChildName(name: String) {
        _uiState.value = _uiState.value.copy(childName = name, errorMessage = null)
    }

    fun updateChildAge(age: String) {
        // Solo permitir números
        if (age.isEmpty() || age.all { it.isDigit() }) {
            _uiState.value = _uiState.value.copy(childAge = age, errorMessage = null)
        }
    }

    fun updateHourlyRate(rate: String) {
        // Permitir números y un punto decimal
        if (rate.isEmpty() || rate.matches(Regex("^\\d+(\\.\\d{0,2})?$"))) {
            _uiState.value = _uiState.value.copy(hourlyRate = rate, errorMessage = null)
        }
    }

    fun updateChildNotes(notes: String) {
        _uiState.value = _uiState.value.copy(childNotes = notes)
    }

    fun updateParentName(index: Int, name: String) {
        val currentParents = _uiState.value.parents.toMutableList()
        if (index < currentParents.size) {
            currentParents[index] = currentParents[index].copy(name = name)
            _uiState.value = _uiState.value.copy(parents = currentParents, errorMessage = null)
        }
    }

    fun updateParentPhone(index: Int, phone: String) {
        val currentParents = _uiState.value.parents.toMutableList()
        if (index < currentParents.size) {
            currentParents[index] = currentParents[index].copy(phoneNumber = phone)
            _uiState.value = _uiState.value.copy(parents = currentParents, errorMessage = null)
        }
    }

    fun updateParentRelationship(index: Int, relationship: String) {
        val currentParents = _uiState.value.parents.toMutableList()
        if (index < currentParents.size) {
            currentParents[index] = currentParents[index].copy(relationship = relationship)
            _uiState.value = _uiState.value.copy(parents = currentParents)
        }
    }

    fun addParent() {
        val currentParents = _uiState.value.parents.toMutableList()
        currentParents.add(ParentInput())
        _uiState.value = _uiState.value.copy(parents = currentParents)
    }

    fun removeParent(index: Int) {
        val currentParents = _uiState.value.parents.toMutableList()
        if (currentParents.size > 1 && index < currentParents.size) {
            currentParents.removeAt(index)
            _uiState.value = _uiState.value.copy(parents = currentParents)
        }
    }

    fun saveChild() {
        val state = _uiState.value

        // Validaciones
        val validationError = validateInput(state)
        if (validationError != null) {
            _uiState.value = state.copy(errorMessage = validationError)
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, errorMessage = null)

            try {
                // Crear el objeto Child
                val child = Child(
                    name = state.childName.trim(),
                    age = state.childAge.toInt(),
                    hourlyRate = state.hourlyRate.toDouble(),
                    notes = state.childNotes.trim()
                )

                // Crear la lista de padres
                val parents = state.parents
                    .filter { it.name.isNotBlank() } // Solo padres con nombre
                    .map { parentInput ->
                        Parent(
                            childId = 0, // Se actualizará en el repository
                            name = parentInput.name.trim(),
                            phoneNumber = parentInput.phoneNumber.trim(),
                            relationship = parentInput.relationship.trim()
                        )
                    }

                // Guardar en la base de datos
                repository.insertChild(child, parents)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isChildSaved = true
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error al guardar: ${e.message}"
                )
            }
        }
    }

    private fun validateInput(state: AddChildUiState): String? {
        return when {
            state.childName.isBlank() -> "El nombre del niño es requerido"
            state.childAge.isBlank() -> "La edad es requerida"
            state.childAge.toIntOrNull() == null -> "La edad debe ser un número válido"
            state.childAge.toInt() < 0 || state.childAge.toInt() > 18 -> "La edad debe estar entre 0 y 18 años"
            state.hourlyRate.isBlank() -> "La tarifa por hora es requerida"
            state.hourlyRate.toDoubleOrNull() == null -> "La tarifa debe ser un número válido"
            state.hourlyRate.toDouble() <= 0 -> "La tarifa debe ser mayor a 0"
            state.hourlyRate.toDouble() > 1000 -> "La tarifa parece muy alta (máximo $1000/hora)"
            state.parents.none { it.name.isNotBlank() } -> "Debe agregar al menos un padre/madre/tutor"
            state.parents.any { it.name.isNotBlank() && it.phoneNumber.isBlank() } -> "Todos los contactos deben tener número de teléfono"
            else -> null
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}