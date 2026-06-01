package com.example.p2p.data.repository

import com.example.p2p.core.network.NetworkResult
import com.example.p2p.data.remote.api.CreateRatingRequest
import com.example.p2p.data.remote.api.RatingApi
import com.example.p2p.data.remote.api.RatingResponse
import com.example.p2p.domain.repository.RatingRepository

class RatingRepositoryImpl(
    private val api: RatingApi
) : RatingRepository {

    override suspend fun createRating(transactionId: String, score: Int, comment: String?): NetworkResult<RatingResponse> {
        return try {
            val response = api.createRating(CreateRatingRequest(transactionId, score, comment))
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
