package com.example.p2p.domain.repository

import com.example.p2p.core.network.NetworkResult
import com.example.p2p.data.remote.model.CreateOfferRequest
import com.example.p2p.data.remote.model.Offer

interface OfferRepository {
    suspend fun listOffers(currency: String? = null, fiatCurrency: String? = null, offerType: String? = null): NetworkResult<List<Offer>>
    suspend fun createOffer(request: CreateOfferRequest): NetworkResult<Offer>
    suspend fun getMyOffers(): NetworkResult<List<Offer>>
    suspend fun matchOffer(currency: String, fiatCurrency: String): NetworkResult<Offer>
    suspend fun pauseOffer(offerId: String): NetworkResult<Offer>
    suspend fun resumeOffer(offerId: String): NetworkResult<Offer>
    suspend fun deleteOffer(offerId: String): NetworkResult<Unit>
}
