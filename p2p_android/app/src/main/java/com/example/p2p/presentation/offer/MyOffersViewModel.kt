package com.example.p2p.presentation.offer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.p2p.core.network.NetworkResult
import com.example.p2p.data.remote.model.Offer
import com.example.p2p.domain.repository.OfferRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MyOffersUiState(
    val isLoading: Boolean = false,
    val offers: List<Offer> = emptyList(),
    val error: String? = null
)

class MyOffersViewModel(
    private val offerRepository: OfferRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyOffersUiState())
    val uiState: StateFlow<MyOffersUiState> = _uiState.asStateFlow()

    fun loadMyOffers() {
        viewModelScope.launch {
            _uiState.value = MyOffersUiState(isLoading = true)
            when (val result = offerRepository.getMyOffers()) {
                is NetworkResult.Success -> {
                    _uiState.value = MyOffersUiState(offers = result.data)
                }
                is NetworkResult.Error -> {
                    _uiState.value = MyOffersUiState(error = result.message)
                }
                NetworkResult.Loading -> Unit
            }
        }
    }

    fun pauseOffer(offerId: String) {
        viewModelScope.launch {
            when (offerRepository.pauseOffer(offerId)) {
                is NetworkResult.Success -> loadMyOffers()
                else -> Unit
            }
        }
    }

    fun resumeOffer(offerId: String) {
        viewModelScope.launch {
            when (offerRepository.resumeOffer(offerId)) {
                is NetworkResult.Success -> loadMyOffers()
                else -> Unit
            }
        }
    }

    fun deleteOffer(offerId: String) {
        viewModelScope.launch {
            when (offerRepository.deleteOffer(offerId)) {
                is NetworkResult.Success -> loadMyOffers()
                else -> Unit
            }
        }
    }

    class Factory(private val offerRepository: OfferRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            MyOffersViewModel(offerRepository) as T
    }
}
