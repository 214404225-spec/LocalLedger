package com.example.localledger.data.local

import androidx.room.*
import com.example.localledger.data.model.ExchangeRateEntity

@Dao
interface ExchangeRateDao {
    @Query("SELECT * FROM exchange_rates WHERE fromCurrency = :currency")
    suspend fun getRate(currency: String): ExchangeRateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRate(rate: ExchangeRateEntity)

    @Query("DELETE FROM exchange_rates WHERE fromCurrency = :currency")
    suspend fun deleteRate(currency: String)
}