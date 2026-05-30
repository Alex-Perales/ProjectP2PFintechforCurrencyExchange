package com.example.p2p.data.remote.api

import retrofit2.Response
import retrofit2.http.*

data class AdminDashboardResponse(
    val total_users: Int,
    val total_transactions: Int,
    val pending_disputes: Int,
    val total_volume: Double
)

data class AdminDispute(
    val id: String,
    val transaction_id: String,
    val initiator_id: String,
    val reason: String,
    val description: String?,
    val status: String,
    val created_at: String
)

data class AdminDisputesResponse(
    val disputes: List<AdminDispute>
)

interface AdminApi {
    @GET("admin/dashboard")
    suspend fun getDashboardStats(): Response<AdminDashboardResponse>

    @GET("admin/disputes")
    suspend fun getDisputes(): Response<AdminDisputesResponse>

    @PATCH("admin/disputes/{id}/resolve")
    suspend fun resolveDispute(
        @Path("id") disputeId: String,
        @Body body: Map<String, String>
    ): Response<Map<String, String>>
}
