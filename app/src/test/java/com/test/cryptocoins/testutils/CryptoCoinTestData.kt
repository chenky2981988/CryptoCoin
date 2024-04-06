package com.test.cryptocoins.testutils

import android.view.View
import com.test.cryptocoins.R
import com.test.cryptocoins.model.CryptoCoinData
import com.test.cryptocoins.model.CryptoCoinUIModel


/**
 * Created by Chirag Sidhiwala on 05/04/24.
 */
object CryptoCoinTestData {
    fun getCryptoCoinList(): List<CryptoCoinData> {
        val bitcoin = CryptoCoinData(
            name = "Bitcoin",
            symbol = "BTC",
            type = "coin",
            isActive = true,
            isNew = false
        )
        val ethereum = CryptoCoinData(
            name = "Ethereum",
            symbol = "ETH",
            type = "token",
            isActive = true,
            isNew = false
        )
        val litecoin = CryptoCoinData(
            name = "Litecoin",
            symbol = "LTC",
            type = "coin",
            isActive = false,
            isNew = true
        )
        return listOf(bitcoin, ethereum, litecoin)
    }

    fun getCryptoCoinUIData(): List<CryptoCoinUIModel> {
        val bitcoin = CryptoCoinUIModel(
            name = "Bitcoin",
            symbol = "BTC",
            newTagVisibility = View.GONE,
            cryptoIcon = R.drawable.crypto_coin,
            backgroundColor = android.R.color.transparent
        )

        val ethereum = CryptoCoinUIModel(
            name = "Ethereum",
            symbol = "ETH",
            newTagVisibility = View.GONE,
            cryptoIcon = R.drawable.crypto_token,
            backgroundColor = android.R.color.transparent
        )

        val litecoin = CryptoCoinUIModel(
            name = "Litecoin",
            symbol = "LTC",
            newTagVisibility = View.VISIBLE,
            cryptoIcon = R.drawable.crypto_disabled,
            backgroundColor = R.color.light_gray
        )
        return listOf(bitcoin, ethereum, litecoin)
    }
}