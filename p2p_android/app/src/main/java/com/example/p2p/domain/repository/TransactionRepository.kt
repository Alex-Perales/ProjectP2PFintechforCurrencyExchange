package com.example.p2p.domain.repository

import com.example.p2p.core.network.NetworkResult
import com.example.p2p.data.remote.model.CreateTransactionRequest
import com.example.p2p.data.remote.model.TransactionDto

interface TransactionRepository {
    suspend fun listTransactions(status: String? = null): NetworkResult<List<TransactionDto>>
    suspend fun getPendingTransactions(): NetworkResult<List<TransactionDto>>
    suspend fun getTransaction(id: String): NetworkResult<TransactionDto>
    suspend fun createTransaction(request: CreateTransactionRequest): NetworkResult<TransactionDto>
    suspend fun uploadVoucher(id: String, imageUrl: String): NetworkResult<Unit>
    suspend fun updateStatus(id: String, newStatus: String): NetworkResult<TransactionDto>
    suspend fun confirmTransaction(id: String): NetworkResult<Unit>
}
