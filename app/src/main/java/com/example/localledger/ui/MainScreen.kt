package com.example.localledger.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.localledger.data.model.TransactionEntity
import com.example.localledger.ui.screens.AddTransactionScreen
import com.example.localledger.ui.screens.EditTransactionScreen
import com.example.localledger.ui.screens.StatsScreen
import com.example.localledger.ui.screens.TransactionListScreen
import com.example.localledger.viewmodel.ScreenState
import com.example.localledger.viewmodel.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: TransactionViewModel) {
    val transactions by viewModel.transactions.collectAsState()
    val expensesByCategory by viewModel.expensesByCategory.collectAsState()
    val expensesByDay by viewModel.expensesByDay.collectAsState()
    val screenState by viewModel.screenState.collectAsState()

    var showDeleteConfirm by remember { mutableStateOf<TransactionEntity?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val currentState = screenState) {
            ScreenState.List -> {
                TransactionListScreen(
                    transactions = transactions,
                    onEdit = { viewModel.navigateTo(ScreenState.Edit(it)) },
                    onDelete = { showDeleteConfirm = it }
                )
            }
            ScreenState.Add -> {
                AddTransactionScreen(
                    onSave = { amount, currency, category, note ->
                        viewModel.addTransaction(amount, currency, category, note)
                    },
                    onDismiss = { viewModel.navigateTo(ScreenState.List) }
                )
            }
            is ScreenState.Edit -> {
                EditTransactionScreen(
                    transaction = currentState.transaction,
                    onSave = { viewModel.updateTransaction(it) },
                    onDismiss = { viewModel.navigateTo(ScreenState.List) }
                )
            }
            ScreenState.Stats -> {
                StatsScreen(
                    expensesByCategory = expensesByCategory,
                    expensesByDay = expensesByDay,
                    onDismiss = { viewModel.navigateTo(ScreenState.List) }
                )
            }
        }

        // 仅在列表页显示 FAB 和顶部操作
        if (screenState is ScreenState.List) {
            TopAppBar(
                title = { Text("本地隐私记账簿") },
                actions = {
                    IconButton(onClick = { viewModel.navigateTo(ScreenState.Stats) }) {
                        Icon(Icons.Default.BarChart, contentDescription = "统计")
                    }
                },
                modifier = Modifier.align(Alignment.TopStart)
            )

            FloatingActionButton(
                onClick = { viewModel.navigateTo(ScreenState.Add) },
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomEnd)
            ) {
                Icon(Icons.Default.Add, contentDescription = "添加账目")
            }
        }

        // 删除确认对话框
        showDeleteConfirm?.let { transaction ->
            AlertDialog(
                onDismissRequest = { showDeleteConfirm = null },
                title = { Text("删除账目？") },
                text = { Text("此操作不可恢复。") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deleteTransaction(transaction)
                            showDeleteConfirm = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("删除")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirm = null }) {
                        Text("取消")
                    }
                }
            )
        }
    }
}