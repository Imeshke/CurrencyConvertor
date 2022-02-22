package com.imeshke.currencyconverter.ui.structure

import androidx.recyclerview.widget.RecyclerView
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import com.imeshke.currencyconverter.R
import com.imeshke.currencyconverter.viewmodel.model.Currency


class CurrencyRateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val rateLayout: View
    val currencyName: TextView
    val currencyRateEdit: TextView

    init {
        rateLayout = this.itemView.findViewById(R.id.rateLayout)
        currencyName = this.itemView.findViewById(R.id.currencyNameTextView)
        currencyRateEdit = this.itemView.findViewById(R.id.rateTextView)
    }

    fun bindTo(currencyRate: Currency, position: Int) {

        updateValue(currencyRate,position)
        updateCurrency(currencyRate,position)
    }

    fun updateValue(currencyRate: Currency, position: Int) {

        val newValue: String = "%.2f".format(currencyRate.value)

        if (currencyRateEdit.text.toString() != newValue) {
            currencyRateEdit.setText(newValue)
        }
    }

    private fun updateCurrency(currencyRate: Currency, position: Int) {
        val updatedCurrency = currencyRate.currency

        currencyName.text = updatedCurrency
    }

}