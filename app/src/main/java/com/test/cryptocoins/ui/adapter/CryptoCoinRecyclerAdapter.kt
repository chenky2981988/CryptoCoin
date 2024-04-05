package com.test.cryptocoins.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.test.cryptocoins.databinding.CryptoCoinListItemBinding
import com.test.cryptocoins.model.CryptoCoinUIModel


/**
 * Created by Chirag Sidhiwala on 04/04/24.
 */
class CryptoCoinRecyclerAdapter(cryptoCoinList: List<CryptoCoinUIModel>) :
    RecyclerView.Adapter<CryptoCoinRecyclerAdapter.CryptoCoinViewHolder>() {

    private var displayCryptoList: List<CryptoCoinUIModel> = cryptoCoinList

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

    fun setFilterList(filterList: List<CryptoCoinUIModel>) {
        this.displayCryptoList = filterList
        notifyDataSetChanged()
    }

    inner class CryptoCoinViewHolder(
        private val binding: CryptoCoinListItemBinding,
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cryptoCoinData: CryptoCoinUIModel) {
            binding.apply {
                newTagIv.visibility = cryptoCoinData.newTagVisibility
                cryptoNameTv.text = cryptoCoinData.name
                cryptoSymbolTv.text = cryptoCoinData.symbol
                coinIv.setImageResource(cryptoCoinData.cryptoIcon)
                root.setBackgroundColor(root.context.getColor(cryptoCoinData.backgroundColor))
                /*when {
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
                }*/
            }
        }
    }
}