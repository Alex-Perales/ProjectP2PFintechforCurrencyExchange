package com.example.p2p.domain.repository

import com.example.p2p.core.network.NetworkResult
import com.example.p2p.data.remote.dto.UserDto

interface UserRepository {
    suspend fun getMe(): NetworkResult<UserDto>
}
