package com.example.localledger.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(tableName = "exchange_rates")
data class ExchangeRateEntity(
    @PrimaryKey val fromCurrency: String, // 如 "USD"
    val toCurrency: String = "CNY",
    val rate: BigDecimal,                // 1 USD = ? CNY
    val lastFetched: Long = System.currentTimeMillis() // 缓存时间
) {
    fun isExpired(): Boolean {
        return System.currentTimeMillis() - lastFetched > 24 * 60 * 60 * 1000 // 24小时过期
    }
}