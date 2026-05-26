package com.example.p2p.domain.repository

import com.example.p2p.core.network.NetworkResult
import com.example.p2p.data.remote.dto.LoginResponse

interface AuthRepository {
    suspend fun login(email: String, password: String): NetworkResult<LoginResponse>
    suspend fun logout()
    suspend fun isLoggedIn(): Boolean
}
