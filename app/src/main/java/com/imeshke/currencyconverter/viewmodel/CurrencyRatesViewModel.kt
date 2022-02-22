package com.imeshke.currencyconverter.viewmodel


import android.util.Log
import androidx.lifecycle.*
import com.imeshke.currencyconverter.api.ApiClient
import com.imeshke.currencyconverter.api.ApiResult
import com.imeshke.currencyconverter.api.model.CurrencyRates
import com.imeshke.currencyconverter.viewmodel.model.Currency

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class CurrencyRatesViewModel : ViewModel() {

    private val RATE_UPDATE: Any = Object()

    private var baseCurrency: String = "USD"
    private var baseValue: Float = 1F
    private var API_KEY: String = "6d9e4ce9a2b0e74a14344299427beb7a"
    // private var API_KEY: String = "44069cf4116dfaa919465190152aae46"

    private val currencyRates: MutableLiveData<List<Currency>> = MutableLiveData()
    public val newCurrencyRates: ArrayList<Currency> = ArrayList<Currency>()


    private fun updateLatestRates(latestRates: CurrencyRates) {

        synchronized(RATE_UPDATE) {
                if (!latestRates.source.equals(null)) {
                    val cr = Currency(latestRates.source!!, 1.0F, baseValue);

                    newCurrencyRates.add(cr)
                }

               latestRates.quotes?.forEach { (currency, rate) ->

                   newCurrencyRates.add(Currency(currency.substring(3,6), rate, rate * baseValue))

               }
               currencyRates.postValue(newCurrencyRates)
           }

    }

    fun getCurrencyRates() : LiveData<List<Currency>> = currencyRates

    fun refreshRates() {
        viewModelScope.launch(Dispatchers.Default) {
            val apiResult = ApiClient.refreshLatestRates(API_KEY);

            when (apiResult) {
                is ApiResult.Success -> updateLatestRates(apiResult.data)
                is ApiResult.UnknownError -> Log.d("CurrencyRatesViewModel","ApiError: ${apiResult.errorMessage}")
            }
        }
    }

    fun setNewBase(newBaseCurrency: String, newBaseValue: Float) {

        if (baseCurrency == newBaseCurrency)
            return;
        baseCurrency = newBaseCurrency
        baseValue = newBaseValue
        refreshRates()
    }

    fun setNewBaseValue(value: Float) {

        if (baseValue.equals(value))
            return

        viewModelScope.launch(Dispatchers.Default) {
            synchronized(RATE_UPDATE) {
                baseValue = value

                val newCurrencyRates: MutableList<Currency> = mutableListOf<Currency>()

                currencyRates.value?.forEach { newCurrencyRates.add(Currency(it.currency,it.rate,it.rate*baseValue))}
                currencyRates.postValue(newCurrencyRates)
            }
        }
    }
}
