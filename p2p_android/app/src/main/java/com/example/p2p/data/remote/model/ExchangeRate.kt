package com.example.p2p.data.remote.model

data class ExchangeRate(
    val id: String,
    val from_currency: String,
    val to_currency: String,
    val rate: Double,
    val updated_at: String?
)

data class ExchangeRatesResponse(
    val rates: List<ExchangeRate>
)
