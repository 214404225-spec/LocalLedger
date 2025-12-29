package com.example.localledger.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.localledger.ui.screens.AddTransactionScreen
import com.example.localledger.ui.screens.TransactionListScreen
import com.example.localledger.viewmodel.TransactionViewModel

@Composable
fun MainScreen(viewModel: TransactionViewModel) {
    val transactions by viewModel.transactions.collectAsState()

    var showAddScreen by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (showAddScreen) {
            AddTransactionScreen(
                onSave = { amount, currency, category, note ->
                    viewModel.addTransaction(amount, currency, category, note)
                    showAddScreen = false
                },
                onDismiss = { showAddScreen = false }
            )
        } else {
            TransactionListScreen(transactions = transactions)
        }

        if (!showAddScreen) {
            FloatingActionButton(
                onClick = { showAddScreen = true },
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "添加账目"
                )
            }
        }
    }
}