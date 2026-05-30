package com.example.p2p.presentation.market

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.p2p.core.network.NetworkResult
import com.example.p2p.data.remote.api.ExchangeApi
import com.example.p2p.data.remote.model.ExchangeRateDto
import com.example.p2p.data.remote.model.OfferDto
import com.example.p2p.domain.repository.OfferRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.p2p.domain.repository.TransactionRepository
import com.example.p2p.data.remote.model.CreateTransactionRequest

data class MarketUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val offers: List<OfferDto> = emptyList(),
    val exchangeRates: List<ExchangeRateDto> = emptyList()
)

class MarketViewModel(
    private val offerRepository: OfferRepository,
    private val transactionRepository: TransactionRepository,
    private val exchangeApi: ExchangeApi? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(MarketUiState())
    val uiState: StateFlow<MarketUiState> = _uiState.asStateFlow()

    init {
        loadOffers()
        loadExchangeRates()
    }

    private fun loadExchangeRates() {
        if (exchangeApi == null) return
        viewModelScope.launch {
            try {
                val response = exchangeApi.getRates()
                if (response.isSuccessful) {
                    val rates = response.body()?.rates ?: emptyList()
                    _uiState.value = _uiState.value.copy(exchangeRates = rates)
                }
            } catch (_: Exception) { /* keep defaults on error */ }
        }
    }

    fun loadOffers() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = offerRepository.listOffers()) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        offers = result.data
                    )
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                NetworkResult.Loading -> Unit
            }
        }
    }

    fun createTransaction(request: CreateTransactionRequest, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = transactionRepository.createTransaction(request)) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    onSuccess(result.data.id)
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    onError(result.message)
                }
                NetworkResult.Loading -> Unit
            }
        }
    }

    fun matchOffer(currency: String, fiatCurrency: String, onMatched: (OfferDto) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = offerRepository.matchOffer(currency, fiatCurrency)) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    onMatched(result.data)
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = result.message)
                    onError(result.message)
                }
                NetworkResult.Loading -> Unit
            }
        }
    }

    class Factory(
        private val offerRepository: OfferRepository,
        private val transactionRepository: TransactionRepository,
        private val exchangeApi: ExchangeApi? = null
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            MarketViewModel(offerRepository, transactionRepository, exchangeApi) as T
    }
}
