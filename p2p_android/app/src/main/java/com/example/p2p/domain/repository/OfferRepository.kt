package com.example.p2p.domain.repository

import com.example.p2p.core.network.NetworkResult
import com.example.p2p.data.remote.model.CreateOfferRequest
import com.example.p2p.data.remote.model.OfferDto

interface OfferRepository {
    suspend fun listOffers(currency: String? = null, fiatCurrency: String? = null, offerType: String? = null): NetworkResult<List<OfferDto>>
    suspend fun createOffer(request: CreateOfferRequest): NetworkResult<OfferDto>
    suspend fun getMyOffers(): NetworkResult<List<OfferDto>>
    suspend fun matchOffer(currency: String, fiatCurrency: String): NetworkResult<OfferDto>
    suspend fun pauseOffer(offerId: String): NetworkResult<OfferDto>
    suspend fun resumeOffer(offerId: String): NetworkResult<OfferDto>
    suspend fun deleteOffer(offerId: String): NetworkResult<Unit>
}
