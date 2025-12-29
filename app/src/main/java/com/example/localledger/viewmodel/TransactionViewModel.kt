package com.example.localledger.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.localledger.data.model.TransactionEntity
import com.example.localledger.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal

class TransactionViewModel(
    private val repository: TransactionRepository
) : ViewModel() {

    private val _transactions = MutableStateFlow<List<TransactionEntity>>(emptyList())
    val transactions: StateFlow<List<TransactionEntity>> = _transactions

    init {
        loadTransactions()
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            _transactions.value = repository.getAllTransactions()
        }
    }

    fun addTransaction(
        amount: String,
        currency: String,
        category: String,
        note: String
    ) {
        viewModelScope.launch {
            try {
                val amountValue = amount.toBigDecimal()
                repository.addTransaction(amountValue, currency, category, note)
                loadTransactions() // 刷新列表
            } catch (e: NumberFormatException) {
                // 可选：暴露错误状态
            }
        }
    }

    fun getTotalForToday(): BigDecimal {
        // 简化：返回 0，或根据需要实现日期逻辑
        return BigDecimal.ZERO
    }
}