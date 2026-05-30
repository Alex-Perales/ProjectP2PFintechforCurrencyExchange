package com.example.p2p.data.remote.api

import com.example.p2p.data.remote.model.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH

interface UserApi {
    @GET("users/me")
    suspend fun getMe(): Response<UserDto>

    @PATCH("users/profile")
    suspend fun updateProfile(@Body body: Map<String, String?>): Response<UserDto>
}
