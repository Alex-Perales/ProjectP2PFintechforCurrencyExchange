package com.example.p2p.domain.repository

import com.example.p2p.core.network.NetworkResult
import com.example.p2p.data.remote.model.DisputeDto
import com.example.p2p.data.remote.model.CreateDisputeRequest

interface DisputeRepository {
    suspend fun getDisputes(): NetworkResult<List<DisputeDto>>
    suspend fun createDispute(transactionId: String, request: CreateDisputeRequest): NetworkResult<DisputeDto>
}
