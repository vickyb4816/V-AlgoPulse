package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MarketRepository
import com.example.model.StrategyBenchmark
import com.example.ui.components.SparklineChart
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CompareStrategiesScreen(
    repository: MarketRepository,
    onBack: () -> Unit,
    onSelectStrategy: (StrategyBenchmark) -> Unit,
    onDeployStrategy: (StrategyBenchmark) -> Unit,
    onShowToast: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedCategoryFilter by remember { mutableStateOf("All") } // "All", "Trend", "Reversion", "Volatility"
    var sortIndex by remember { mutableIntStateOf(0) }
    val sortOptions = listOf("Sort: Net Return ▼", "Sort: Sharpe Ratio ▼", "Sort: Win Rate ▼", "Sort: Max DD ▲")

    var isRerunning by remember { mutableStateOf(false) }

    val rawBenchmarks by repository.strategyBenchmarks.collectAsState()

    val filteredBenchmarks = remember(rawBenchmarks, selectedCategoryFilter, sortIndex) {
        val filtered = if (selectedCategoryFilter == "All") {
            rawBenchmarks
        } else {
            rawBenchmarks.filter { it.category == selectedCategoryFilter }
        }

        when (sortIndex) {
            0 -> filtered.sortedByDescending { it.netYieldPercent }
            1 -> filtered.sortedByDescending { it.sharpeRatio }
            2 -> filtered.sortedByDescending { it.winRatePercent }
            else -> filtered.sortedBy { it.maxDrawdownPercent }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
            .statusBarsPadding()
            .padding(horizontal = 12.dp)
            .testTag("compare_screen_content"),
        contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 1. Navigation Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = OnSurfaceVariant)
                    }
                    Column {
                        Text("Compare All Strategies", fontFamily = SpaceGrotesk, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                        Text("16 Built-in Alpha Engines", fontFamily = JetBrainsMono, fontSize = 10.sp, color = OnSurfaceVariant)
                    }
                }

                Box(
                    modifier = Modifier
                        .background(SurfaceContainerLow, RoundedCornerShape(10.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("Leaderboard", fontFamily = JetBrainsMono, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Primary)
                }
            }
        }

        // 2. Benchmark Matrix Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Analytics, contentDescription = null, tint = PrimaryContainer, modifier = Modifier.size(18.dp))
                            Text("Strategy Benchmark Matrix", fontFamily = SpaceGrotesk, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                        }
                        Box(
                            modifier = Modifier.background(SurfaceContainerHigh, RoundedCornerShape(10.dp)).padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("SOLUSDT", fontFamily = JetBrainsMono, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = PrimaryFixedDim)
                        }
                    }

                    Text(
                        text = "15m (1,500 Bars) • 10x Cross Margin • Capital: 10,000 USDT",
                        fontFamily = JetBrainsMono,
                        fontSize = 10.sp,
                        color = OnSurfaceVariant
                    )

                    // Visual telemetry bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(2.5.dp))
                    ) {
                        Box(modifier = Modifier.weight(0.65f).fillMaxHeight().background(SecondaryContainer))
                        Box(modifier = Modifier.weight(0.25f).fillMaxHeight().background(PrimaryContainer))
                        Box(modifier = Modifier.weight(0.10f).fillMaxHeight().background(LaserCrimson))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("14 Profitable", fontFamily = JetBrainsMono, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Secondary)
                        Text("2 Neutral", fontFamily = JetBrainsMono, fontSize = 9.sp, color = OnSurfaceVariant)
                        Text("0 Liquidated", fontFamily = JetBrainsMono, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = LaserCrimson)
                    }
                }
            }
        }

        // 3. Filter & Sort Bar
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CategoryChip("All 16 Strategies", selectedCategoryFilter == "All") { selectedCategoryFilter = "All" }
                    CategoryChip("Trend Following", selectedCategoryFilter == "Trend") { selectedCategoryFilter = "Trend" }
                    CategoryChip("Mean Reversion", selectedCategoryFilter == "Reversion") { selectedCategoryFilter = "Reversion" }
                    CategoryChip("Breakout & Volatility", selectedCategoryFilter == "Volatility") { selectedCategoryFilter = "Volatility" }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceContainerHighest)
                            .clickable {
                                sortIndex = (sortIndex + 1) % sortOptions.size
                                onShowToast(sortOptions[sortIndex])
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.SwapVert, contentDescription = null, tint = Primary, modifier = Modifier.size(13.dp))
                            Text(sortOptions[sortIndex], fontFamily = JetBrainsMono, fontSize = 10.sp, color = Primary)
                        }
                    }
                }
            }
        }

        // 4. Leading Alpha Engine Spotlight
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerHigh)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier.size(28.dp).clip(RoundedCornerShape(6.dp)).background(SecondaryContainer.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = SecondaryContainer, modifier = Modifier.size(16.dp))
                            }
                            Column {
                                Text("LEADING ALPHA ENGINE", fontFamily = JetBrainsMono, fontSize = 8.sp, color = Secondary)
                                Text("Supertrend + EMA Pullback v2", fontFamily = SpaceGrotesk, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("+142.8%", fontFamily = JetBrainsMono, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SecondaryContainer)
                            Text("2.41 Sharpe Ratio", fontFamily = JetBrainsMono, fontSize = 9.sp, color = OnSurfaceVariant)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Column {
                                Text("Win Rate", fontFamily = JetBrainsMono, fontSize = 9.sp, color = Outline)
                                Text("68.4%", fontFamily = JetBrainsMono, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                            }
                            Column {
                                Text("Max Drawdown", fontFamily = JetBrainsMono, fontSize = 9.sp, color = Outline)
                                Text("-8.6%", fontFamily = JetBrainsMono, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SecondaryFixed)
                            }
                            Column {
                                Text("Sample Size", fontFamily = JetBrainsMono, fontSize = 9.sp, color = Outline)
                                Text("56 Trades", fontFamily = JetBrainsMono, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                            }
                        }

                        Button(
                            onClick = {
                                rawBenchmarks.firstOrNull()?.let { onDeployStrategy(it) }
                                onShowToast("Deployed Supertrend + EMA Pullback into Auto-Trade")
                            },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                            modifier = Modifier.height(30.dp),
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SecondaryContainer, contentColor = Color(0xFF00391E))
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.SmartToy, contentDescription = null, modifier = Modifier.size(14.dp))
                                Text("Auto-Deploy", fontFamily = SpaceGrotesk, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // 5. Ranked Strategy Cards List
        items(filteredBenchmarks) { item ->
            StrategyBenchmarkCard(
                item = item,
                onTestClick = {
                    onSelectStrategy(item)
                    onShowToast("Loaded ${item.name} into Chart Studio")
                },
                onParamsClick = {
                    onShowToast("Opening parameter tuner for ${item.name}")
                }
            )
        }

        // 6. Floating Action Matrix Controls
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        isRerunning = true
                        onShowToast("Running high-speed vector backtest for SOLUSDT...")
                        coroutineScope.launch {
                            delay(1000)
                            isRerunning = false
                            onShowToast("16 Strategies benchmark updated!")
                        }
                    },
                    modifier = Modifier.weight(1f).height(44.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainer, contentColor = Color(0xFF00363D))
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        if (isRerunning) {
                            CircularProgressIndicator(color = Color(0xFF00363D), modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Replay, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                        Text("Re-run All on Current Coin", fontFamily = SpaceGrotesk, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Button(
                    onClick = { onShowToast("Matrix CSV generated (16 algos • SOLUSDT)") },
                    modifier = Modifier.height(44.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainerHigh, contentColor = OnSurface)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.FileDownload, contentDescription = null, tint = PrimaryContainer, modifier = Modifier.size(16.dp))
                        Text("Export CSV", fontFamily = SpaceGrotesk, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) PrimaryContainer else SurfaceContainerHigh)
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = text,
            fontFamily = JetBrainsMono,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color(0xFF00363D) else OnSurfaceVariant
        )
    }
}

@Composable
private fun StrategyBenchmarkCard(
    item: StrategyBenchmark,
    onTestClick: () -> Unit,
    onParamsClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Header Row: Rank #, Name, Category, Yield
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val rankColor = when (item.rank) {
                        1 -> Color(0xFFFBBF24)
                        2 -> Color(0xFFCBD5E1)
                        3 -> Color(0xFFF59E0B)
                        else -> OnSurfaceVariant
                    }
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(rankColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("${item.rank}", fontFamily = SpaceGrotesk, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = rankColor)
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(item.name, fontFamily = SpaceGrotesk, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = OnSurface, maxLines = 1)
                            Box(
                                modifier = Modifier
                                    .background(SurfaceContainerHigh, RoundedCornerShape(3.dp))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(item.category, fontFamily = JetBrainsMono, fontSize = 8.sp, color = PrimaryContainer)
                            }
                        }
                        Text("${item.tradesCount} Trades • Profit Factor: ${item.profitFactor}", fontFamily = JetBrainsMono, fontSize = 9.sp, color = OnSurfaceVariant)
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("+${item.netYieldPercent}%", fontFamily = JetBrainsMono, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SecondaryContainer)
                    Text("NET YIELD", fontFamily = JetBrainsMono, fontSize = 8.sp, color = Secondary)
                }
            }

            // Telemetry Matrix Row with Sparkline
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceContainerLowest.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Column {
                        Text("Win Rate", fontFamily = JetBrainsMono, fontSize = 8.sp, color = Outline)
                        Text("${item.winRatePercent}%", fontFamily = JetBrainsMono, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Secondary)
                    }
                    Column {
                        Text("Sharpe", fontFamily = JetBrainsMono, fontSize = 8.sp, color = Outline)
                        Text("${item.sharpeRatio}", fontFamily = JetBrainsMono, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryContainer)
                    }
                    Column {
                        Text("Max DD", fontFamily = JetBrainsMono, fontSize = 8.sp, color = Outline)
                        Text("${item.maxDrawdownPercent}%", fontFamily = JetBrainsMono, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                    }
                }

                // Sparkline
                SparklineChart(
                    points = item.sparklinePoints,
                    lineColor = if (item.rank <= 3) SecondaryContainer else PrimaryContainer,
                    fillColor = if (item.rank <= 3) SecondaryContainer else null,
                    modifier = Modifier.width(60.dp).height(18.dp)
                )
            }

            // Bottom Actions: Note, Params, Select & Test
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    Icon(Icons.Default.Verified, contentDescription = null, tint = Secondary, modifier = Modifier.size(12.dp))
                    Text(item.badgeNote, fontFamily = JetBrainsMono, fontSize = 8.sp, color = OnSurfaceVariant)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = onParamsClick,
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                        modifier = Modifier.height(26.dp),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainerHigh, contentColor = OnSurfaceVariant)
                    ) {
                        Text("Params", fontFamily = JetBrainsMono, fontSize = 9.sp)
                    }

                    Button(
                        onClick = onTestClick,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(26.dp),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainer, contentColor = Color(0xFF00363D))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(11.dp))
                            Text("Select & Test", fontFamily = JetBrainsMono, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
