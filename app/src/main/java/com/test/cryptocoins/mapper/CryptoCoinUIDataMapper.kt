package com.test.cryptocoins.mapper

import android.view.View
import com.test.cryptocoins.R
import com.test.cryptocoins.model.CryptoCoinData
import com.test.cryptocoins.model.CryptoCoinUIModel
import java.util.function.Function
import javax.inject.Inject


/**
 * Created by Chirag Sidhiwala on 05/04/24.
 */
interface CryptoCoinUIDataMapper : Function1<List<CryptoCoinData>, List<CryptoCoinUIModel>>

class CryptoCoinUIDataMapperImpl @Inject constructor() : CryptoCoinUIDataMapper {
    override fun invoke(cryptoCoinDataList: List<CryptoCoinData>): List<CryptoCoinUIModel> {
        return cryptoCoinDataList.map {
            var icon = R.drawable.crypto_coin
            var backgroundColor = android.R.color.transparent
            when {
                it.type.equals(
                    "token",
                    ignoreCase = true
                ) && it.isActive == true -> {
                    icon = R.drawable.crypto_token
                }

                it.isActive == false -> {
                    icon = R.drawable.crypto_disabled
                    backgroundColor = R.color.light_gray
                }
            }
            CryptoCoinUIModel(
                name = it.name ?: "",
                symbol = it.symbol ?: "",
                newTagVisibility = when (it.isNew) {
                    true -> View.VISIBLE
                    else -> View.GONE
                },
                cryptoIcon = icon,
                backgroundColor = backgroundColor
            )
        }
    }
}