package com.example.localledger.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import com.example.localledger.data.model.TransactionEntity
import java.math.BigDecimal

@Composable
fun EditTransactionScreen(
    transaction: TransactionEntity,
    onSave: (TransactionEntity) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var amount by remember { mutableStateOf(transaction.amount.toString()) }
    var currency by remember { mutableStateOf(transaction.currency) }
    var category by remember { mutableStateOf(transaction.category) }
    var note by remember { mutableStateOf(transaction.note) }

    val currencies = listOf("CNY", "USD", "EUR", "JPY", "GBP")
    val categories = listOf("餐饮", "交通", "购物", "娱乐", "其他")

    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("编辑账目", style = MaterialTheme.typography.headlineSmall)

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("金额") },
                placeholder = { Text("例如：25.8") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            DropdownMenuSelector(
                label = "币种",
                options = currencies,
                selected = currency,
                onSelected = { currency = it }
            )

            DropdownMenuSelector(
                label = "类别",
                options = categories,
                selected = category,
                onSelected = { category = it }
            )

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("备注（可选）") },
                placeholder = { Text("例如：星巴克咖啡") }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = onDismiss) {
                    Text("取消")
                }
                Button(
                    onClick = {
                        if (amount.isNotBlank()) {
                            val updated = transaction.copy(
                                amount = amount.toBigDecimal(),
                                currency = currency,
                                category = category,
                                note = note
                            )
                            onSave(updated)
                        }
                    },
                    enabled = amount.isNotBlank()
                ) {
                    Text("更新")
                }
            }
        }
    }
}