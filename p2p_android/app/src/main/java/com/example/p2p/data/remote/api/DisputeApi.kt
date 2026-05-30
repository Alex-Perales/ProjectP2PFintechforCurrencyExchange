package com.example.p2p.data.remote.api

import com.example.p2p.data.remote.model.DisputeDto
import com.example.p2p.data.remote.model.DisputesResponse
import com.example.p2p.data.remote.model.CreateDisputeRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface DisputeApi {
    @GET("transactions/disputes")
    suspend fun getDisputes(): Response<DisputesResponse>

    @POST("transactions/{id}/dispute")
    suspend fun createDispute(
        @Path("id") transactionId: String,
        @Body request: CreateDisputeRequest
    ): Response<DisputeDto>
}
