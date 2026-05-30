package com.example.p2p.data.repository

import com.example.p2p.core.network.NetworkResult
import com.example.p2p.data.remote.api.DisputeApi
import com.example.p2p.data.remote.dto.DisputeDto
import com.example.p2p.data.remote.dto.CreateDisputeRequest
import com.example.p2p.domain.repository.DisputeRepository

class DisputeRepositoryImpl(
    private val api: DisputeApi
) : DisputeRepository {

    override suspend fun getDisputes(): NetworkResult<List<DisputeDto>> {
        return try {
            val response = api.getDisputes()
            if (response.isSuccessful && response.body() != null) {
                NetworkResult.Success(response.body()!!.disputes)
            } else {
                NetworkResult.Error(response.code(), response.message())
            }
        } catch (e: Exception) {
            NetworkResult.Error(-1, e.message ?: "An error occurred")
        }
    }

    override suspend fun createDispute(transactionId: String, request: CreateDisputeRequest): NetworkResult<DisputeDto> {
        return try {
            val response = api.createDispute(transactionId, request)
            if (response.isSuccessful && response.body() != null) {
                NetworkResult.Success(response.body()!!)
            } else {
                NetworkResult.Error(response.code(), response.message())
            }
        } catch (e: Exception) {
            NetworkResult.Error(-1, e.message ?: "An error occurred")
        }
    }
}
