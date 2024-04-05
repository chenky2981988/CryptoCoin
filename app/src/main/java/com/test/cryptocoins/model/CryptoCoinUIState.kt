package com.test.cryptocoins.model

import com.test.cryptocoins.common.UIState


/**
 * Created by Chirag Sidhiwala on 04/04/24.
 */
sealed class CryptoCoinUIState : UIState {
    data class CryptoCoinSuccess(val cryptoCoinList: List<CryptoCoinUIModel>) : CryptoCoinUIState()
    data object CryptoCoinFailure : CryptoCoinUIState()
    data class CryptoCoinFilterList(val cryptoCoinList: List<CryptoCoinUIModel>) : CryptoCoinUIState()
}