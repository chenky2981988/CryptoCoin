package com.test.cryptocoins.usecase

import com.test.cryptocoins.common.UIState
import com.test.cryptocoins.model.CryptoCoinUIState
import com.test.cryptocoins.repository.CryptoCoinRepository
import javax.inject.Inject


/**
 * Created by Chirag Sidhiwala on 04/04/24.
 */
interface CryptoCoinUseCase {
    suspend fun getCryptoCoinList(): UIState
}

class CryptoCoinUseCaseImpl @Inject constructor(private val cryptoCoinRepository: CryptoCoinRepository) :
    CryptoCoinUseCase {
    override suspend fun getCryptoCoinList(): UIState {
        return runCatching {
            val cryptoCoinResult = cryptoCoinRepository.getCryptoCoinList()
            if (cryptoCoinResult.isNullOrEmpty()) {
                CryptoCoinUIState.CryptoCoinFailure
            } else {
                CryptoCoinUIState.CryptoCoinSuccess(cryptoCoinResult)
            }
        }.getOrElse {
            UIState.Error(throwable = it)
        }
    }
}