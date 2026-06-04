package com.example.p2p.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.p2p.core.network.NetworkResult
import com.example.p2p.data.remote.api.AdminDashboardResponse
import com.example.p2p.data.remote.api.AdminUser
import com.example.p2p.data.remote.model.Dispute
import com.example.p2p.data.remote.model.DisputesResponse
import com.example.p2p.domain.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ── UI State ──────────────────────────────────────────────────────────────────

data class AdminUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val stats: AdminDashboardResponse? = null,
    val disputes: List<Dispute> = emptyList(),
    val users: List<AdminUser> = emptyList(),
    val selectedDispute: Dispute? = null,
    // Para feedback de acciones individuales
    val actionInProgress: String? = null,   // ID de la disputa/usuario siendo procesado
    val actionSuccess: String? = null,      // Mensaje de éxito
)

// ── ViewModel ─────────────────────────────────────────────────────────────────

class AdminViewModel(
    private val repository: AdminRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    // ── Carga inicial ─────────────────────────────────────────────────────────

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val statsResult = repository.getDashboardStats()
            val disputesResult = repository.getDisputes()

            val stats = (statsResult as? NetworkResult.Success)?.data
            val disputes = (disputesResult as? NetworkResult.Success)?.data?.disputes ?: emptyList()
            val error = when {
                statsResult is NetworkResult.Error -> statsResult.message
                disputesResult is NetworkResult.Error -> disputesResult.message
                else -> null
            }

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                stats = stats,
                disputes = disputes,
                error = error,
            )
        }
    }

    fun loadDisputes(status: String? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = repository.getDisputes(status = status)) {
                is NetworkResult.Success -> _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    disputes = result.data.disputes,
                )
                is NetworkResult.Error -> _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = result.message,
                )
                NetworkResult.Loading -> Unit
            }
        }
    }

    fun loadUsers(role: String? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = repository.getUsers(role = role)) {
                is NetworkResult.Success -> _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    users = result.data,
                )
                is NetworkResult.Error -> _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = result.message,
                )
                NetworkResult.Loading -> Unit
            }
        }
    }

    // ── Acciones sobre disputas ───────────────────────────────────────────────

    fun takeDispute(
        disputeId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(actionInProgress = disputeId)
            when (val result = repository.takeDispute(disputeId)) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        actionInProgress = null,
                        actionSuccess = "Disputa tomada para revisión",
                    )
                    loadData()
                    onSuccess()
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        actionInProgress = null,
                        error = result.message,
                    )
                    onError(result.message)
                }
                NetworkResult.Loading -> Unit
            }
        }
    }

    fun resolveDispute(
        disputeId: String,
        resolution: String,
        resolutionNote: String? = null,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(actionInProgress = disputeId)
            when (val result = repository.resolveDispute(disputeId, resolution, resolutionNote)) {
                is NetworkResult.Success -> {
                    val label = if (resolution == "favour_buyer") "a favor del comprador"
                                else "a favor del vendedor"
                    _uiState.value = _uiState.value.copy(
                        actionInProgress = null,
                        actionSuccess = "Disputa resuelta $label",
                    )
                    loadData()
                    onSuccess()
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        actionInProgress = null,
                        error = result.message,
                    )
                    onError(result.message)
                }
                NetworkResult.Loading -> Unit
            }
        }
    }

    // ── Acciones sobre usuarios ───────────────────────────────────────────────

    fun banUser(
        userId: String,
        banned: Boolean,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(actionInProgress = userId)
            when (val result = repository.banUser(userId, banned)) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        actionInProgress = null,
                        actionSuccess = if (banned) "Usuario baneado" else "Ban removido",
                    )
                    loadUsers()
                    onSuccess()
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        actionInProgress = null,
                        error = result.message,
                    )
                    onError(result.message)
                }
                NetworkResult.Loading -> Unit
            }
        }
    }

    fun clearError() { _uiState.value = _uiState.value.copy(error = null) }
    fun clearSuccess() { _uiState.value = _uiState.value.copy(actionSuccess = null) }

    // ── Factory ───────────────────────────────────────────────────────────────

    class Factory(private val repo: AdminRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            AdminViewModel(repo) as T
    }
}
