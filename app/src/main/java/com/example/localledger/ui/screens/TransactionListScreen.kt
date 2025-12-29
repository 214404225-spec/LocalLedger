package com.example.localledger.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled. Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.localledger.data.model.TransactionEntity
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(
    transactions:  List<TransactionEntity>,
    onEdit: (TransactionEntity) -> Unit,
    onDelete:  (TransactionEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    if (transactions.isEmpty()) {
        Box(
            modifier = modifier. fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "暂无账目\n点击 ➕ 添加第一笔记录",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign. Center
            )
        }
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
    ) {
        items(transactions, key = { it.id }) { transaction ->
            val dismissState = rememberSwipeToDismissBoxState(
                confirmValueChange = { dismissValue ->
                    if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                        onDelete(transaction)
                        true
                    } else {
                        false
                    }
                }
            )

            SwipeToDismissBox(
                state = dismissState,
                enableDismissFromStartToEnd = false,
                backgroundContent = {
                    val color by animateColorAsState(
                        targetValue = when (dismissState.targetValue) {
                            SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.error
                            else -> Color. Transparent
                        },
                        label = "backgroundColor"
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            . background(color)
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "删除",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            ) {
                TransactionItem(
                    transaction = transaction,
                    onLongClick = { onEdit(transaction) },
                    modifier = Modifier. combinedClickable(
                        onClick = { /* 无操作 */ },
                        onLongClick = { onEdit(transaction) }
                    )
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun TransactionItem(
    transaction: TransactionEntity,
    onLongClick: () -> Unit = {},
    modifier:  Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults. cardColors(
            containerColor = MaterialTheme.colorScheme. surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            // 类别和备注
            Text(
                text = buildString {
                    append(transaction.category)
                    if (transaction.note.isNotBlank()) {
                        append(" · ${transaction.note}")
                    }
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 金额显示
            Text(
                text = buildString {
                    if (transaction.currency != "CNY") {
                        append(transaction.currency)
                        append(" ")
                    }
                    append(transaction.amount.setScale(2, java.math.RoundingMode.HALF_UP).toString())
                    if (transaction.currency != "CNY") {
                        append(" ≈ ¥")
                        append(transaction.baseAmount.setScale(2, java.math. RoundingMode.HALF_UP).toString())
                    }
                },
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier. height(4.dp))
            
            // 时间戳
            Text(
                text = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                    .format(Date(transaction.timestamp)),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}
