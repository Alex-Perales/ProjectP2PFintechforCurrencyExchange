package com.example.p2p.data.repository

import com.example.p2p.core.network.NetworkResult
import com.example.p2p.data.remote.api.UserApi
import com.example.p2p.data.remote.model.UserDto
import com.example.p2p.domain.repository.UserRepository

class UserRepositoryImpl(
    private val api: UserApi
) : UserRepository {

    override suspend fun getMe(): NetworkResult<UserDto> {
        return try {
            val response = api.getMe()
            if (response.isSuccessful && response.body() != null) {
                NetworkResult.Success(response.body()!!)
            } else {
                NetworkResult.Error(response.code(), response.message())
            }
        } catch (e: Exception) {
            NetworkResult.Error(-1, e.message ?: "An error occurred")
        }
    }

    override suspend fun updateProfile(fullName: String, phone: String?): NetworkResult<UserDto> {
        return try {
            val body = buildMap<String, String?> {
                put("full_name", fullName)
                if (phone != null) put("phone", phone)
            }
            val response = api.updateProfile(body)
            if (response.isSuccessful && response.body() != null) {
                NetworkResult.Success(response.body()!!)
            } else {
                NetworkResult.Error(response.code(), response.message())
            }
        } catch (e: Exception) {
            NetworkResult.Error(-1, e.message ?: "An error occurred")
        }
    }
}
