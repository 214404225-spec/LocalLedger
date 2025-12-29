package com.example.localledger.data.local

import androidx.room.*
import com.example.localledger.data.model.TransactionEntity

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    suspend fun getAllTransactions(): List<TransactionEntity>

    @Insert
    suspend fun insertTransaction(transaction: TransactionEntity)

    // 返回 Double，Room 才能处理 SUM
    @Query("SELECT SUM(baseAmount) FROM transactions WHERE timestamp BETWEEN :start AND :end")
    suspend fun getTotalInPeriodAsDouble(start: Long, end: Long): Double?
}