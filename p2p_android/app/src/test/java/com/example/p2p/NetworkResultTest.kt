package com.example.p2p

import com.example.p2p.core.network.NetworkResult
import org.junit.Assert.*
import org.junit.Test

class NetworkResultTest {

    @Test
    fun `NetworkResult Success holds data`() {
        val result = NetworkResult.Success("token_abc")
        assertTrue(result is NetworkResult.Success)
        assertEquals("token_abc", result.data)
    }

    @Test
    fun `NetworkResult Error holds code and message`() {
        val result = NetworkResult.Error(401, "Unauthorized")
        assertTrue(result is NetworkResult.Error)
        assertEquals(401, result.code)
        assertEquals("Unauthorized", result.message)
    }

    @Test
    fun `NetworkResult Loading is singleton`() {
        assertTrue(NetworkResult.Loading is NetworkResult.Loading)
    }
}
