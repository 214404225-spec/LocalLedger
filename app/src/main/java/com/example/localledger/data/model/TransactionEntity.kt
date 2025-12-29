package com.example.localledger.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: Long = System.currentTimeMillis(),
    val amount: BigDecimal,          // 原始金额（如 10.5 USD）
    val currency: String,            // 原始币种（如 "USD"）
    val baseAmount: BigDecimal,      // 换算成本币（如 CNY）的金额
    val baseCurrency: String = "CNY",// 本币固定为 CNY
    val category: String = "其他",
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)