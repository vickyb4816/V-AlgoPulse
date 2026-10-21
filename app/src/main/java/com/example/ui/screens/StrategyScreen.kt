package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.model.BacktestFill
import com.example.ui.components.EquityCurveChart
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun StrategyScreen(
    repository: MarketRepository,
    onOpenCompare: () -> Unit,
    onOpenPineEditor: () -> Unit,
    onShowToast: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedStrategySubTab by remember { mutableStateOf("backtest") }
    var selectedPair by remember { mutableStateOf("SOLUSDT.P") }
    var selectedInterval by remember { mutableStateOf("15m") }
    var selectedEngine by remember { mutableStateOf("Supertrend + EMA Pullback v2") }
    var leverageFactor by remember { mutableFloatStateOf(10f) }

    var isSimulating by remember { mutableStateOf(false) }
    var simulationComplete by remember { mutableStateOf(false) }

    var showPairPicker by remember { mutableStateOf(false) }
    var showEnginePicker by remember { mutableStateOf(false) }
    var showAllTrades by remember { mutableStateOf(false) }

    val backtestFills by repository.backtestTrades.collectAsState()

    val availableEngines = listOf(
        "Supertrend + EMA Pullback v2",
        "EMA Golden Cross + ATR Filter",
        "Bollinger Band Mean Reversion",
        "Keltner Channel Squeeze",
        "MACD Zero-Lag Histogram",
        "Pine Script Custom (.algo)"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
            .padding(horizontal = 12.dp)
            .testTag("strategy_screen_content"),
        contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. Sub-Navigation Pill Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceContainerLow, RoundedCornerShape(24.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (selectedStrategySubTab == "backtest") PrimaryContainer else Color.Transparent)
                        .clickable { selectedStrategySubTab = "backtest" }
                        .padding(vertical = 7.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.QueryStats, contentDescription = null, tint = if (selectedStrategySubTab == "backtest") Color(0xFF00363D) else OnSurfaceVariant, modifier = Modifier.size(15.dp))
                        Text(
                            text = "Backtest",
                            fontFamily = JetBrainsMono,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedStrategySubTab == "backtest") Color(0xFF00363D) else OnSurfaceVariant
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (selectedStrategySubTab == "compare") PrimaryContainer else Color.Transparent)
                        .clickable {
                            selectedStrategySubTab = "compare"
                            onOpenCompare()
                        }
                        .padding(vertical = 7.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.StackedLineChart, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(15.dp))
                        Text(
                            text = "Compare All",
                            fontFamily = JetBrainsMono,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = OnSurfaceVariant
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (selectedStrategySubTab == "editor") PrimaryContainer else Color.Transparent)
                        .clickable {
                            selectedStrategySubTab = "editor"
                            onOpenPineEditor()
                        }
                        .padding(vertical = 7.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.Code, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(15.dp))
                        Text(
                            text = "Pine Editor",
                            fontFamily = JetBrainsMono,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = OnSurfaceVariant
                        )
                    }
                }
            }
        }

        // 2. Configuration Matrix Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("config_matrix_card"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainer)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(PrimaryContainer))
                            Text("Configuration Matrix", fontFamily = SpaceGrotesk, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                        }
                        Box(
                            modifier = Modifier
                                .background(SurfaceContainerHighest, RoundedCornerShape(12.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                Icon(Icons.Default.Tune, contentDescription = null, tint = Primary, modifier = Modifier.size(12.dp))
                                Text("V2.4 Param", fontFamily = JetBrainsMono, fontSize = 10.sp, color = Primary)
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(SurfaceContainerLowest)
                                .clickable { showPairPicker = true }
                                .padding(8.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("ASSET PAIR", fontFamily = JetBrainsMono, fontSize = 9.sp, color = OnSurfaceVariant)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Box(
                                            modifier = Modifier.size(16.dp).clip(CircleShape).background(SecondaryContainer.copy(alpha = 0.2f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("S", fontFamily = JetBrainsMono, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Secondary)
                                        }
                                        Text(selectedPair, fontFamily = JetBrainsMono, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                                    }
                                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Outline, modifier = Modifier.size(16.dp))
                                }
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(SurfaceContainerLowest)
                                .clickable { onShowToast("Interval set to 15m (1,500 Bars)") }
                                .padding(8.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("INTERVAL & CANDLES", fontFamily = JetBrainsMono, fontSize = 9.sp, color = OnSurfaceVariant)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Box(
                                            modifier = Modifier.background(SurfaceContainerHigh, RoundedCornerShape(3.dp)).padding(horizontal = 4.dp, vertical = 1.dp)
                                        ) {
                                            Text(selectedInterval, fontFamily = JetBrainsMono, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryFixed)
                                        }
                                        Text("1,500 Bars", fontFamily = JetBrainsMono, fontSize = 11.sp, color = OnSurface)
                                    }
                                    Icon(Icons.Default.History, contentDescription = null, tint = Outline, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceContainerLowest)
                            .clickable { showEnginePicker = true }
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text("ALGORITHM ENGINE", fontFamily = JetBrainsMono, fontSize = 9.sp, color = OnSurfaceVariant)
                                Text(selectedEngine, fontFamily = SpaceGrotesk, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Primary)
                            }
                            Icon(Icons.Default.SwapVert, contentDescription = null, tint = Outline, modifier = Modifier.size(18.dp))
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceContainerLowest.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.Bolt, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(14.dp))
                                Text("Leverage Factor", fontFamily = JetBrainsMono, fontSize = 11.sp, color = OnSurfaceVariant)
                            }
                            Text("${leverageFactor.toInt()}x", fontFamily = JetBrainsMono, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SecondaryContainer)
                        }

                        Slider(
                            value = leverageFactor,
                            onValueChange = { leverageFactor = it },
                            valueRange = 1f..50f,
                            steps = 48,
                            colors = SliderDefaults.colors(
                                thumbColor = PrimaryContainer,
                                activeTrackColor = PrimaryContainer,
                                inactiveTrackColor = SurfaceContainerHighest
                            ),
                            modifier = Modifier.height(24.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Fee Model", fontFamily = JetBrainsMono, fontSize = 10.sp, color = OnSurfaceVariant)
                            Box(
                                modifier = Modifier
                                    .background(SurfaceContainerHigh, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("Maker 0.02% / Taker 0.05%", fontFamily = JetBrainsMono, fontSize = 10.sp, color = OnSurface)
                            }
                        }
                    }

                    Button(
                        onClick = {
                            if (!isSimulating) {
                                isSimulating = true
                                simulationComplete = false
                                coroutineScope.launch {
                                    delay(800)
                                    isSimulating = false
                                    simulationComplete = true
                                    onShowToast("Backtest executed: Yield +142.8% on $selectedPair")
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("run_backtest_btn"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (simulationComplete) SecondaryContainer else PrimaryContainer,
                            contentColor = if (simulationComplete) Color(0xFF00391E) else Color(0xFF00363D)
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            if (isSimulating) {
                                CircularProgressIndicator(color = Color(0xFF00363D), modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                Text("Simulating Orders...", fontFamily = JetBrainsMono, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            } else if (simulationComplete) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                                Text("Strategy Backtested (Up to date)", fontFamily = JetBrainsMono, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            } else {
                                Icon(Icons.Default.PlayCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                Text("Execute Quantitative Backtest", fontFamily = SpaceGrotesk, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // 3. Performance Telemetry Grid (2x3)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Signal Telemetry & Yield", fontFamily = SpaceGrotesk, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = OnSurface)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(SecondaryContainer))
                        Text("Simulated 99.8% Fill", fontFamily = JetBrainsMono, fontSize = 10.sp, color = SecondaryContainer)
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TelemetryCard(
                        title = "NET RETURN",
                        primaryValue = "+142.8%",
                        secondaryValue = "+$14,280.00 USDT",
                        valueColor = SecondaryContainer,
                        icon = Icons.Default.TrendingUp,
                        modifier = Modifier.weight(1f)
                    )
                    TelemetryCard(
                        title = "WIN RATE",
                        primaryValue = "68.4%",
                        secondaryValue = "38 Won • 18 Lost",
                        valueColor = PrimaryContainer,
                        icon = Icons.Default.PieChart,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow)
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("EXECUTIONS", fontFamily = JetBrainsMono, fontSize = 9.sp, color = OnSurfaceVariant)
                                Icon(Icons.Default.Tag, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(13.dp))
                            }
                            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("56", fontFamily = JetBrainsMono, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                                Text("Trades", fontFamily = JetBrainsMono, fontSize = 10.sp, color = OnSurfaceVariant)
                            }
                            Row(modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp))) {
                                Box(modifier = Modifier.weight(0.68f).fillMaxHeight().background(SecondaryContainer))
                                Box(modifier = Modifier.weight(0.32f).fillMaxHeight().background(LaserCrimson))
                            }
                        }
                    }

                    TelemetryCard(
                        title = "PROFIT FACTOR",
                        primaryValue = "2.41",
                        secondaryValue = "Gross 2.9x • Sharpe 2.18",
                        valueColor = SecondaryFixed,
                        icon = Icons.Default.Balance,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TelemetryCard(
                        title = "MAX DRAWDOWN",
                        primaryValue = "-8.6%",
                        secondaryValue = "Peak-trough • Recov 2.4d",
                        valueColor = LaserCrimson,
                        icon = Icons.Default.TrendingDown,
                        modifier = Modifier.weight(1f)
                    )
                    TelemetryCard(
                        title = "AVG TRADE",
                        primaryValue = "+2.55%",
                        secondaryValue = "Per order • Exp: $255",
                        valueColor = Primary,
                        icon = Icons.Default.Percent,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 4. Dynamic Growth Equity Curve
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("DYNAMIC GROWTH", fontFamily = JetBrainsMono, fontSize = 9.sp, color = OnSurfaceVariant)
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("$24,280.00", fontFamily = JetBrainsMono, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                                Box(
                                    modifier = Modifier.background(SecondaryContainer.copy(alpha = 0.15f), RoundedCornerShape(10.dp)).padding(horizontal = 5.dp, vertical = 2.dp)
                                ) {
                                    Text("+$14,280 (+142.8%)", fontFamily = JetBrainsMono, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = SecondaryContainer)
                                }
                            }
                        }
                        Box(
                            modifier = Modifier.background(SurfaceContainerHigh, RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Text("Base: $10k", fontFamily = JetBrainsMono, fontSize = 10.sp, color = OnSurface)
                        }
                    }

                    EquityCurveChart()

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Bar #1 (Start)", fontFamily = JetBrainsMono, fontSize = 9.sp, color = Outline)
                        Text("Bar #750", fontFamily = JetBrainsMono, fontSize = 9.sp, color = Outline)
                        Text("ATH $24.28k", fontFamily = JetBrainsMono, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = SecondaryFixed)
                    }
                }
            }
        }

        // 5. Pine Script v5 Logic Preview box
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainer)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.Terminal, contentDescription = null, tint = Primary, modifier = Modifier.size(16.dp))
                            Text("Pine Script v5 Logic", fontFamily = SpaceGrotesk, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = OnSurface)
                        }
                        Button(
                            onClick = onOpenPineEditor,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainerHigh, contentColor = Primary)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(12.dp))
                                Text("Inspect Source", fontFamily = JetBrainsMono, fontSize = 10.sp)
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceContainerLowest)
                            .padding(10.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text("//@version=5", fontFamily = JetBrainsMono, fontSize = 10.sp, color = OutlineVariant)
                            Text("strategy(\"AlgoPulse Supertrend\", overlay=true, margin_long=100)", fontFamily = JetBrainsMono, fontSize = 10.sp, color = Primary)
                            Text("atrPeriod = input.int(10, \"ATR Period\")", fontFamily = JetBrainsMono, fontSize = 10.sp, color = PrimaryFixedDim)
                            Text("src = hlc3", fontFamily = JetBrainsMono, fontSize = 10.sp, color = OnSurface)
                            Text("if (ta.crossover(close, upperBand))", fontFamily = JetBrainsMono, fontSize = 10.sp, color = SecondaryFixed)
                            Text("    strategy.entry(\"Long\", strategy.long)", fontFamily = JetBrainsMono, fontSize = 10.sp, color = Outline)
                        }
                    }
                }
            }
        }

        // 6. Recent Backtest Fills
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.History, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(16.dp))
                    Text("Recent Backtest Fills", fontFamily = SpaceGrotesk, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = OnSurface)
                }
                Text("Latest 12 Executions", fontFamily = JetBrainsMono, fontSize = 10.sp, color = Outline)
            }
        }

        val displayedFills = if (showAllTrades) backtestFills else backtestFills.take(6)
        items(displayedFills) { fill ->
            BacktestFillRow(fill = fill)
        }

        item {
            Button(
                onClick = {
                    showAllTrades = !showAllTrades
                    onShowToast(if (showAllTrades) "Loaded full trade history" else "Showing latest 6 executions")
                },
                modifier = Modifier.fillMaxWidth().height(38.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainerLow, contentColor = OnSurfaceVariant)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(if (showAllTrades) "Show Less Fills" else "Load Trades #1 to #50", fontFamily = JetBrainsMono, fontSize = 11.sp)
                    Icon(if (showAllTrades) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward, contentDescription = null, modifier = Modifier.size(14.dp))
                }
            }
        }
    }

    if (showPairPicker) {
        AlertDialog(
            onDismissRequest = { showPairPicker = false },
            title = { Text("Select Backtest Pair", fontFamily = SpaceGrotesk, color = OnSurface) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("SOLUSDT.P", "BTCUSDT.P", "ETHUSDT.P", "NEARUSDT.P", "AVAXUSDT.P").forEach { p ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (p == selectedPair) SurfaceContainerHigh else SurfaceContainerLowest)
                                .clickable {
                                    selectedPair = p
                                    showPairPicker = false
                                    onShowToast("Matrix switched to $p")
                                }
                                .padding(12.dp)
                        ) {
                            Text(p, fontFamily = JetBrainsMono, fontWeight = FontWeight.Bold, color = OnSurface)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showPairPicker = false }) { Text("Cancel", color = Primary) }
            },
            containerColor = SurfaceContainerHigh
        )
    }

    if (showEnginePicker) {
        AlertDialog(
            onDismissRequest = { showEnginePicker = false },
            title = { Text("Select Quant Algorithm", fontFamily = SpaceGrotesk, color = OnSurface) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    availableEngines.forEach { eng ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (eng == selectedEngine) SurfaceContainerHigh else SurfaceContainerLowest)
                                .clickable {
                                    selectedEngine = eng
                                    showEnginePicker = false
                                    onShowToast("Engine selected: $eng")
                                }
                                .padding(12.dp)
                        ) {
                            Text(eng, fontFamily = JetBrainsMono, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showEnginePicker = false }) { Text("Cancel", color = Primary) }
            },
            containerColor = SurfaceContainerHigh
        )
    }
}

@Composable
private fun TelemetryCard(
    title: String,
    primaryValue: String,
    secondaryValue: String,
    valueColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(title, fontFamily = JetBrainsMono, fontSize = 9.sp, color = OnSurfaceVariant)
                Icon(icon, contentDescription = null, tint = valueColor, modifier = Modifier.size(13.dp))
            }
            Text(primaryValue, fontFamily = JetBrainsMono, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = valueColor)
            Text(secondaryValue, fontFamily = JetBrainsMono, fontSize = 10.sp, color = Outline)
        }
    }
}

@Composable
private fun BacktestFillRow(fill: BacktestFill) {
    val isWin = fill.returnPercent >= 0
    val outcomeColor = if (isWin) NeonEmerald else LaserCrimson

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceContainerLow)
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(outcomeColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isWin) Icons.Default.NorthEast else Icons.Default.SouthEast,
                    contentDescription = null,
                    tint = outcomeColor,
                    modifier = Modifier.size(16.dp)
                )
            }

            Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("#${fill.id} ${if (fill.isLong) "LONG" else "SHORT"} ${fill.pair}", fontFamily = JetBrainsMono, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                    Box(
                        modifier = Modifier
                            .background(outcomeColor.copy(alpha = 0.15f), RoundedCornerShape(3.dp))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(fill.outcomeType, fontFamily = JetBrainsMono, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = outcomeColor)
                    }
                }
                Text(
                    text = "$${fill.entryPrice} → $${fill.exitPrice} • Fee $${fill.feeUsd}",
                    fontFamily = JetBrainsMono,
                    fontSize = 10.sp,
                    color = OnSurfaceVariant
                )
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = if (isWin) "+${fill.returnPercent}%" else "${fill.returnPercent}%",
                fontFamily = JetBrainsMono,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = outcomeColor
            )
            Text(fill.timeAgo, fontFamily = JetBrainsMono, fontSize = 9.sp, color = Outline)
        }
    }
}
