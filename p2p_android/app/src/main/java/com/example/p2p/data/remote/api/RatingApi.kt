package com.example.p2p.data.remote.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class CreateRatingRequest(
    val transaction_id: String,
    val score: Int,
    val comment: String?
)

data class RatingResponse(
    val id: String,
    val score: Int,
    val message: String
)

interface RatingApi {
    @POST("ratings")
    suspend fun createRating(@Body request: CreateRatingRequest): Response<RatingResponse>
}
