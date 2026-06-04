package com.example.p2p.data.remote.api

import com.example.p2p.data.remote.model.Complaint
import com.example.p2p.data.remote.model.ComplaintsResponse
import com.example.p2p.data.remote.model.CreateComplaintRequest
import retrofit2.Response
import retrofit2.http.*

interface ComplaintApi {
    @POST("complaints")
    suspend fun createComplaint(
        @Body request: CreateComplaintRequest
    ): Response<Complaint>

    @GET("complaints/my-complaints")
    suspend fun getMyComplaints(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20
    ): Response<ComplaintsResponse>
}