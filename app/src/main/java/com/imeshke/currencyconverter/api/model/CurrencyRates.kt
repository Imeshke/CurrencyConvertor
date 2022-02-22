package com.imeshke.currencyconverter.api.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class CurrencyRates {

    @SerializedName("source")
    @Expose
    val source: String? = null
    @SerializedName("timestamp")
    @Expose
    val timestamp: String? = null
    @SerializedName("quotes")
    @Expose
    val quotes: Map<String, Float>? = null
    
}