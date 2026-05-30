package com.example.p2p.data.remote.api

import com.example.p2p.data.remote.dto.UserDto
import retrofit2.Response
import retrofit2.http.GET

interface UserApi {
    @GET("users/me")
    suspend fun getMe(): Response<UserDto>
}
