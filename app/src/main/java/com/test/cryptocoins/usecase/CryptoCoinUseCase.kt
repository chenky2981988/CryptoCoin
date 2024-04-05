package com.test.cryptocoins.usecase

import com.test.cryptocoins.R
import com.test.cryptocoins.common.CryptoCoinConstants
import com.test.cryptocoins.common.CryptoCoinConstants.KEY_TOKEN_COIN
import com.test.cryptocoins.common.UIState
import com.test.cryptocoins.mapper.CryptoCoinUIDataMapper
import com.test.cryptocoins.model.CryptoCoinData
import com.test.cryptocoins.model.CryptoCoinUIState
import com.test.cryptocoins.repository.CryptoCoinRepository
import javax.inject.Inject


/**
 * Created by Chirag Sidhiwala on 04/04/24.
 */
interface CryptoCoinUseCase {
    suspend fun getCryptoCoinList(): UIState
    suspend fun textFilter(filterText: String): UIState
    suspend fun applyChipFilters(key: Int, chipFilters: Int): UIState
}

class CryptoCoinUseCaseImpl @Inject constructor(
    private val cryptoCoinRepository: CryptoCoinRepository,
    private val cryptoCoinUIDataMapper: CryptoCoinUIDataMapper,
) :
    CryptoCoinUseCase {
    private val filterList: MutableList<Int> = mutableListOf(-1, -1, -1)
    private var filterText: String = ""
    private var cryptoCoinList: List<CryptoCoinData>? = null
    private var displayCryptoList: List<CryptoCoinData> = emptyList()
    override suspend fun getCryptoCoinList(): UIState {
        return runCatching {
            cryptoCoinList = cryptoCoinRepository.getCryptoCoinList()
            if (cryptoCoinList.isNullOrEmpty()) {
                CryptoCoinUIState.CryptoCoinFailure
            } else {
                displayCryptoList = cryptoCoinList ?: emptyList()
                CryptoCoinUIState.CryptoCoinSuccess(cryptoCoinUIDataMapper.invoke(displayCryptoList))
            }
        }.getOrElse {
            UIState.Error(throwable = it)
        }
    }

    override suspend fun textFilter(filterText: String): UIState {
        this.filterText = filterText
        displayCryptoList = cryptoCoinList ?: emptyList()
        if (filterList.any { it != -1 }) {
            filterListV2()
        } else {
            filterInternalList()
        }
        return CryptoCoinUIState.CryptoCoinFilterList(
            cryptoCoinUIDataMapper.invoke(
                displayCryptoList
            )
        )
    }

    override suspend fun applyChipFilters(key: Int, chipFilters: Int): UIState {
        filterList[key] = chipFilters
        filterListV2()
        return CryptoCoinUIState.CryptoCoinFilterList(
            cryptoCoinUIDataMapper.invoke(
                displayCryptoList
            )
        )
    }

    private fun filterInternalList() {
        if (filterText.isEmpty()) {
            this.displayCryptoList = cryptoCoinList ?: emptyList()
        } else {
            val filteredList = cryptoCoinList?.filter {
                (it.name?.contains(filterText, ignoreCase = true) == true) ||
                        (it.symbol?.contains(filterText, ignoreCase = true) == true)
            }
            this.displayCryptoList = filteredList ?: emptyList()
        }
    }

    private fun filterListV2() {
        filterInternalList()
        val type = getTypeFilter()
        val activeInActiveFilter = getActiveInActiveFilter()
        val newCoinFilter = getNewCoinFilter()
        val newFilterList = displayCryptoList.filter { cryptoCoinData ->
            if (type != null) {
                cryptoCoinData.type == type
            } else {
                true
            }
        }.filter { cryptoCoinData ->
            if (activeInActiveFilter != null) {
                cryptoCoinData.isActive == activeInActiveFilter
            } else {
                true
            }
        }.filter { cryptoCoinData ->
            if (newCoinFilter != null) {
                cryptoCoinData.isNew == newCoinFilter
            } else {
                true
            }
        }
        this.displayCryptoList = newFilterList
    }

    private fun getTypeFilter(): String? {
        return if (filterList[KEY_TOKEN_COIN] != -1) {
            when (filterList[KEY_TOKEN_COIN]) {
                R.id.onlyTokenChip -> "token"
                R.id.onlyCoinChip -> "coin"
                else -> null
            }
        } else {
            null
        }
    }

    private fun getActiveInActiveFilter(): Boolean? {
        return if (filterList[CryptoCoinConstants.KEY_ACTIVE_INACTIVE] != -1) {
            when (filterList[CryptoCoinConstants.KEY_ACTIVE_INACTIVE]) {
                R.id.activeCoinsChip -> true
                R.id.inActiveCoinsChip -> false
                else -> null
            }
        } else {
            null
        }
    }

    private fun getNewCoinFilter(): Boolean? {
        return if (filterList[CryptoCoinConstants.KEY_NEW_COIN] != -1) {
            when (filterList[CryptoCoinConstants.KEY_NEW_COIN]) {
                R.id.newCoinsChip -> true
                else -> null
            }
        } else {
            null
        }
    }
}