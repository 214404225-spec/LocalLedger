package com.example.localledger.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import com.example.localledger.data.local.CategoryExpense
import com.example.localledger.data.local.DayExpense

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    expensesByCategory: List<CategoryExpense>,
    expensesByDay: List<DayExpense>,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(StatsTab.Category) }

    Surface {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("统计") },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                }
            )

            TabRow(
                selectedTabIndex = selectedTab.ordinal,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                StatsTab.entries.forEach { tab ->
                    Tab(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        text = { Text(tab.title) }
                    )
                }
            }

            when (selectedTab) {
                StatsTab.Category -> CategoryChart(expensesByCategory)
                StatsTab.Day -> DayChart(expensesByDay)
            }
        }
    }
}

enum class StatsTab(val title: String) {
    Category("类别"),
    Day("近期")
}

@Composable
fun CategoryChart(data: List<CategoryExpense>) {
    if (data.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("暂无数据")
        }
        return
    }

    val total = data.sumOf { it.total.toDouble() }.coerceAtLeast(1.0)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        data.forEach { item ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${item.category} (${item.total})",
                    modifier = Modifier.weight(0.4f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                // ✅ 修正：使用 weight + fill 代替已弃用的 fill=false
                Box(
                    modifier = Modifier
                        .weight(0.6f)
                        .height(24.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant) // 给进度条一个背景色
                ) {
                    val fraction = (item.total.toDouble() / total).toFloat()
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction)
                            .fillMaxHeight()
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun DayChart(data: List<DayExpense>) {
    if (data.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("暂无数据")
        }
        return
    }

    val maxAmount = data.maxOfOrNull { it.total.toDouble() }?.coerceAtLeast(1.0) ?: 1.0
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        data.reversed().forEach { item ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                // ✅ 关键修复：处理 item.day 可能为 null
                Text(
                    text = item.day ?: "未知日期", // ← 安全访问
                    modifier = Modifier.width(60.dp),
                    style = MaterialTheme.typography.labelMedium
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(120.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .width(32.dp)
                            .height((item.total.toDouble() / maxAmount * 100).dp)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                    Text(
                        text = item.total.toString(),
                        modifier = Modifier.align(Alignment.TopStart),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}