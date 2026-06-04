package com.example.p2p.data.remote.model

data class Complaint(
    val id: String,
    val user_id: String,
    val type: String,
    val description: String,
    val status: String,
    val admin_note: String?,
    val created_at: String,
    val updated_at: String
)

data class ComplaintsResponse(
    val complaints: List<Complaint>,
    val pagination: PaginationMeta? = null
)

data class CreateComplaintRequest(
    val type: String,
    val description: String
)

object ComplaintType {
    const val TRANSACTION_ISSUE = "transaction_issue"
    const val PLATFORM_ERROR    = "platform_error"
    const val PAYMENT_ISSUE     = "payment_issue"
    const val ACCOUNT_ISSUE     = "account_issue"
    const val OTHER             = "other"

    fun label(type: String) = when (type) {
        TRANSACTION_ISSUE -> "Problema con transacción"
        PLATFORM_ERROR    -> "Error en plataforma"
        PAYMENT_ISSUE     -> "Problema de pago"
        ACCOUNT_ISSUE     -> "Problema con cuenta"
        OTHER             -> "Otro motivo"
        else              -> type
    }

    val all = listOf(
        TRANSACTION_ISSUE,
        PLATFORM_ERROR,
        PAYMENT_ISSUE,
        ACCOUNT_ISSUE,
        OTHER
    )
}