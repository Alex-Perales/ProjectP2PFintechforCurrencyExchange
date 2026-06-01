package com.example.p2p.domain.repository

import com.example.p2p.core.network.NetworkResult
import com.example.p2p.data.remote.model.Dispute
import com.example.p2p.data.remote.model.CreateDisputeRequest

interface DisputeRepository {
    suspend fun getDisputes(): NetworkResult<List<Dispute>>
    suspend fun createDispute(transactionId: String, request: CreateDisputeRequest): NetworkResult<Dispute>
}
