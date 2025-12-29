package com.example.localledger.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.localledger.data.model.TransactionEntity
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TransactionListScreen(transactions: List<TransactionEntity>) {
    if (transactions.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "暂无账目\n点击 ➕ 添加第一笔记录",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
    ) {
        items(transactions) { transaction ->
            TransactionItem(transaction = transaction)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun TransactionItem(transaction: TransactionEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${transaction.category}${if (transaction.note.isNotBlank()) " · ${transaction.note}" else ""}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = buildString {
                    if (transaction.currency != "CNY") {
                        append("（${transaction.currency}）")
                    }
                    append(" ${transaction.amount.setScale(2, java.math.RoundingMode.HALF_UP)}")
                    if (transaction.currency != "CNY") {
                        append(" → ≈ ¥${transaction.baseAmount.setScale(2, java.math.RoundingMode.HALF_UP)}")
                    }
                },
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
                    .format(Date(transaction.timestamp)),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}