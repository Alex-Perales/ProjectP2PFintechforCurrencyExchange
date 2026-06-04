package com.example.p2p.presentation.complaints

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.p2p.core.network.NetworkResult
import com.example.p2p.data.remote.api.ComplaintApi
import com.example.p2p.data.remote.model.Complaint
import com.example.p2p.data.remote.model.CreateComplaintRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

data class ComplaintsUiState(
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val complaints: List<Complaint> = emptyList(),
    val error: String? = null,
    val submitSuccess: Boolean = false
)

class ComplaintsViewModel(private val api: ComplaintApi) : ViewModel() {

    private val _uiState = MutableStateFlow(ComplaintsUiState())
    val uiState: StateFlow<ComplaintsUiState> = _uiState.asStateFlow()

    init { loadMyComplaints() }

    fun loadMyComplaints() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val response = api.getMyComplaints()
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        complaints = response.body()!!.complaints
                    )
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = response.message())
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun createComplaint(
        type: String,
        description: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmitting = true)
            try {
                val response = api.createComplaint(CreateComplaintRequest(type, description))
                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(isSubmitting = false, submitSuccess = true)
                    loadMyComplaints()
                    onSuccess()
                } else {
                    _uiState.value = _uiState.value.copy(isSubmitting = false)
                    onError(response.message())
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSubmitting = false)
                onError(e.message ?: "Error de conexión")
            }
        }
    }

    class Factory(private val api: ComplaintApi) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ComplaintsViewModel(api) as T
    }
}