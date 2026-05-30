package com.example.p2p.data.remote.dto

data class UserDto(
    val id: String,
    val email: String,
    val full_name: String?,
    val phone: String?,
    val country: String?,
    val avatar_url: String?,
    val rating: Double?,
    val total_transactions: Int?,
    val role: String,
    val kyc_verified: Boolean,
    val is_active: Boolean,
    val created_at: String
)
