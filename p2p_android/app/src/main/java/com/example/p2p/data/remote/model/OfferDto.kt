package com.example.p2p.data.remote.model

data class OfferDto(
    val id: String,
    val vendor_id: String,
    val currency: String,
    val fiat_currency: String,
    val amount: Double,
    val available_amount: Double,
    val price_per_unit: Double,
    val offer_type: String,
    val status: String,
    val min_transaction: Double,
    val max_transaction: Double?,
    val payment_methods: List<String>?,
    val created_at: String,
    val vendor: UserDto? = null
)

data class OffersResponse(
    val offers: List<OfferDto>
)

data class CreateOfferRequest(
    val currency: String,
    val fiat_currency: String,
    val amount: Double,
    val price_per_unit: Double,
    val offer_type: String,
    val min_transaction: Double,
    val max_transaction: Double?,
    val payment_methods: List<String>
)
