package com.test.cryptocoins.model

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes


/**
 * Created by Chirag Sidhiwala on 05/04/24.
 */
data class CryptoCoinUIModel(
    val name: String,
    val symbol: String,
    val newTagVisibility: Int,
    @DrawableRes val cryptoIcon: Int,
    @ColorRes val backgroundColor: Int,
)
