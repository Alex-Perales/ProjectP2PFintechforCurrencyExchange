package com.example.p2p.data.repository

import com.example.p2p.core.network.NetworkResult
import com.example.p2p.data.remote.api.TransactionApi
import com.example.p2p.data.remote.dto.CreateTransactionRequest
import com.example.p2p.data.remote.dto.TransactionDto
import com.example.p2p.domain.repository.TransactionRepository
import org.json.JSONObject

class TransactionRepositoryImpl(
    private val api: TransactionApi
) : TransactionRepository {

    override suspend fun listTransactions(status: String?): NetworkResult<List<TransactionDto>> {
        return try {
            val response = api.listTransactions(status)
            if (response.isSuccessful && response.body() != null) {
                NetworkResult.Success(response.body()!!.transactions)
            } else {
                NetworkResult.Error(response.code(), response.message())
            }
        } catch (e: Exception) {
            NetworkResult.Error(-1, e.message ?: "An error occurred")
        }
    }

    override suspend fun getPendingTransactions(): NetworkResult<List<TransactionDto>> {
        return try {
            val response = api.pendingTransactions()
            if (response.isSuccessful && response.body() != null) {
                NetworkResult.Success(response.body()!!.transactions)
            } else {
                NetworkResult.Error(response.code(), response.message())
            }
        } catch (e: Exception) {
            NetworkResult.Error(-1, e.message ?: "An error occurred")
        }
    }

    override suspend fun getTransaction(id: String): NetworkResult<TransactionDto> {
        return try {
            val response = api.getTransaction(id)
            if (response.isSuccessful && response.body() != null) {
                NetworkResult.Success(response.body()!!)
            } else {
                NetworkResult.Error(response.code(), response.message())
            }
        } catch (e: Exception) {
            NetworkResult.Error(-1, e.message ?: "An error occurred")
        }
    }

    override suspend fun createTransaction(request: CreateTransactionRequest): NetworkResult<TransactionDto> {
        return try {
            val response = api.createTransaction(request)
            if (response.isSuccessful && response.body() != null) {
                NetworkResult.Success(response.body()!!)
            } else {
                val errorMsg = parseBackendError(response.errorBody()?.string(), response.code())
                NetworkResult.Error(response.code(), errorMsg)
            }
        } catch (e: Exception) {
            NetworkResult.Error(-1, e.message ?: "Error de conexión")
        }
    }

    private fun parseBackendError(errorBody: String?, code: Int): String {
        if (!errorBody.isNullOrBlank()) {
            return try {
                val json = JSONObject(errorBody)
                val errorCode = json.optString("error", "")
                val message = json.optString("message", "")
                when (errorCode) {
                    "OWN_OFFER" -> "No puedes comprar tu propia oferta"
                    "OFFER_UNAVAILABLE" -> "Esta oferta ya no está disponible"
                    "INVALID_AMOUNT" -> "Monto inválido: $message"
                    "MISSING_FIELD" -> "Datos incompletos"
                    else -> if (message.isNotBlank()) message else "Error $code"
                }
            } catch (e: Exception) {
                "Error $code"
            }
        }
        return when (code) {
            400 -> "Solicitud incorrecta"
            401 -> "Sesión expirada, vuelve a iniciar sesión"
            403 -> "No tienes permiso para esta acción"
            404 -> "No encontrado"
            else -> "Error $code"
        }
    }

    override suspend fun uploadVoucher(id: String, imageUrl: String): NetworkResult<Unit> {
        return try {
            val response = api.uploadVoucher(id, mapOf("image_url" to imageUrl))
            if (response.isSuccessful) {
                NetworkResult.Success(Unit)
            } else {
                NetworkResult.Error(response.code(), response.message())
            }
        } catch (e: Exception) {
            NetworkResult.Error(-1, e.message ?: "An error occurred")
        }
    }

    override suspend fun updateStatus(id: String, newStatus: String): NetworkResult<TransactionDto> {
        return try {
            val response = api.updateStatus(id, mapOf("status" to newStatus))
            if (response.isSuccessful && response.body() != null) {
                NetworkResult.Success(response.body()!!)
            } else {
                NetworkResult.Error(response.code(), response.message())
            }
        } catch (e: Exception) {
            NetworkResult.Error(-1, e.message ?: "An error occurred")
        }
    }

    override suspend fun confirmTransaction(id: String): NetworkResult<Unit> {
        return try {
            val response = api.confirmTransaction(id)
            if (response.isSuccessful) {
                NetworkResult.Success(Unit)
            } else {
                NetworkResult.Error(response.code(), response.message())
            }
        } catch (e: Exception) {
            NetworkResult.Error(-1, e.message ?: "An error occurred")
        }
    }
}
