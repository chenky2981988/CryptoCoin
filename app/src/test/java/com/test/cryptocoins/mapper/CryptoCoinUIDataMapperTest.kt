package com.test.cryptocoins.mapper

import android.view.View
import com.test.cryptocoins.R
import com.test.cryptocoins.testutils.CryptoCoinTestData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Created by Chirag Sidhiwala on 06/04/24.
 */
@ExperimentalCoroutinesApi
class CryptoCoinUIDataMapperTest {
    private lateinit var cryptoCoinUIDataMapper: CryptoCoinUIDataMapper

    @BeforeEach
    fun setUp() {
        cryptoCoinUIDataMapper = CryptoCoinUIDataMapperImpl()
    }

    @Test
    fun `test invoke`() {
        val cryptoCoinDataList = CryptoCoinTestData.getCryptoCoinList()
        val actualResult = cryptoCoinUIDataMapper(cryptoCoinDataList)
        assertEquals(3, actualResult.size)
        assertEquals("Bitcoin", actualResult[0].name)
        assertEquals("BTC", actualResult[0].symbol)
        assertEquals(View.GONE, actualResult[0].newTagVisibility)
        assertEquals(R.drawable.crypto_coin, actualResult[0].cryptoIcon)
        assertEquals(android.R.color.transparent, actualResult[0].backgroundColor)
        assertEquals("Ethereum", actualResult[1].name)
        assertEquals("ETH", actualResult[1].symbol)
        assertEquals(View.GONE, actualResult[1].newTagVisibility)
        assertEquals(R.drawable.crypto_token, actualResult[1].cryptoIcon)
        assertEquals(android.R.color.transparent, actualResult[1].backgroundColor)
        assertEquals("Litecoin", actualResult[2].name)
        assertEquals("LTC", actualResult[2].symbol)
        assertEquals(View.VISIBLE, actualResult[2].newTagVisibility)
        assertEquals(R.drawable.crypto_disabled, actualResult[2].cryptoIcon)
        assertEquals(R.color.light_gray, actualResult[2].backgroundColor)
    }
}