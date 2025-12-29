package com.example.localledger.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.localledger.data.model.TransactionEntity
import com.example.localledger.data.repository.TransactionRepository
import com.example.localledger.data.local.CategoryExpense
import com.example.localledger.data.local.DayExpense
import com.example.localledger.data.local.MonthExpense
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal

sealed class ScreenState {
    object List : ScreenState()
    object Add : ScreenState()
    data class Edit(val transaction: TransactionEntity) : ScreenState()
    object Stats : ScreenState()
}

class TransactionViewModel(
    private val repository: TransactionRepository
) : ViewModel() {

    private val _transactions = MutableStateFlow<List<TransactionEntity>>(emptyList())
    val transactions: StateFlow<List<TransactionEntity>> = _transactions

    private val _expensesByCategory = MutableStateFlow<List<CategoryExpense>>(emptyList())
    val expensesByCategory: StateFlow<List<CategoryExpense>> = _expensesByCategory

    private val _expensesByDay = MutableStateFlow<List<DayExpense>>(emptyList())
    val expensesByDay: StateFlow<List<DayExpense>> = _expensesByDay

    private val _expensesByMonth = MutableStateFlow<List<MonthExpense>>(emptyList())
    val expensesByMonth: StateFlow<List<MonthExpense>> = _expensesByMonth

    private val _screenState = MutableStateFlow<ScreenState>(ScreenState.List)
    val screenState: StateFlow<ScreenState> = _screenState

    init {
        loadTransactions()
        loadStats()
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            _transactions.value = repository.getAllTransactions()
        }
    }

    private fun loadStats() {
        viewModelScope.launch {
            _expensesByCategory.value = repository.getExpensesByCategory()
            _expensesByDay.value = repository.getExpensesByDay()
            _expensesByMonth.value = repository.getExpensesByMonth()
        }
    }

    fun navigateTo(screen: ScreenState) {
        _screenState.value = screen
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
                loadTransactions()
                loadStats() // 重新加载统计
                navigateTo(ScreenState.List)
            } catch (e: NumberFormatException) {
                // Handle error
            }
        }
    }

    fun updateTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.updateTransaction(transaction)
            loadTransactions()
            loadStats()
            navigateTo(ScreenState.List)
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
            loadTransactions()
            loadStats()
            // 不改变 screenState，保持在列表页
        }
    }
}