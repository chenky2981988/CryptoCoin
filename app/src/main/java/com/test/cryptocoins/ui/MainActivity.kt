package com.test.cryptocoins.ui

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Toast
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
class MainActivity : AppCompatActivity(), OnCheckedStateChangeListener {

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
        val searchItem = menu?.findItem(R.id.actionSearch)
        // getting search view of our item.
        searchView = searchItem?.actionView as SearchView?
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
                        cryptoCoinViewModel.applySearchFilter(newText.toString())
                    }
                    return false
                }
            })
            setOnCloseListener {
                cryptoCoinViewModel.applySearchFilter("")
                false
            }
        }
        return true

    }

    private fun setChipFilterListener() {
        binding.apply {
            coinTokenChipGroup.setOnCheckedStateChangeListener(this@MainActivity)
            activeInActiveChipGroup.setOnCheckedStateChangeListener(this@MainActivity)
            newCoinChipGroup.setOnCheckedStateChangeListener(this@MainActivity)
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
                    // Either show loader or shimmer effect
                    Log.d("TAG", "Loading")
                    showLoadingView()
                }

                is CryptoCoinUIState.CryptoCoinSuccess -> {
                    showLoadingView(false)
                    showCryptoList(it.cryptoCoinList)
                }

                is CryptoCoinUIState.CryptoCoinFilterList -> {
                    cryptoCoinRecyclerAdapter?.setFilterList(it.cryptoCoinList)
                }

                is CryptoCoinUIState.CryptoCoinFailure -> {
                    showLoadingView(false)
                    Toast.makeText(
                        applicationContext,
                        "Error in crypto coins data",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is UIState.Error -> {
                    showLoadingView(false)
                    Toast.makeText(
                        applicationContext,
                        "Something went wrong! Try after some time",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun showLoadingView(shouldShowLoading: Boolean = true) {
        with(binding) {
            if (shouldShowLoading) {
                shimmerLayout.apply {
                    visibility = View.VISIBLE
                    startShimmer()
                }
                cryptoCoinsRecyclerView.visibility = View.GONE
                coinFilterLayout.visibility = View.GONE
            } else {
                shimmerLayout.apply {
                    visibility = View.GONE
                    stopShimmer()
                }
                cryptoCoinsRecyclerView.visibility = View.VISIBLE
                coinFilterLayout.visibility = View.VISIBLE
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

    override fun onCheckedChanged(chipGroup: ChipGroup, selectedChipList: MutableList<Int>) {
        when (chipGroup.id) {
            R.id.coinTokenChipGroup -> {
                cryptoCoinViewModel.applyChipFilters(
                    CryptoCoinConstants.KEY_TOKEN_COIN,
                    chipGroup.checkedChipId
                )
            }
            R.id.activeInActiveChipGroup -> {
                cryptoCoinViewModel.applyChipFilters(
                    CryptoCoinConstants.KEY_ACTIVE_INACTIVE,
                    chipGroup.checkedChipId
                )
            }
            R.id.newCoinChipGroup -> {
                cryptoCoinViewModel.applyChipFilters(
                    CryptoCoinConstants.KEY_NEW_COIN,
                    chipGroup.checkedChipId
                )
            }
        }
    }
}