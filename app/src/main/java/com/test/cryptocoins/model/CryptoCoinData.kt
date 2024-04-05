package com.test.cryptocoins.model

import com.google.gson.annotations.SerializedName


/**
 * Created by Chirag Sidhiwala on 04/04/24.
 */
data class CryptoCoinData(
    val name: String? = "Default Name",
    val symbol: String? = "Default Symbol",
    @SerializedName("is_new")
    val isNew: Boolean? = false,
    @SerializedName("is_active")
    val isActive: Boolean? = false,
    val type: String? = "Default Type",
)
