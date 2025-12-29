package com.example.localledger.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

data class ExchangeRateResponse(
    val base_code: String,
    val conversion_rates: Map<String, Double>
)

interface ExchangeRateApi {
    // TODO: 替换 YOUR_API_KEY 为你自己的 Key
    @GET("latest/{base}")
    suspend fun getExchangeRates(
        @Path("base") base: String
    ): Response<ExchangeRateResponse>

    companion object {
        const val BASE_URL = "https://v6.exchangerate-api.com/v6/754b743f7a7295b1a0cd228a/latest/USD"
    }
}