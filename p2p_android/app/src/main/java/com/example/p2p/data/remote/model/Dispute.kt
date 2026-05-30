package com.example.p2p.data.remote.model

data class Dispute(
    val id: String,
    val transaction_id: String,
    val initiator_id: String,
    val reason: String,
    val description: String?,
    val status: String,
    val created_at: String
)

data class DisputesResponse(
    val disputes: List<Dispute>
)

data class CreateDisputeRequest(
    val reason: String,
    val description: String?
)
