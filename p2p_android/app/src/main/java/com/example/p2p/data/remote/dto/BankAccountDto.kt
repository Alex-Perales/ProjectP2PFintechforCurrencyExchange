package com.example.p2p.data.remote.dto

data class BankAccountDto(
    val id: String,
    val user_id: String,
    val bank_name: String,
    val account_number: String,
    val account_holder: String,
    val account_type: String,
    val currency: String,
    val is_primary: Boolean,
    val is_verified: Boolean,
    val created_at: String
)

data class BankAccountsResponse(
    val bank_accounts: List<BankAccountDto>
)

data class CreateBankAccountRequest(
    val bank_name: String,
    val account_number: String,
    val account_holder: String,
    val account_type: String = "savings",
    val currency: String = "PEN",
    val is_primary: Boolean = false
)
