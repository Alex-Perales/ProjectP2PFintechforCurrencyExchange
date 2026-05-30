package com.example.p2p.data.repository

import com.example.p2p.core.network.NetworkResult
import com.example.p2p.data.remote.api.OfferApi
import com.example.p2p.data.remote.dto.CreateOfferRequest
import com.example.p2p.data.remote.dto.OfferDto
import com.example.p2p.domain.repository.OfferRepository

class OfferRepositoryImpl(
    private val api: OfferApi
) : OfferRepository {

    override suspend fun listOffers(
        currency: String?,
        fiatCurrency: String?,
        offerType: String?
    ): NetworkResult<List<OfferDto>> {
        return try {
            val response = api.listOffers(currency, fiatCurrency, offerType)
            if (response.isSuccessful && response.body() != null) {
                NetworkResult.Success(response.body()!!.offers)
            } else {
                NetworkResult.Error(response.code(), response.message())
            }
        } catch (e: Exception) {
            NetworkResult.Error(-1, e.message ?: "An error occurred")
        }
    }

    override suspend fun createOffer(request: CreateOfferRequest): NetworkResult<OfferDto> {
        return try {
            val response = api.createOffer(request)
            if (response.isSuccessful && response.body() != null) {
                NetworkResult.Success(response.body()!!)
            } else {
                NetworkResult.Error(response.code(), response.message())
            }
        } catch (e: Exception) {
            NetworkResult.Error(-1, e.message ?: "An error occurred")
        }
    }

    override suspend fun getMyOffers(): NetworkResult<List<OfferDto>> {
        return try {
            val response = api.myOffers()
            if (response.isSuccessful && response.body() != null) {
                NetworkResult.Success(response.body()!!.offers)
            } else {
                NetworkResult.Error(response.code(), response.message())
            }
        } catch (e: Exception) {
            NetworkResult.Error(-1, e.message ?: "An error occurred")
        }
    }

    override suspend fun matchOffer(currency: String, fiatCurrency: String): NetworkResult<OfferDto> {
        return try {
            val response = api.matchOffer(mapOf("currency" to currency, "fiat_currency" to fiatCurrency))
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
