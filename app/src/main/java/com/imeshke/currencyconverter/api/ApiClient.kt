package com.imeshke.currencyconverter.api


import com.imeshke.currencyconverter.api.model.CurrencyRates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object ApiClient {

    private val BASE_URL: String = "http://api.currencylayer.com/"
    private val service: ApiInterface

    init {

        val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        service = retrofit.create(ApiInterface::class.java)
    }

    suspend fun refreshLatestRates(ApiKey : String) : ApiResult<CurrencyRates> {

        try {
            val response = withContext(Dispatchers.IO) { service.getLatestRates(ApiKey) }

            if (response.isSuccessful)
                return ApiResult.Success(response.body()!!)

            return ApiResult.UnknownError("Unknown Error");

        } catch (e : Exception){
            return ApiResult.UnknownError("Exception ${e.message}");
        }

    }

}
