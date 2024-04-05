package com.test.cryptocoins.service

import com.test.cryptocoins.model.CryptoCoinData
import retrofit2.http.GET


/**
 * Created by Chirag Sidhiwala on 04/04/24.
 */
interface CryptoCoinService {
    @GET(".")
    suspend fun getCryptoCoinList(): List<CryptoCoinData>?
}