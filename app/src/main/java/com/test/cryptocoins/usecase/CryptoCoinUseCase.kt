package com.test.cryptocoins.usecase

import com.test.cryptocoins.R
import com.test.cryptocoins.common.CryptoCoinConstants
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
            filterList()
        } else {
            filterInternalList()
        }
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

    private fun filterList() {
        filterInternalList()
        var coinTokenFilterList = emptyList<CryptoCoinData>()
        when {
            filterList[CryptoCoinConstants.KEY_TOKEN_COIN] != -1 -> {
                when {
                    filterList[CryptoCoinConstants.KEY_TOKEN_COIN] == R.id.onlyTokenChip -> {
                        coinTokenFilterList = this.displayCryptoList.filter { cryptoCoinData ->
                            cryptoCoinData.type == "token"
                        }
                    }

                    filterList[CryptoCoinConstants.KEY_TOKEN_COIN] == R.id.onlyCoinChip -> {
                        coinTokenFilterList = this.displayCryptoList.filter { cryptoCoinData ->
                            cryptoCoinData.type == "coin"
                        }
                    }

                    else -> {
                        coinTokenFilterList = this.displayCryptoList.filter { cryptoCoinData ->
                            cryptoCoinData.type == "coin" || cryptoCoinData.type == "token"
                        }
                    }
                }
            } else -> {
            coinTokenFilterList = this.displayCryptoList
            }
        }
        var activeInActiveFilterList = emptyList<CryptoCoinData>()
        when {
            filterList[CryptoCoinConstants.KEY_ACTIVE_INACTIVE] != -1 -> {
                when {
                    filterList[CryptoCoinConstants.KEY_ACTIVE_INACTIVE] == R.id.activeCoinsChip -> {
                        activeInActiveFilterList = coinTokenFilterList.filter { cryptoCoinData ->
                            cryptoCoinData.isActive == true
                        }
                    }

                    filterList[CryptoCoinConstants.KEY_ACTIVE_INACTIVE] == R.id.inActiveCoinsChip -> {
                        activeInActiveFilterList = coinTokenFilterList.filter { cryptoCoinData ->
                            cryptoCoinData.isActive == false
                        }
                    }

                    else -> {
                        activeInActiveFilterList = coinTokenFilterList.filter { cryptoCoinData ->
                            cryptoCoinData.isActive == true || cryptoCoinData.isActive == false
                        }
                    }
                }
            }
            else -> {
                activeInActiveFilterList = coinTokenFilterList
            }
        }
        var newCoinFilterList = emptyList<CryptoCoinData>()
        when {
            filterList[CryptoCoinConstants.KEY_NEW_COIN] != -1 -> {
                newCoinFilterList = when (R.id.newCoinsChip) {
                    filterList[CryptoCoinConstants.KEY_NEW_COIN] -> {
                        activeInActiveFilterList.filter { cryptoCoinData ->
                            cryptoCoinData.isNew == true
                        }
                    }

                    else -> {
                        activeInActiveFilterList.filter { cryptoCoinData ->
                            cryptoCoinData.isNew == false || cryptoCoinData.isNew == true
                        }
                    }
                }
            }

            else -> {
                newCoinFilterList = activeInActiveFilterList.filter { cryptoCoinData ->
                    cryptoCoinData.isNew == false || cryptoCoinData.isNew == true
                }
            }
        }
        displayCryptoList = newCoinFilterList
    }

    override suspend fun applyChipFilters(key: Int, chipFilters: Int): UIState {
        filterList[key] = chipFilters
        filterList()
        return CryptoCoinUIState.CryptoCoinFilterList(
            cryptoCoinUIDataMapper.invoke(
                displayCryptoList
            )
        )
    }
}