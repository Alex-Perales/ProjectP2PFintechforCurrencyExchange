package com.example.p2p.data.repository

import com.example.p2p.core.network.NetworkResult
import com.example.p2p.data.remote.api.TransactionApi
import com.example.p2p.data.remote.dto.CreateTransactionRequest
import com.example.p2p.data.remote.dto.TransactionDto
import com.example.p2p.domain.repository.TransactionRepository

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
                NetworkResult.Error(response.code(), response.message())
            }
        } catch (e: Exception) {
            NetworkResult.Error(-1, e.message ?: "An error occurred")
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
