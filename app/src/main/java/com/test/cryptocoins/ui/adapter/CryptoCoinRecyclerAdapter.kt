package com.test.cryptocoins.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.test.cryptocoins.R
import com.test.cryptocoins.databinding.CryptoCoinListItemBinding
import com.test.cryptocoins.model.CryptoCoinData


/**
 * Created by Chirag Sidhiwala on 04/04/24.
 */
class CryptoCoinRecyclerAdapter(private val cryptoCoinList: List<CryptoCoinData>) :
    RecyclerView.Adapter<CryptoCoinRecyclerAdapter.CryptoCoinViewHolder>() {

    private var displayCryptoList: List<CryptoCoinData> = cryptoCoinList
    private val filterList: MutableList<Int> = mutableListOf(-1,-1,-1)
    private var filterText: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CryptoCoinViewHolder {
        return CryptoCoinViewHolder(
            CryptoCoinListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CryptoCoinViewHolder, position: Int) {
        holder.bind(displayCryptoList[position])
    }

    override fun getItemCount(): Int {
        return displayCryptoList.size
    }

    fun textFilter(filterText: String) {
        this.filterText = filterText
        if(filterList.any { it != -1 }) {
            filterList()
        } else {
            filterInternalList()
            notifyDataSetChanged()
        }
    }

    private fun filterInternalList() {
        if (filterText.isEmpty()) {
            this.displayCryptoList = cryptoCoinList
        } else {
            val filteredList = cryptoCoinList.filter {
                (it.name?.contains(filterText, ignoreCase = true) == true) ||
                        (it.symbol?.contains(filterText, ignoreCase = true) == true)
            }
            this.displayCryptoList = filteredList
        }
    }

    fun coinTokenFilterList(chipFilters: Int) {
        filterList.add(KEY_TOKEN_COIN, chipFilters)
        filterList()
    }

    fun activeInactiveFilterList(chipFilters: Int) {
        filterList.add(KEY_ACTIVE_INACTIVE, chipFilters)
        filterList()
    }

    fun newCoinFilterList(chipFilters: Int) {
        filterList.add(KEY_NEW_COIN, chipFilters)
        filterList()
    }

    private fun filterList() {
        filterInternalList()
        when {
            filterList[KEY_TOKEN_COIN] != -1 -> {
                when {
                    filterList[KEY_TOKEN_COIN] == R.id.onlyTokenChip -> {
                        this.displayCryptoList = this.displayCryptoList.filter { cryptoCoinData ->
                            cryptoCoinData.type == "token"
                        }
                    }
                    filterList[KEY_TOKEN_COIN] == R.id.onlyCoinChip -> {
                        this.displayCryptoList = this.displayCryptoList.filter { cryptoCoinData ->
                            cryptoCoinData.type == "coin"
                        }
                    }
                    else -> {
                        this.displayCryptoList = this.displayCryptoList.filter { cryptoCoinData ->
                            cryptoCoinData.type == "coin" || cryptoCoinData.type == "token"
                        }
                    }
                }
            }
        }
        when {
            filterList[KEY_ACTIVE_INACTIVE] != -1 -> {
                when {
                    filterList[KEY_ACTIVE_INACTIVE] == R.id.activeCoinsChip -> {
                        this.displayCryptoList = this.displayCryptoList.filter { cryptoCoinData ->
                            cryptoCoinData.isActive == true
                        }
                    }
                    filterList[KEY_ACTIVE_INACTIVE] == R.id.inActiveCoinsChip -> {
                        this.displayCryptoList = this.displayCryptoList.filter { cryptoCoinData ->
                            cryptoCoinData.isActive == false
                        }
                    }
                    else -> {
                        this.displayCryptoList = this.displayCryptoList.filter { cryptoCoinData ->
                            cryptoCoinData.isActive == true || cryptoCoinData.isActive == false
                        }
                    }
                }
            }
        }
        when {
            filterList[KEY_NEW_COIN] != -1 -> {
                when (R.id.newCoinsChip) {
                    filterList[KEY_NEW_COIN] -> {
                        this.displayCryptoList = this.displayCryptoList.filter { cryptoCoinData ->
                            cryptoCoinData.isNew == true
                        }
                    }
                    else -> {
                        this.displayCryptoList = this.displayCryptoList.filter { cryptoCoinData ->
                            cryptoCoinData.isNew == false || cryptoCoinData.isNew == true
                        }
                    }
                }
            }
        }
        notifyDataSetChanged()
    }

    inner class CryptoCoinViewHolder(
        private val binding: CryptoCoinListItemBinding,
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cryptoCoinData: CryptoCoinData) {
            binding.apply {
                if (cryptoCoinData.isNew == true) {
                    newTagIv.visibility = View.VISIBLE
                } else {
                    newTagIv.visibility = View.GONE
                }
                cryptoNameTv.text = cryptoCoinData.name
                cryptoSymbolTv.text = cryptoCoinData.symbol
                when {
                    cryptoCoinData.type.equals(
                        "coin",
                        ignoreCase = true
                    ) && cryptoCoinData.isActive == true -> {
                        coinIv.setImageResource(R.drawable.crypto_coin)
                        binding.root.setBackgroundColor(root.context.getColor(android.R.color.transparent))
                    }

                    cryptoCoinData.type.equals(
                        "token",
                        ignoreCase = true
                    ) && cryptoCoinData.isActive == true -> {
                        coinIv.setImageResource(R.drawable.crypto_token)
                        binding.root.setBackgroundColor(root.context.getColor(android.R.color.transparent))
                    }

                    cryptoCoinData.isActive == false -> {
                        coinIv.setImageResource(R.drawable.crypto_disabled)
                        binding.root.setBackgroundColor(root.context.getColor(R.color.light_gray))
                    }
                }
            }
        }
    }

    companion object {
        private const val KEY_TOKEN_COIN = 0
        private const val KEY_ACTIVE_INACTIVE = 1
        private const val KEY_NEW_COIN = 2
    }
}