package com.example.p2p.presentation.offer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.p2p.core.network.NetworkResult
import com.example.p2p.data.remote.model.CreateOfferRequest
import com.example.p2p.domain.repository.OfferRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PublishUiState(
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)

class PublishViewModel(
    private val offerRepository: OfferRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PublishUiState())
    val uiState: StateFlow<PublishUiState> = _uiState.asStateFlow()

    fun publishOffer(request: CreateOfferRequest) {
        viewModelScope.launch {
            _uiState.value = PublishUiState(isLoading = true)
            when (val result = offerRepository.createOffer(request)) {
                is NetworkResult.Success -> {
                    _uiState.value = PublishUiState(success = true)
                }
                is NetworkResult.Error -> {
                    _uiState.value = PublishUiState(error = result.message)
                }
                NetworkResult.Loading -> Unit
            }
        }
    }

    fun resetState() {
        _uiState.value = PublishUiState()
    }

    class Factory(private val offerRepository: OfferRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            PublishViewModel(offerRepository) as T
    }
}
