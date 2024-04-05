package com.test.cryptocoins.ui

import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.chip.ChipGroup
import com.google.android.material.chip.ChipGroup.OnCheckedStateChangeListener
import com.test.cryptocoins.R
import com.test.cryptocoins.common.CryptoCoinConstants
import com.test.cryptocoins.common.UIState
import com.test.cryptocoins.databinding.ActivityMainBinding
import com.test.cryptocoins.model.CryptoCoinData
import com.test.cryptocoins.model.CryptoCoinUIModel
import com.test.cryptocoins.model.CryptoCoinUIState
import com.test.cryptocoins.ui.adapter.CryptoCoinRecyclerAdapter
import com.test.cryptocoins.viewmodel.CryptoCoinViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val cryptoCoinViewModel: CryptoCoinViewModel by viewModels()
    private var cryptoCoinRecyclerAdapter: CryptoCoinRecyclerAdapter? = null
    private var searchView: SearchView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setChipFilterListener()
        showCryptoCoinData()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // inside inflater we are inflating our menu file.
        menuInflater.inflate(R.menu.crypto_coin_menu, menu)

        // below line is to get our menu item.

        // below line is to get our menu item.
        val searchItem = menu?.findItem(R.id.actionSearch)

        // getting search view of our item.

        // getting search view of our item.
        searchView = searchItem?.actionView as SearchView?

        // below line is to call set on query text listener method.

        // below line is to call set on query text listener method.
        searchView?.apply {
            setOnQueryTextListener(object : OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    // inside on query text change method we are
                    // calling a method to filter our recycler view.
                    if (newText != null) {
                        //cryptoCoinRecyclerAdapter?.textFilter(newText.toString())
                        cryptoCoinViewModel.applySearchFilter(newText.toString())
                    }
                    return false
                }
            })
            setOnCloseListener {
                //cryptoCoinRecyclerAdapter?.textFilter("")
                cryptoCoinViewModel.applySearchFilter("")
                false
            }
        }
        return true

    }

    private fun setChipFilterListener() {
        binding.coinTokenChipGroup.setOnCheckedStateChangeListener { coinTokenGroup, coinTokenList ->
            Log.d("TAG", "coinTokenList : $coinTokenList")
            Log.d(
                "TAG",
                "is Only Token Selected : ${coinTokenGroup.checkedChipId == R.id.onlyTokenChip}"
            )
            Log.d("TAG", "coinTokenGroup selected chip : ${coinTokenGroup.checkedChipId}")
            Log.d("TAG", "Search Text : ${searchView?.query.toString()}")
            //cryptoCoinRecyclerAdapter?.coinTokenFilterList(coinTokenGroup.checkedChipId)
            cryptoCoinViewModel.applyChipFilters(
                CryptoCoinConstants.KEY_TOKEN_COIN,
                coinTokenGroup.checkedChipId
            )
        }
        binding.activeInActiveChipGroup.setOnCheckedStateChangeListener { activeInactiveChipGroup, coinTokenList ->
            Log.d("TAG", "activeInActiveChip : $coinTokenList")
            Log.d(
                "TAG",
                "activeInactiveChipGroup selected chip: ${activeInactiveChipGroup.checkedChipId}"
            )
            //cryptoCoinRecyclerAdapter?.activeInactiveFilterList(activeInactiveChipGroup.checkedChipId)
            cryptoCoinViewModel.applyChipFilters(
                CryptoCoinConstants.KEY_ACTIVE_INACTIVE,
                activeInactiveChipGroup.checkedChipId
            )
        }
        binding.newCoinChipGroup.setOnCheckedStateChangeListener { newCoinChipGroup, coinTokenList ->
            Log.d("TAG", "newCoinChipGroup : $coinTokenList")
            Log.d("TAG", "newCoinChipGroup selected chip: ${newCoinChipGroup.checkedChipId}")
            //cryptoCoinRecyclerAdapter?.newCoinFilterList(newCoinChipGroup.checkedChipId)
            cryptoCoinViewModel.applyChipFilters(
                CryptoCoinConstants.KEY_NEW_COIN,
                newCoinChipGroup.checkedChipId
            )
        }
    }

    private fun showCryptoCoinData() {
        observerCryptoCoinLiveData()
        cryptoCoinViewModel.getCryptoData()
    }

    private fun observerCryptoCoinLiveData() {
        cryptoCoinViewModel.cryptoListLiveData.observe(this) {
            when (it) {
                is UIState.Loading -> {
                    Log.d("TAG", "Loading")
                }

                is CryptoCoinUIState.CryptoCoinSuccess -> {
                    Log.d("TAG", "Success with data ${it.cryptoCoinList.size}")
                    showCryptoList(it.cryptoCoinList)
                }

                is CryptoCoinUIState.CryptoCoinFilterList -> {
                    Log.d("TAG", "CryptoCoinFilterList")
                    cryptoCoinRecyclerAdapter?.setFilterList(it.cryptoCoinList)
                }

                is CryptoCoinUIState.CryptoCoinFailure -> {
                    Log.d("TAG", "CryptoCoinFailure")
                }

                is UIState.Error -> {
                    Log.d("TAG", "Error")
                }
            }
        }
    }

    private fun showCryptoList(cryptoCoinList: List<CryptoCoinUIModel>) {
        if (cryptoCoinRecyclerAdapter == null) {
            cryptoCoinRecyclerAdapter = CryptoCoinRecyclerAdapter(cryptoCoinList)
            binding.cryptoCoinsRecyclerView.apply {
                adapter = cryptoCoinRecyclerAdapter
                addItemDecoration(
                    DividerItemDecoration(
                        this.context,
                        DividerItemDecoration.VERTICAL
                    )
                )
            }
        }
    }
}