package com.imeshke.currencyconverter.ui.structure

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.imeshke.currencyconverter.R
import com.imeshke.currencyconverter.viewmodel.model.Currency

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*


class CurrencyRatesAdapter(private val callback: OnRateInteraction) : RecyclerView.Adapter<CurrencyRateViewHolder>() {

    private var cRatesList: List<Currency>? = null

    companion object {
        const val ROOT_RATE = 0
        const val OTHER_RATE = 1
    }

    interface OnRateInteraction {
        fun onRateChanged(currencyName: String, value: Float)
        fun onValueChanged(value: Float)
        fun scrollToTop()
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyRateViewHolder {

        val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.rate_grid_item, parent, false)

        val rateHolder = CurrencyRateViewHolder(view)
        return rateHolder
    }

    override fun onBindViewHolder(holderCurrency: CurrencyRateViewHolder, position: Int) {
        holderCurrency.bindTo(cRatesList!![position],position)
    }

    override fun onBindViewHolder(holderCurrency: CurrencyRateViewHolder, position: Int, payloads: MutableList<Any>) {

        val set = payloads.firstOrNull() as Set<*>?

        if (set==null || set.isEmpty() ) {
            return super.onBindViewHolder(holderCurrency, position, payloads)
        }

        if (set.contains(CurrencyRatesDiff.CHANGE)){
            holderCurrency.updateValue(cRatesList!![position],position)
        }
    }

    override fun getItemCount(): Int {
        return if (cRatesList==null) 0 else cRatesList!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position==0) ROOT_RATE else OTHER_RATE
    }

    val pendingList: Deque<List<Currency>> = LinkedList()

    suspend fun updateList(newRatesList: List<Currency>)  {

        if (cRatesList == null) {
            cRatesList = newRatesList
            notifyItemRangeInserted(0, newRatesList.size)
            return
        }

        calculateDiff(newRatesList)
    }


    private suspend fun calculateDiff(latest: List<Currency>) {

        val ratesAdapter = this
        val diffResult = withContext(Dispatchers.Default) { DiffUtil.calculateDiff(CurrencyRatesDiff(cRatesList!!, latest)) }
        try {
            val newRootRate: Boolean = (cRatesList!![0].currency != latest[0].currency)
            cRatesList = latest

            diffResult.dispatchUpdatesTo(ratesAdapter)
            if (newRootRate)
                callback.scrollToTop()
        }catch (e : Exception){

        }

    }
}