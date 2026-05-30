package com.example.p2p.data.remote.api

import com.example.p2p.data.remote.dto.BankAccountDto
import com.example.p2p.data.remote.dto.BankAccountsResponse
import com.example.p2p.data.remote.dto.CreateBankAccountRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface BankAccountsApi {

    @GET("bank-accounts")
    suspend fun listAccounts(): Response<BankAccountsResponse>

    @POST("bank-accounts")
    suspend fun createAccount(@Body request: CreateBankAccountRequest): Response<BankAccountDto>

    @DELETE("bank-accounts/{id}")
    suspend fun deleteAccount(@Path("id") id: String): Response<Unit>
}
