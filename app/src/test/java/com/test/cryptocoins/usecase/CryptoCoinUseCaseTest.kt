package com.test.cryptocoins.usecase

import android.view.View
import com.test.cryptocoins.R
import com.test.cryptocoins.common.CryptoCoinConstants
import com.test.cryptocoins.common.UIState
import com.test.cryptocoins.mapper.CryptoCoinUIDataMapper
import com.test.cryptocoins.model.CryptoCoinData
import com.test.cryptocoins.model.CryptoCoinUIModel
import com.test.cryptocoins.model.CryptoCoinUIState
import com.test.cryptocoins.repository.CryptoCoinRepository
import com.test.cryptocoins.testutils.CryptoCoinTestData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock

/**
 * Created by Chirag Sidhiwala on 05/04/24.
 */
@ExperimentalCoroutinesApi
class CryptoCoinUseCaseTest {
    private lateinit var cryptoCoinUseCase: CryptoCoinUseCase
    private val cryptoCoinRepository: CryptoCoinRepository = mock()
    private val cryptoCoinUIDataMapper: CryptoCoinUIDataMapper = mock()

    @BeforeEach
    fun setUp() {
        cryptoCoinUseCase = CryptoCoinUseCaseImpl(cryptoCoinRepository, cryptoCoinUIDataMapper)
    }

    @Test
    fun getCryptoCoinListEmpty() = runTest {
        val testData = emptyList<CryptoCoinData>()
        `when`(cryptoCoinRepository.getCryptoCoinList()).thenReturn(testData)
        val actualResult = cryptoCoinUseCase.getCryptoCoinList()
        assertEquals(CryptoCoinUIState.CryptoCoinFailure, actualResult)
    }

    @Test
    fun getCryptoCoinListNull() = runTest {
        `when`(cryptoCoinRepository.getCryptoCoinList()).thenReturn(null)
        val actualResult = cryptoCoinUseCase.getCryptoCoinList()
        assertEquals(CryptoCoinUIState.CryptoCoinFailure, actualResult)
    }

    @Test
    fun getCryptoCoinListSuccess() = runTest {
        val testData = CryptoCoinTestData.getCryptoCoinList()
        `when`(cryptoCoinRepository.getCryptoCoinList()).thenReturn(testData)
        `when`(cryptoCoinUIDataMapper.invoke(testData)).thenReturn(CryptoCoinTestData.getCryptoCoinUIData())
        val actualResult = cryptoCoinUseCase.getCryptoCoinList()
        assertEquals(
            CryptoCoinUIState.CryptoCoinSuccess(CryptoCoinTestData.getCryptoCoinUIData()),
            actualResult
        )
    }

    @Test
    fun getCryptoCoinListError() = runTest {
        `when`(cryptoCoinRepository.getCryptoCoinList()).thenThrow(RuntimeException("Error"))
        val actualResult = cryptoCoinUseCase.getCryptoCoinList()
        assertTrue(actualResult is UIState.Error)
    }

    @Test
    fun `searchCoinNameTextFilterWithAllFilterListValues-1`() = runTest {
        val testData = CryptoCoinTestData.getCryptoCoinList()
        val expectedResult = listOf(
            CryptoCoinUIModel(
                name = "Bitcoin",
                symbol = "BTC",
                newTagVisibility = View.GONE,
                cryptoIcon = R.drawable.crypto_coin,
                backgroundColor = android.R.color.transparent
            )
        )
        `when`(cryptoCoinRepository.getCryptoCoinList()).thenReturn(testData)
        `when`(
            cryptoCoinUIDataMapper.invoke(
                listOf(
                    CryptoCoinData(
                        name = "Bitcoin",
                        symbol = "BTC",
                        type = "coin",
                        isActive = true,
                        isNew = false
                    )
                )
            )
        ).thenReturn(expectedResult)
        cryptoCoinUseCase.getCryptoCoinList()
        val actualResult = cryptoCoinUseCase.searchTextFilter("Bitcoin")
        assertEquals(CryptoCoinUIState.CryptoCoinFilterList(expectedResult), actualResult)
    }

    @Test
    fun `searchNotAvailableTextFilterWithAllFilterListValues-1`() = runTest {
        val testData = CryptoCoinTestData.getCryptoCoinList()
        val expectedResult = emptyList<CryptoCoinUIModel>()
        `when`(cryptoCoinRepository.getCryptoCoinList()).thenReturn(testData)
        `when`(
            cryptoCoinUIDataMapper.invoke(emptyList())
        ).thenReturn(expectedResult)
        cryptoCoinUseCase.getCryptoCoinList()
        val actualResult = cryptoCoinUseCase.searchTextFilter("Random")
        assertEquals(CryptoCoinUIState.CryptoCoinFilterList(expectedResult), actualResult)
    }

    @Test
    fun testRandomNotAvailableTextSearchOnSelectedChipFilter() = runTest {
        val testData = CryptoCoinTestData.getCryptoCoinList()
        `when`(cryptoCoinRepository.getCryptoCoinList()).thenReturn(testData)
        `when`(cryptoCoinUIDataMapper.invoke(testData)).thenReturn(CryptoCoinTestData.getCryptoCoinUIData())
        cryptoCoinUseCase.getCryptoCoinList()

        `when`(
            cryptoCoinUIDataMapper.invoke(
                emptyList()
            )
        ).thenReturn(
            emptyList()
        )
        cryptoCoinUseCase.searchTextFilter("Random")

        val actualResult = cryptoCoinUseCase.applyChipFilters(
            CryptoCoinConstants.KEY_TOKEN_COIN,
            R.id.onlyCoinChip
        )
        assertEquals(CryptoCoinUIState.CryptoCoinFilterList(emptyList()), actualResult)
    }

    @Test
    fun searchCoinNameTextFilterWith1ChipFilterActive() = runTest {
        val testData = CryptoCoinTestData.getCryptoCoinList()
        val expectedResult = listOf(
            CryptoCoinUIModel(
                name = "Ethereum",
                symbol = "ETH",
                newTagVisibility = View.GONE,
                cryptoIcon = R.drawable.crypto_token,
                backgroundColor = android.R.color.transparent
            )
        )
        `when`(cryptoCoinRepository.getCryptoCoinList()).thenReturn(testData)
        `when`(
            cryptoCoinUIDataMapper.invoke(
                listOf(
                    CryptoCoinData(
                        name = "Ethereum",
                        symbol = "ETH",
                        type = "token",
                        isActive = true,
                        isNew = false
                    )
                )
            )
        ).thenReturn(expectedResult)
        cryptoCoinUseCase.getCryptoCoinList()
        cryptoCoinUseCase.applyChipFilters(
            CryptoCoinConstants.KEY_TOKEN_COIN,
            R.id.onlyTokenChip
        )
        val actualResult = cryptoCoinUseCase.searchTextFilter("Ethereum")
        assertEquals(CryptoCoinUIState.CryptoCoinFilterList(expectedResult), actualResult)
    }

    @Test
    fun searchCoinNameTextFilterWith1ChipFilterActiveAndEmptyResult() = runTest {
        val testData = CryptoCoinTestData.getCryptoCoinList()
        `when`(cryptoCoinRepository.getCryptoCoinList()).thenReturn(testData)
        `when`(cryptoCoinUIDataMapper.invoke(testData)).thenReturn(CryptoCoinTestData.getCryptoCoinUIData())
        cryptoCoinUseCase.getCryptoCoinList()

        `when`(
            cryptoCoinUIDataMapper.invoke(
                listOf(
                    CryptoCoinData(
                        name = "Bitcoin",
                        symbol = "BTC",
                        type = "coin",
                        isActive = true,
                        isNew = false
                    ), CryptoCoinData(
                        name = "Litecoin",
                        symbol = "LTC",
                        type = "coin",
                        isActive = false,
                        isNew = true
                    )
                )
            )
        ).thenReturn(
            listOf(
                CryptoCoinUIModel(
                    name = "Bitcoin",
                    symbol = "BTC",
                    newTagVisibility = View.GONE,
                    cryptoIcon = R.drawable.crypto_coin,
                    backgroundColor = android.R.color.transparent
                ), CryptoCoinUIModel(
                    name = "Litecoin",
                    symbol = "LTC",
                    newTagVisibility = View.VISIBLE,
                    cryptoIcon = R.drawable.crypto_disabled,
                    backgroundColor = R.color.light_gray
                )
            )
        )
        cryptoCoinUseCase.applyChipFilters(
            CryptoCoinConstants.KEY_TOKEN_COIN,
            R.id.onlyCoinChip
        )

        `when`(cryptoCoinUIDataMapper.invoke(emptyList())).thenReturn(emptyList())
        val actualResult = cryptoCoinUseCase.searchTextFilter("Ethereum")
        assertEquals(CryptoCoinUIState.CryptoCoinFilterList(emptyList()), actualResult)
    }

    @Test
    fun searchCoinNameTextFilterWith1ChipFilter() = runTest {
        val testData = CryptoCoinTestData.getCryptoCoinList()
        `when`(cryptoCoinRepository.getCryptoCoinList()).thenReturn(testData)
        `when`(cryptoCoinUIDataMapper.invoke(testData)).thenReturn(CryptoCoinTestData.getCryptoCoinUIData())
        cryptoCoinUseCase.getCryptoCoinList()

        `when`(
            cryptoCoinUIDataMapper.invoke(
                listOf(
                    CryptoCoinData(
                        name = "Bitcoin",
                        symbol = "BTC",
                        type = "coin",
                        isActive = true,
                        isNew = false
                    ), CryptoCoinData(
                        name = "Litecoin",
                        symbol = "LTC",
                        type = "coin",
                        isActive = false,
                        isNew = true
                    )
                )
            )
        ).thenReturn(
            listOf(
                CryptoCoinUIModel(
                    name = "Bitcoin",
                    symbol = "BTC",
                    newTagVisibility = View.GONE,
                    cryptoIcon = R.drawable.crypto_coin,
                    backgroundColor = android.R.color.transparent
                ), CryptoCoinUIModel(
                    name = "Litecoin",
                    symbol = "LTC",
                    newTagVisibility = View.VISIBLE,
                    cryptoIcon = R.drawable.crypto_disabled,
                    backgroundColor = R.color.light_gray
                )
            )
        )
        cryptoCoinUseCase.applyChipFilters(
            CryptoCoinConstants.KEY_TOKEN_COIN,
            R.id.onlyCoinChip
        )

        `when`(
            cryptoCoinUIDataMapper.invoke(
                listOf(
                    CryptoCoinData(
                        name = "Bitcoin",
                        symbol = "BTC",
                        type = "coin",
                        isActive = true,
                        isNew = false
                    )
                )
            )
        ).thenReturn(
            listOf(
                CryptoCoinUIModel(
                    name = "Bitcoin",
                    symbol = "BTC",
                    newTagVisibility = View.GONE,
                    cryptoIcon = R.drawable.crypto_coin,
                    backgroundColor = android.R.color.transparent
                )
            )
        )
        val actualResult = cryptoCoinUseCase.searchTextFilter("BTC")
        assertEquals(
            CryptoCoinUIState.CryptoCoinFilterList(
                listOf(
                    CryptoCoinUIModel(
                        name = "Bitcoin",
                        symbol = "BTC",
                        newTagVisibility = View.GONE,
                        cryptoIcon = R.drawable.crypto_coin,
                        backgroundColor = android.R.color.transparent
                    )
                )
            ), actualResult
        )
    }

    @Test
    fun searchCoinNameTextFilterThanActive1ChipFilter() = runTest {
        val testData = CryptoCoinTestData.getCryptoCoinList()
        `when`(cryptoCoinRepository.getCryptoCoinList()).thenReturn(testData)
        `when`(cryptoCoinUIDataMapper.invoke(testData)).thenReturn(CryptoCoinTestData.getCryptoCoinUIData())
        cryptoCoinUseCase.getCryptoCoinList()

        `when`(
            cryptoCoinUIDataMapper.invoke(
                listOf(
                    CryptoCoinData(
                        name = "Bitcoin",
                        symbol = "BTC",
                        type = "coin",
                        isActive = true,
                        isNew = false
                    ), CryptoCoinData(
                        name = "Litecoin",
                        symbol = "LTC",
                        type = "coin",
                        isActive = false,
                        isNew = true
                    )
                )
            )
        ).thenReturn(
            listOf(
                CryptoCoinUIModel(
                    name = "Bitcoin",
                    symbol = "BTC",
                    newTagVisibility = View.GONE,
                    cryptoIcon = R.drawable.crypto_coin,
                    backgroundColor = android.R.color.transparent
                ), CryptoCoinUIModel(
                    name = "Litecoin",
                    symbol = "LTC",
                    newTagVisibility = View.VISIBLE,
                    cryptoIcon = R.drawable.crypto_disabled,
                    backgroundColor = R.color.light_gray
                )
            )
        )
        cryptoCoinUseCase.searchTextFilter("coin")

        `when`(
            cryptoCoinUIDataMapper.invoke(
                listOf(
                    CryptoCoinData(
                        name = "Litecoin",
                        symbol = "LTC",
                        type = "coin",
                        isActive = false,
                        isNew = true
                    )
                )
            )
        ).thenReturn(
            listOf(
                CryptoCoinUIModel(
                    name = "Litecoin",
                    symbol = "LTC",
                    newTagVisibility = View.VISIBLE,
                    cryptoIcon = R.drawable.crypto_disabled,
                    backgroundColor = R.color.light_gray
                )
            )
        )
        val actualResult = cryptoCoinUseCase.applyChipFilters(
            CryptoCoinConstants.KEY_ACTIVE_INACTIVE,
            R.id.inActiveCoinsChip
        )
        assertEquals(
            CryptoCoinUIState.CryptoCoinFilterList(
                listOf(
                    CryptoCoinUIModel(
                        name = "Litecoin",
                        symbol = "LTC",
                        newTagVisibility = View.VISIBLE,
                        cryptoIcon = R.drawable.crypto_disabled,
                        backgroundColor = R.color.light_gray
                    )
                )
            ), actualResult
        )
    }

    @Test
    fun searchCoinNameTextFilterThanActiveNewCoinFilter() = runTest {
        val testData = CryptoCoinTestData.getCryptoCoinList()
        `when`(cryptoCoinRepository.getCryptoCoinList()).thenReturn(testData)
        `when`(cryptoCoinUIDataMapper.invoke(testData)).thenReturn(CryptoCoinTestData.getCryptoCoinUIData())
        cryptoCoinUseCase.getCryptoCoinList()

        `when`(
            cryptoCoinUIDataMapper.invoke(
                listOf(
                    CryptoCoinData(
                        name = "Bitcoin",
                        symbol = "BTC",
                        type = "coin",
                        isActive = true,
                        isNew = false
                    ), CryptoCoinData(
                        name = "Litecoin",
                        symbol = "LTC",
                        type = "coin",
                        isActive = false,
                        isNew = true
                    )
                )
            )
        ).thenReturn(
            listOf(
                CryptoCoinUIModel(
                    name = "Bitcoin",
                    symbol = "BTC",
                    newTagVisibility = View.GONE,
                    cryptoIcon = R.drawable.crypto_coin,
                    backgroundColor = android.R.color.transparent
                ), CryptoCoinUIModel(
                    name = "Litecoin",
                    symbol = "LTC",
                    newTagVisibility = View.VISIBLE,
                    cryptoIcon = R.drawable.crypto_disabled,
                    backgroundColor = R.color.light_gray
                )
            )
        )
        cryptoCoinUseCase.searchTextFilter("coin")

        `when`(
            cryptoCoinUIDataMapper.invoke(
                listOf(
                    CryptoCoinData(
                        name = "Litecoin",
                        symbol = "LTC",
                        type = "coin",
                        isActive = false,
                        isNew = true
                    )
                )
            )
        ).thenReturn(
            listOf(
                CryptoCoinUIModel(
                    name = "Litecoin",
                    symbol = "LTC",
                    newTagVisibility = View.VISIBLE,
                    cryptoIcon = R.drawable.crypto_disabled,
                    backgroundColor = R.color.light_gray
                )
            )
        )
        val actualResult = cryptoCoinUseCase.applyChipFilters(
            CryptoCoinConstants.KEY_NEW_COIN,
            R.id.newCoinsChip
        )
        assertEquals(
            CryptoCoinUIState.CryptoCoinFilterList(
                listOf(
                    CryptoCoinUIModel(
                        name = "Litecoin",
                        symbol = "LTC",
                        newTagVisibility = View.VISIBLE,
                        cryptoIcon = R.drawable.crypto_disabled,
                        backgroundColor = R.color.light_gray
                    )
                )
            ), actualResult
        )

        val removeFilterResult = cryptoCoinUseCase.applyChipFilters(
            CryptoCoinConstants.KEY_NEW_COIN,
            -1
        )
        assertEquals(
            CryptoCoinUIState.CryptoCoinFilterList(
                listOf(
                    CryptoCoinUIModel(
                        name = "Bitcoin",
                        symbol = "BTC",
                        newTagVisibility = View.GONE,
                        cryptoIcon = R.drawable.crypto_coin,
                        backgroundColor = android.R.color.transparent
                    ), CryptoCoinUIModel(
                        name = "Litecoin",
                        symbol = "LTC",
                        newTagVisibility = View.VISIBLE,
                        cryptoIcon = R.drawable.crypto_disabled,
                        backgroundColor = R.color.light_gray
                    )
                )
            ), removeFilterResult
        )
    }

    @Test
    fun searchCoinNameTextFilterThanApplyIncorrectFilterId() = runTest {
        val testData = CryptoCoinTestData.getCryptoCoinList()
        `when`(cryptoCoinRepository.getCryptoCoinList()).thenReturn(testData)
        `when`(cryptoCoinUIDataMapper.invoke(testData)).thenReturn(CryptoCoinTestData.getCryptoCoinUIData())
        cryptoCoinUseCase.getCryptoCoinList()

        `when`(
            cryptoCoinUIDataMapper.invoke(
                listOf(
                    CryptoCoinData(
                        name = "Bitcoin",
                        symbol = "BTC",
                        type = "coin",
                        isActive = true,
                        isNew = false
                    ), CryptoCoinData(
                        name = "Litecoin",
                        symbol = "LTC",
                        type = "coin",
                        isActive = false,
                        isNew = true
                    )
                )
            )
        ).thenReturn(
            listOf(
                CryptoCoinUIModel(
                    name = "Bitcoin",
                    symbol = "BTC",
                    newTagVisibility = View.GONE,
                    cryptoIcon = R.drawable.crypto_coin,
                    backgroundColor = android.R.color.transparent
                ), CryptoCoinUIModel(
                    name = "Litecoin",
                    symbol = "LTC",
                    newTagVisibility = View.VISIBLE,
                    cryptoIcon = R.drawable.crypto_disabled,
                    backgroundColor = R.color.light_gray
                )
            )
        )
        cryptoCoinUseCase.searchTextFilter("coin")

        `when`(
            cryptoCoinUIDataMapper.invoke(
                listOf(
                    CryptoCoinData(
                        name = "Litecoin",
                        symbol = "LTC",
                        type = "coin",
                        isActive = false,
                        isNew = true
                    )
                )
            )
        ).thenReturn(
            listOf(
                CryptoCoinUIModel(
                    name = "Litecoin",
                    symbol = "LTC",
                    newTagVisibility = View.VISIBLE,
                    cryptoIcon = R.drawable.crypto_disabled,
                    backgroundColor = R.color.light_gray
                )
            )
        )

        //Wrong ActiveInActive Token Filter Test
        val wrongActiveInActiveTokenFilter = cryptoCoinUseCase.applyChipFilters(
            CryptoCoinConstants.KEY_ACTIVE_INACTIVE,
            123
        )
        assertEquals(
            CryptoCoinUIState.CryptoCoinFilterList(
                listOf(
                    CryptoCoinUIModel(
                        name = "Bitcoin",
                        symbol = "BTC",
                        newTagVisibility = View.GONE,
                        cryptoIcon = R.drawable.crypto_coin,
                        backgroundColor = android.R.color.transparent
                    ), CryptoCoinUIModel(
                        name = "Litecoin",
                        symbol = "LTC",
                        newTagVisibility = View.VISIBLE,
                        cryptoIcon = R.drawable.crypto_disabled,
                        backgroundColor = R.color.light_gray
                    )
                )
            ), wrongActiveInActiveTokenFilter
        )

        //Wrong Coin Token Filter Test
        val wrongCoinTokenFilterResult = cryptoCoinUseCase.applyChipFilters(
            CryptoCoinConstants.KEY_TOKEN_COIN,
            123
        )
        assertEquals(
            CryptoCoinUIState.CryptoCoinFilterList(
                listOf(
                    CryptoCoinUIModel(
                        name = "Bitcoin",
                        symbol = "BTC",
                        newTagVisibility = View.GONE,
                        cryptoIcon = R.drawable.crypto_coin,
                        backgroundColor = android.R.color.transparent
                    ), CryptoCoinUIModel(
                        name = "Litecoin",
                        symbol = "LTC",
                        newTagVisibility = View.VISIBLE,
                        cryptoIcon = R.drawable.crypto_disabled,
                        backgroundColor = R.color.light_gray
                    )
                )
            ), wrongCoinTokenFilterResult
        )

        //Wrong New Coin Filter Test
        val wrongNewCoinFilterResult = cryptoCoinUseCase.applyChipFilters(
            CryptoCoinConstants.KEY_NEW_COIN,
            123
        )
        assertEquals(
            CryptoCoinUIState.CryptoCoinFilterList(
                listOf(
                    CryptoCoinUIModel(
                        name = "Bitcoin",
                        symbol = "BTC",
                        newTagVisibility = View.GONE,
                        cryptoIcon = R.drawable.crypto_coin,
                        backgroundColor = android.R.color.transparent
                    ), CryptoCoinUIModel(
                        name = "Litecoin",
                        symbol = "LTC",
                        newTagVisibility = View.VISIBLE,
                        cryptoIcon = R.drawable.crypto_disabled,
                        backgroundColor = R.color.light_gray
                    )
                )
            ), wrongNewCoinFilterResult
        )
    }
}