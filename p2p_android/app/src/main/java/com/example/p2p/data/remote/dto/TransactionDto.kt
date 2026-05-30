package com.example.p2p.data.remote.dto

data class TransactionDto(
    val id: String,
    val offer_id: String,
    val buyer_id: String,
    val vendor_id: String,
    val amount_from: Double,
    val amount_to: Double,
    val exchange_rate: Double,
    val status: String,
    val buyer_payment_account: String?,
    val vendor_payment_account: String?,
    val created_at: String,
    val updated_at: String?
)

data class TransactionsResponse(
    val transactions: List<TransactionDto>
)

data class CreateTransactionRequest(
    val offer_id: String,
    val amount_from: Double,
    val amount_to: Double,
    val buyer_payment_account: String,
    val vendor_payment_account: String
)
