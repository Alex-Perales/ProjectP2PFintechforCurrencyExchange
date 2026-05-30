package com.example.p2p.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.p2p.core.network.NetworkResult
import com.example.p2p.data.remote.api.AdminDashboardResponse
import com.example.p2p.data.remote.api.AdminDispute
import com.example.p2p.domain.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AdminUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val stats: AdminDashboardResponse? = null,
    val disputes: List<AdminDispute> = emptyList()
)

class AdminViewModel(
    private val repository: AdminRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val statsResult = repository.getDashboardStats()
            val disputesResult = repository.getDisputes()

            if (statsResult is NetworkResult.Success && disputesResult is NetworkResult.Success) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    stats = statsResult.data,
                    disputes = disputesResult.data
                )
            } else {
                val errMsg = when {
                    statsResult is NetworkResult.Error -> statsResult.message
                    disputesResult is NetworkResult.Error -> disputesResult.message
                    else -> "Failed to load admin data"
                }
                _uiState.value = _uiState.value.copy(isLoading = false, error = errMsg)
            }
        }
    }

    fun resolveDispute(disputeId: String, resolution: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = repository.resolveDispute(disputeId, resolution)) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    onSuccess()
                    loadData() // Reload
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = result.message)
                    onError(result.message)
                }
                NetworkResult.Loading -> Unit
            }
        }
    }

    class Factory(private val repo: AdminRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            AdminViewModel(repo) as T
    }
}
