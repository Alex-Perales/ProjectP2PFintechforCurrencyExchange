package com.example.p2p.data.remote.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
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

data class ReceivedRating(
    val id: String,
    val score: Int,
    val comment: String?,
    val rater_name: String?,
    val created_at: String?
)

data class ReceivedRatingsResponse(
    val ratings: List<ReceivedRating>,
    val average: Double,
    val total: Int,
    val distribution: Map<String, Int>
)

interface RatingApi {
    @POST("ratings")
    suspend fun createRating(@Body request: CreateRatingRequest): Response<RatingResponse>

    @GET("ratings/received")
    suspend fun getReceivedRatings(): Response<ReceivedRatingsResponse>
}
