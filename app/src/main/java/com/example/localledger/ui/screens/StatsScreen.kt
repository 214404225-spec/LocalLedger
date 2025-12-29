package com.example.localledger.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.localledger.data.local.CategoryExpense
import com.example.localledger.data.local.DayExpense

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    expensesByCategory: List<CategoryExpense>,
    expensesByDay: List<DayExpense>,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(StatsTab.Category) }

    Column(modifier = modifier) {
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
                Box(
                    modifier = Modifier
                        .weight(0.6f)
                        .height(24.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    val fraction = (item.total.toDouble() / total).toFloat()
                    if (fraction > 0) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction)
                                .fillMaxHeight()
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }
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
                Text(
                    text = item.day ?: "未知日期",
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