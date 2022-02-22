package com.imeshke.currencyconverter.ui.structure

import androidx.recyclerview.widget.DiffUtil
import com.imeshke.currencyconverter.viewmodel.model.Currency


class CurrencyRatesDiff(private val currentList: List<Currency>, private val updatedList: List<Currency>) : DiffUtil.Callback() {

    companion object {
        const val CHANGE = "CHANGE"
    }

    override fun getOldListSize() = currentList.size

    override fun getNewListSize() = updatedList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return currentList[oldItemPosition].currency == updatedList[newItemPosition].currency
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return currentList[oldItemPosition] == updatedList[newItemPosition]
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any {
        val oldRate = currentList[oldItemPosition]
        val newRate = updatedList[newItemPosition]

        val payloadSet = mutableSetOf<String>()

        if (oldRate.value!=newRate.value)
            payloadSet.add(CHANGE)

        return payloadSet
    }
}