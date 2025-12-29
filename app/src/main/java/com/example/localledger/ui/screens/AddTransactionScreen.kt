package com.example.localledger.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun AddTransactionScreen(
    onSave: (amount: String, currency: String, category: String, note: String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var amount by remember { mutableStateOf("") }
    var currency by remember { mutableStateOf("CNY") }
    var category by remember { mutableStateOf("餐饮") }
    var note by remember { mutableStateOf("") }

    val currencies = listOf("CNY", "USD", "EUR", "JPY", "GBP")
    val categories = listOf("餐饮", "交通", "购物", "娱乐", "其他")

    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("添加账目", style = MaterialTheme.typography.headlineSmall)

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
                            onSave(amount, currency, category, note)
                            onDismiss()
                        }
                    },
                    enabled = amount.isNotBlank()
                ) {
                    Text("保存")
                }
            }
        }
    }
}

@Composable
fun DropdownMenuSelector(
    label: String,
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(label, style = MaterialTheme.typography.labelMedium)
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(selected)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}