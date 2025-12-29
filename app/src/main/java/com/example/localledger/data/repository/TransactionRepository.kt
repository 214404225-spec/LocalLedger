package com.example.localledger.data.repository

import android.util.Log
import com.example.localledger.data.local.TransactionDao
import com.example.localledger.data.local.ExchangeRateDao
import com.example.localledger.data.remote.ExchangeRateApi
import com.example.localledger.data.model.TransactionEntity
import com.example.localledger.data.model.ExchangeRateEntity
import java.math.BigDecimal
import java.math.RoundingMode

class TransactionRepository(
    private val transactionDao: TransactionDao,
    private val exchangeRateDao: ExchangeRateDao,
    private val api: ExchangeRateApi
) {

    private val baseCurrency = "CNY"

    suspend fun addTransaction(
        amount: BigDecimal,
        currency: String,
        category: String,
        note: String
    ) {
        val baseAmount = if (currency == baseCurrency) {
            amount
        } else {
            val rate = getOrCreateExchangeRate(currency)
            amount.multiply(rate)
        }

        val transaction = TransactionEntity(
            amount = amount,
            currency = currency,
            baseAmount = baseAmount.setScale(2, RoundingMode.HALF_UP),
            category = category,
            note = note
        )
        transactionDao.insertTransaction(transaction)
    }

    private suspend fun getOrCreateExchangeRate(fromCurrency: String): BigDecimal {
        var rateEntity = exchangeRateDao.getRate(fromCurrency)

        if (rateEntity == null || rateEntity.isExpired()) {
            try {
                val response = api.getExchangeRates(fromCurrency)
                if (response.isSuccessful) {
                    val rateValue = response.body()?.conversion_rates?.get(baseCurrency)
                    if (rateValue != null) {
                        rateEntity = ExchangeRateEntity(
                            fromCurrency = fromCurrency,
                            rate = BigDecimal(rateValue.toString())
                        )
                        exchangeRateDao.insertRate(rateEntity)
                    }
                }
            } catch (e: Exception) {
                Log.e("Repository", "Fetch rate failed", e)
                rateEntity = exchangeRateDao.getRate(fromCurrency) // 失败时用旧缓存
            }
        }

        return rateEntity?.rate ?: BigDecimal.ONE
    }

    suspend fun getAllTransactions() = transactionDao.getAllTransactions()

    suspend fun getTotalInPeriod(start: Long, end: Long): BigDecimal {
        val totalDouble = transactionDao.getTotalInPeriodAsDouble(start, end)
        return totalDouble?.toBigDecimal()?.setScale(2, RoundingMode.HALF_UP) ?: BigDecimal.ZERO
    }
}