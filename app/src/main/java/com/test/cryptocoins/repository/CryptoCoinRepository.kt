package com.test.cryptocoins.repository

import com.test.cryptocoins.model.CryptoCoinData
import com.test.cryptocoins.service.CryptoCoinService
import javax.inject.Inject


/**
 * Created by Chirag Sidhiwala on 04/04/24.
 */
interface CryptoCoinRepository {
    suspend fun getCryptoCoinList(): List<CryptoCoinData>?
}

class CryptoCoinRepositoryImpl @Inject constructor(private val cryptoCoinService: CryptoCoinService) :
    CryptoCoinRepository {

    override suspend fun getCryptoCoinList(): List<CryptoCoinData>? {
        return cryptoCoinService.getCryptoCoinList()
    }
}