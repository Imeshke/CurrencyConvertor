package com.imeshke.currencyconverter.api

import com.imeshke.currencyconverter.api.model.CurrencyRates
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {

    @GET("live?")
    suspend fun getLatestRates(@Query("access_key", encoded = true)ApiKey : String): Response<CurrencyRates>

}