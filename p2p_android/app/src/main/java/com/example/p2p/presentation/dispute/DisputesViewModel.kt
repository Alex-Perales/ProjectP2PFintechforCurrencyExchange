package com.example.p2p.presentation.dispute

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.p2p.core.network.NetworkResult
import com.example.p2p.data.remote.dto.DisputeDto
import com.example.p2p.domain.repository.DisputeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DisputesUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val disputes: List<DisputeDto> = emptyList()
)

class DisputesViewModel(
    private val disputeRepository: DisputeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DisputesUiState())
    val uiState: StateFlow<DisputesUiState> = _uiState.asStateFlow()

    init {
        loadDisputes()
    }

    fun loadDisputes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = disputeRepository.getDisputes()) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, disputes = result.data)
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = result.message)
                }
                NetworkResult.Loading -> Unit
            }
        }
    }

    fun createDispute(
        transactionId: String,
        reason: String,
        description: String?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val request = com.example.p2p.data.remote.dto.CreateDisputeRequest(reason, description)
            when (val result = disputeRepository.createDispute(transactionId, request)) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    onSuccess()
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = result.message)
                    onError(result.message)
                }
                NetworkResult.Loading -> Unit
            }
        }
    }

    class Factory(private val repo: DisputeRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            DisputesViewModel(repo) as T
    }
}
