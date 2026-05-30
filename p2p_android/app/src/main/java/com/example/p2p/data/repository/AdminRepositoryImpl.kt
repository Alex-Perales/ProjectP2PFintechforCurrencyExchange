package com.example.p2p.data.repository

import com.example.p2p.core.network.NetworkResult
import com.example.p2p.data.remote.api.AdminApi
import com.example.p2p.data.remote.api.AdminDashboardResponse
import com.example.p2p.data.remote.api.AdminDisputeDto
import com.example.p2p.domain.repository.AdminRepository

class AdminRepositoryImpl(
    private val api: AdminApi
) : AdminRepository {

    override suspend fun getDashboardStats(): NetworkResult<AdminDashboardResponse> {
        return try {
            val response = api.getDashboardStats()
            if (response.isSuccessful && response.body() != null) {
                NetworkResult.Success(response.body()!!)
            } else {
                NetworkResult.Error(response.code(), response.message())
            }
        } catch (e: Exception) {
            NetworkResult.Error(-1, e.message ?: "An error occurred")
        }
    }

    override suspend fun getDisputes(): NetworkResult<List<AdminDisputeDto>> {
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

    override suspend fun resolveDispute(disputeId: String, resolution: String): NetworkResult<Unit> {
        return try {
            val response = api.resolveDispute(disputeId, mapOf("resolution" to resolution))
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
