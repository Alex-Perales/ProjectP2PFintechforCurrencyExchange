package com.example.p2p.data.remote.dto

data class UserDto(
    val id: String,
    val email: String,
    val full_name: String?,
    val phone: String?,
    val country: String? = null,
    val avatar_url: String? = null,
    val rating: Double? = null,
    val total_transactions: Int? = null,
    val role: String,
    val kyc_verified: Boolean = false,
    val is_active: Boolean = true,
    val created_at: String? = null
)
