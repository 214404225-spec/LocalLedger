package com.example.localledger.data.repository

import android.util.Log
import com.example.localledger.data.local.TransactionDao
import com.example.localledger.data.local.ExchangeRateDao
import com.example.localledger.data.remote.ExchangeRateApi
import com.example.localledger.data.model.TransactionEntity
import com.example.localledger.data.model.ExchangeRateEntity
import com.example.localledger.data.local.CategoryExpense
import com.example.localledger.data.local.DayExpense
import com.example.localledger.data.local.MonthExpense
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

    suspend fun updateTransaction(transaction: TransactionEntity) {
        transactionDao.updateTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: TransactionEntity) {
        transactionDao.deleteTransaction(transaction)
    }

    suspend fun getTransactionById(id: Long): TransactionEntity? {
        return transactionDao.getTransactionById(id)
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
                rateEntity = exchangeRateDao.getRate(fromCurrency)
            }
        }

        return rateEntity?.rate ?: BigDecimal.ONE
    }

    suspend fun getAllTransactions() = transactionDao.getAllTransactions()

    suspend fun getTotalInPeriod(start: Long, end: Long): BigDecimal {
        val totalDouble = transactionDao.getTotalInPeriodAsDouble(start, end)
        return totalDouble?.toBigDecimal()?.setScale(2, RoundingMode.HALF_UP) ?: BigDecimal.ZERO
    }

    // ===== 新增统计方法 =====
    suspend fun getExpensesByCategory(): List<CategoryExpense> {
        return transactionDao.getExpensesByCategory()
    }

    suspend fun getExpensesByDay(): List<DayExpense> {
        return transactionDao.getExpensesByDay()
            .filter { it.day != null }
            .map { it.copy(total = it.total.setScale(2, java.math.RoundingMode.HALF_UP)) }
    }

    suspend fun getExpensesByMonth(): List<MonthExpense> {
        return transactionDao.getExpensesByMonth()
            .filter { it.month != null }
            .map { it.copy(total = it.total.setScale(2, java.math.RoundingMode.HALF_UP)) }
    }
}