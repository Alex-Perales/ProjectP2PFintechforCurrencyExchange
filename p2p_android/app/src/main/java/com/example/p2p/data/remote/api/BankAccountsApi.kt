package com.example.p2p.data.remote.api

import com.example.p2p.data.remote.model.BankAccount
import com.example.p2p.data.remote.model.BankAccountsResponse
import com.example.p2p.data.remote.model.CreateBankAccountRequest
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
    suspend fun createAccount(@Body request: CreateBankAccountRequest): Response<BankAccount>

    @DELETE("bank-accounts/{id}")
    suspend fun deleteAccount(@Path("id") id: String): Response<Unit>
}
