package com.test.cryptocoins.repository

import com.test.cryptocoins.service.CryptoCoinService
import com.test.cryptocoins.testutils.CryptoCoinTestData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock

/**
 * Created by Chirag Sidhiwala on 05/04/24.
 */
@ExperimentalCoroutinesApi
class CryptoCoinRepositoryTest {
    private lateinit var cryptoCoinRepository: CryptoCoinRepository
    private val cryptoCoinService: CryptoCoinService = mock()

    @BeforeEach
    fun setUp() {
        cryptoCoinRepository = CryptoCoinRepositoryImpl(cryptoCoinService)
    }

    @Test
    fun getCryptoCoinListSuccess() = runTest {
        val testData = CryptoCoinTestData.getCryptoCoinList()
        `when`(cryptoCoinService.getCryptoCoinList()).thenReturn(testData)
        val actualResult = cryptoCoinRepository.getCryptoCoinList()
        assertEquals(testData, actualResult)
    }

    @Test
    fun getCryptoCoinListNull() = runTest {
        `when`(cryptoCoinService.getCryptoCoinList()).thenReturn(null)
        val actualResult = cryptoCoinRepository.getCryptoCoinList()
        assertNull(actualResult)
    }
}