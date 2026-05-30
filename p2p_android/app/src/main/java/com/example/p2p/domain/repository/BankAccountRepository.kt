package com.example.p2p.domain.repository

import com.example.p2p.core.network.NetworkResult
import com.example.p2p.data.remote.dto.BankAccountDto
import com.example.p2p.data.remote.dto.CreateBankAccountRequest

interface BankAccountRepository {
    suspend fun listAccounts(): NetworkResult<List<BankAccountDto>>
    suspend fun createAccount(request: CreateBankAccountRequest): NetworkResult<BankAccountDto>
    suspend fun deleteAccount(id: String): NetworkResult<Unit>
}
