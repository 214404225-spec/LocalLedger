// data/local/Stats.kt
package com.example.localledger.data.local

import java.math.BigDecimal

data class CategoryExpense(val category: String, val total: BigDecimal)
data class DayExpense(val day: String?, val total: BigDecimal)   // day 可为 null
data class MonthExpense(val month: String?, val total: BigDecimal) // month 可为 null