package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MarketRepository
import com.example.model.StrategyBenchmark
import com.example.ui.components.SparklineChart
import com.example.ui.theme.*

@Composable
fun CompareStrategiesScreen(
    repository: MarketRepository,
    onBack: () -> Unit,
    onSelectStrategyForBacktest: (String) -> Unit,
    onDeployStrategy: (String) -> Unit,
    onShowToast: (String) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("All") }
    var sortBy by remember { mutableStateOf("Net Yield") } // "Net Yield", "Win Rate", "Profit Factor", "Sharpe"

    val benchmarks by repository.strategyBenchmarks.collectAsState()

    val filteredList = remember(benchmarks, selectedCategory, sortBy) {
        val categoryFiltered = if (selectedCategory == "All") {
            benchmarks
        } else {
            benchmarks.filter { it.category.equals(selectedCategory, ignoreCase = true) }
        }

        when (sortBy) {
            "Net Yield" -> categoryFiltered.sortedByDescending { it.netYieldPercent }
            "Win Rate" -> categoryFiltered.sortedByDescending { it.winRatePercent }
            "Profit Factor" -> categoryFiltered.sortedByDescending { it.profitFactor }
            "Sharpe" -> categoryFiltered.sortedByDescending { it.sharpeRatio }
            "Lowest Drawdown" -> categoryFiltered.sortedBy { it.maxDrawdownPercent } // less negative is better
            else -> categoryFiltered
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
            .testTag("compare_strategies_screen"),
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Top Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceContainerLowest)
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(36.dp).testTag("compare_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = OnSurface
                        )
                    }
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Strategy Matrix Leaderboard",
                                color = OnSurface,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(PrimaryContainer)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "16 MODELS",
                                    color = Primary,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Text(
                            text = "Standardized 90-day multi-asset benchmark",
                            color = OnSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Top KPI Performance Cards
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Top Performer
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("TOP YIELD", color = OnSurfaceVariant, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("+142.8%", color = Primary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("Supertrend v2", color = OnSurface, fontSize = 10.sp, maxLines = 1)
                    }
                }

                // Lowest Drawdown
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("LOWEST DD", color = OnSurfaceVariant, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("-6.4%", color = Tertiary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("Bollinger Reversion", color = OnSurface, fontSize = 10.sp, maxLines = 1)
                    }
                }

                // Best Sharpe
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("BEST SHARPE", color = OnSurfaceVariant, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("2.41", color = Primary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("Trend Pullback", color = OnSurface, fontSize = 10.sp, maxLines = 1)
                    }
                }
            }
        }

        // Category Pills & Sort Row
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Category tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("All", "Trend", "Reversion", "Volatility").forEach { cat ->
                        val isSelected = selectedCategory == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) Primary.copy(alpha = 0.2f) else SurfaceContainerLow)
                                .border(
                                    1.dp,
                                    if (isSelected) Primary else OutlineVariant,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedCategory = cat }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = cat,
                                color = if (isSelected) Primary else OnSurfaceVariant,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Sort row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SORT BENCHMARKS BY:",
                        color = OnSurfaceVariant,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Net Yield", "Win Rate", "Profit Factor", "Sharpe", "Lowest Drawdown").forEach { s ->
                            val isSelected = sortBy == s
                            Text(
                                text = s,
                                color = if (isSelected) Primary else OnSurfaceVariant,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .clickable { sortBy = s }
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // Strategy Cards List
        items(filteredList) { strat ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .testTag("strategy_item_${strat.rank}"),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (strat.rank == 1) Primary.copy(alpha = 0.6f) else OutlineVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Top rank, name & category
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Rank Badge
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (strat.rank) {
                                            1 -> Primary
                                            2 -> Tertiary
                                            3 -> Color(0xFFC0C0C0)
                                            else -> SurfaceBright
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "#${strat.rank}",
                                    color = SurfaceContainerLowest,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Column {
                                Text(
                                    text = strat.name,
                                    color = OnSurface,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = strat.category.uppercase(),
                                        color = OnSurfaceVariant,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "• ${strat.tradesCount} Trades",
                                        color = OnSurfaceVariant,
                                        fontSize = 10.sp
                                    )
                                    Text(
                                        text = "• ${strat.badgeNote}",
                                        color = Tertiary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        // Sparkline
                        Box(modifier = Modifier.size(width = 64.dp, height = 24.dp)) {
                            SparklineChart(
                                points = strat.sparklinePoints,
                                lineColor = if (strat.netYieldPercent >= 0) Primary else Secondary,
                                fillColor = if (strat.netYieldPercent >= 0) Primary else Secondary,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    HorizontalDivider(color = OutlineVariant.copy(alpha = 0.3f))

                    // Metrics Grid (Yield, WinRate, PF, Sharpe, MaxDD)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("NET YIELD", color = OnSurfaceVariant, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = "${if (strat.netYieldPercent >= 0) "+" else ""}${strat.netYieldPercent}%",
                                color = if (strat.netYieldPercent >= 0) Primary else Secondary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column {
                            Text("WIN RATE", color = OnSurfaceVariant, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = "${strat.winRatePercent}%",
                                color = OnSurface,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column {
                            Text("PROFIT FACTOR", color = OnSurfaceVariant, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = "${strat.profitFactor}",
                                color = Tertiary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column {
                            Text("SHARPE", color = OnSurfaceVariant, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = "${strat.sharpeRatio}",
                                color = OnSurface,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column {
                            Text("MAX DD", color = OnSurfaceVariant, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = "${strat.maxDrawdownPercent}%",
                                color = Secondary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                onSelectStrategyForBacktest(strat.name)
                                onShowToast("Loaded ${strat.name} into Backtester")
                            },
                            modifier = Modifier.weight(1f).height(34.dp),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(0.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = OnSurface)
                        ) {
                            Icon(imageVector = Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Backtest Model", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Button(
                            onClick = {
                                onDeployStrategy(strat.name)
                            },
                            modifier = Modifier.weight(1f).height(34.dp),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(0.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Primary)
                        ) {
                            Icon(imageVector = Icons.Default.RocketLaunch, contentDescription = null, tint = SurfaceContainerLowest, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Deploy Bot", color = SurfaceContainerLowest, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
