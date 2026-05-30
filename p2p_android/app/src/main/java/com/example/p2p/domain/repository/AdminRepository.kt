package com.example.p2p.domain.repository

import com.example.p2p.core.network.NetworkResult
import com.example.p2p.data.remote.api.AdminDashboardResponse
import com.example.p2p.data.remote.api.AdminDisputeDto

interface AdminRepository {
    suspend fun getDashboardStats(): NetworkResult<AdminDashboardResponse>
    suspend fun getDisputes(): NetworkResult<List<AdminDisputeDto>>
    suspend fun resolveDispute(disputeId: String, resolution: String): NetworkResult<Unit>
}
