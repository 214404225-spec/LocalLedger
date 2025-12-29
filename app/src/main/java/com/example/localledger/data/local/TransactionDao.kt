package com.example.localledger.data.local

import androidx.room.*
import com.example.localledger.data.model.TransactionEntity
import java.math.BigDecimal

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    suspend fun getAllTransactions(): List<TransactionEntity>

    @Insert
    suspend fun insertTransaction(transaction:  TransactionEntity)

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Long): TransactionEntity?

    @Query("SELECT SUM(baseAmount) FROM transactions WHERE timestamp BETWEEN :start AND :end")
    suspend fun getTotalInPeriodAsDouble(start: Long, end: Long): Double?

    // ===== 按类别统计 =====
    @Query("SELECT category, SUM(baseAmount) AS total FROM transactions GROUP BY category")
    suspend fun getExpensesByCategory(): List<CategoryExpense>

    // ===== 按日统计（修复：时间戳/1000 + 本地时区）=====
    @Query("""
        SELECT 
            date(timestamp / 1000, 'unixepoch', 'localtime') AS day,
            SUM(baseAmount) AS total 
        FROM transactions 
        WHERE timestamp > 0
        GROUP BY day 
        ORDER BY day DESC 
        LIMIT 30
    """)
    suspend fun getExpensesByDay(): List<DayExpense>

    // ===== 按月统计（修复：时间戳/1000 + 本地时区）=====
    @Query("""
        SELECT 
            strftime('%Y-%m', timestamp / 1000, 'unixepoch', 'localtime') AS month,
            SUM(baseAmount) AS total 
        FROM transactions 
        WHERE timestamp > 0
        GROUP BY month 
        ORDER BY month DESC 
        LIMIT 12
    """)
    suspend fun getExpensesByMonth(): List<MonthExpense>
}