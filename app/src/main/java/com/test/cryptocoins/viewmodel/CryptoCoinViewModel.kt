package com.test.cryptocoins.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.cryptocoins.common.UIState
import com.test.cryptocoins.usecase.CryptoCoinUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Chirag Sidhiwala on 04/04/24.
 */
@HiltViewModel
class CryptoCoinViewModel @Inject constructor(private val cryptoCoinUseCase: CryptoCoinUseCase) :
    ViewModel() {

    private val _cryptoListMutableLiveData = MutableLiveData<UIState>()
    val cryptoListLiveData = _cryptoListMutableLiveData

    fun getCryptoData() {
        _cryptoListMutableLiveData.value = UIState.Loading()
        viewModelScope.launch {
            val result = cryptoCoinUseCase.getCryptoCoinList()
            _cryptoListMutableLiveData.postValue(result)
        }
    }

    fun applySearchFilter(filterText: String) {
        viewModelScope.launch {
            val result = cryptoCoinUseCase.searchTextFilter(filterText)
            _cryptoListMutableLiveData.postValue(result)
        }
    }

    fun applyChipFilters(key: Int, chipFilters: Int) {
        viewModelScope.launch {
            val result = cryptoCoinUseCase.applyChipFilters(key, chipFilters)
            _cryptoListMutableLiveData.postValue(result)
        }
    }
}