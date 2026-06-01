package com.example.p2p.data.remote.api

import com.example.p2p.data.remote.model.LoginRequest
import com.example.p2p.data.remote.model.LoginResponse
import com.example.p2p.data.remote.model.RegisterRequest
import com.example.p2p.data.remote.model.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<LoginResponse>

    @POST("auth/logout")
    suspend fun logout(): Response<Unit>

    @GET("auth/me")
    suspend fun me(): Response<UserResponse>
}
