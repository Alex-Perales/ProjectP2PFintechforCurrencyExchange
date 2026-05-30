package com.example.p2p.domain.repository

import com.example.p2p.core.network.NetworkResult
import com.example.p2p.data.remote.api.RatingResponse

interface RatingRepository {
    suspend fun createRating(transactionId: String, score: Int, comment: String?): NetworkResult<RatingResponse>
}
