package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MarketRepository
import com.example.model.ActivePosition
import com.example.ui.components.InteractiveCandleChart
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartScreen(
    repository: MarketRepository,
    onShowToast: (String) -> Unit
) {
    val currentPrice by repository.currentPrice.collectAsState()
    val priceChange by repository.priceChangePercent.collectAsState()
    val selectedPair by repository.selectedPair.collectAsState()
    val selectedTf by repository.selectedTimeframe.collectAsState()
    val activePosition by repository.activePosition.collectAsState()

    var showPairDialog by remember { mutableStateOf(false) }
    var isFavorite by remember { mutableStateOf(true) }
    var showIndicators by remember { mutableStateOf(true) }
    var showTpSlSheet by remember { mutableStateOf(false) }

    val pairs = listOf("BTC/USDT", "ETH/USDT", "SOL/USDT", "NEAR/USDT", "AVAX/USDT", "SUI/USDT", "DOGE/USDT")
    val timeframes = listOf("1m", "5m", "15m", "1h", "4h")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
            .padding(horizontal = 12.dp)
            .testTag("chart_screen_content"),
        contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 1. Ticker Header & Favorite
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .clickable { showPairDialog = true }
                        .padding(vertical = 4.dp)
                ) {
                    Text(
                        text = selectedPair,
                        fontFamily = SpaceGrotesk,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface
                    )
                    Box(
                        modifier = Modifier
                            .background(SurfaceContainerHigh, RoundedCornerShape(4.dp))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "PERP",
                            fontFamily = JetBrainsMono,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = OnSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Select Pair",
                        tint = OnSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = {
                            isFavorite = !isFavorite
                            onShowToast(if (isFavorite) "Added to watchlist" else "Removed from watchlist")
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) AlertAmber else OnSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(
                        onClick = { onShowToast("Expanded fullscreen chart") },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fullscreen,
                            contentDescription = "Fullscreen",
                            tint = OnSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // 2. Price & 24h Stats Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "$67,421.26",
                            fontFamily = JetBrainsMono,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnSurface
                        )
                        Box(
                            modifier = Modifier
                                .background(SecondaryContainer.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "▲ +${priceChange}%",
                                fontFamily = JetBrainsMono,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SecondaryContainer
                            )
                        }
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text("24h High", fontFamily = JetBrainsMono, fontSize = 9.sp, color = Outline)
                        Text("$68,150.00", fontFamily = JetBrainsMono, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = OnSurface)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("24h Low", fontFamily = JetBrainsMono, fontSize = 9.sp, color = Outline)
                        Text("$65,210.00", fontFamily = JetBrainsMono, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = OnSurface)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("24h Vol", fontFamily = JetBrainsMono, fontSize = 9.sp, color = Outline)
                        Text("1.42B", fontFamily = JetBrainsMono, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = OnSurface)
                    }
                }
            }
        }

        // 3. Timeframe selector & Indicators bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Timeframe chips
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    timeframes.forEach { tf ->
                        val isSelected = tf == selectedTf
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) PrimaryContainer else SurfaceContainerLow)
                                .clickable { repository.setTimeframe(tf) }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = tf,
                                fontFamily = JetBrainsMono,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color(0xFF080E1A) else OnSurfaceVariant
                            )
                        }
                    }
                }

                // Controls: Candles, Indicators, Layers
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(SurfaceContainerLow, RoundedCornerShape(6.dp))
                            .clickable { onShowToast("Switched chart style: Hollow Candles") }
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            Icon(Icons.Default.CandlestickChart, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(13.dp))
                            Text("Candles", fontFamily = JetBrainsMono, fontSize = 10.sp, color = OnSurfaceVariant)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .background(if (showIndicators) SurfaceContainerHigh else SurfaceContainerLow, RoundedCornerShape(6.dp))
                            .clickable {
                                showIndicators = !showIndicators
                                onShowToast(if (showIndicators) "Indicators overlay enabled" else "Indicators hidden")
                            }
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text("✦ 3", fontFamily = JetBrainsMono, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryContainer)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .background(SurfaceContainerLow, RoundedCornerShape(6.dp))
                            .clickable { onShowToast("Chart settings opened") }
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = "Settings", tint = OnSurfaceVariant, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }

        // 4. Indicator Values Pill Row
        if (showIndicators) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IndicatorChip(label = "EMA 20", value = "67,112.4", dotColor = ElectricCyan)
                    IndicatorChip(label = "EMA 50", value = "66,540.8", dotColor = Color(0xFF00B4D8))
                    IndicatorChip(label = "Supertrend", value = "Bullish", dotColor = NeonEmerald)
                }
            }
        }

        // 5. Candlestick Canvas Chart with Target Levels
        item {
            InteractiveCandleChart(
                showIndicators = showIndicators
            )
        }

        // 6. Funding Rate & Order Book Depth Telemetry Row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceContainerLow, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Funding Rate
                Column {
                    Text("Funding Rate", fontFamily = JetBrainsMono, fontSize = 9.sp, color = Outline)
                    Text("+0.0100%", fontFamily = JetBrainsMono, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = NeonEmerald)
                    Text("(03:14:22)", fontFamily = JetBrainsMono, fontSize = 8.sp, color = Outline)
                }

                // Order Book Depth
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Order Book Depth", fontFamily = JetBrainsMono, fontSize = 9.sp, color = Outline)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("58% Bids", fontFamily = JetBrainsMono, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NeonEmerald)
                        Text("/", fontFamily = JetBrainsMono, fontSize = 10.sp, color = Outline)
                        Text("42% Asks", fontFamily = JetBrainsMono, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = LaserCrimson)
                    }
                    // Mini Depth Bar
                    Row(
                        modifier = Modifier
                            .width(80.dp)
                            .height(3.dp)
                            .clip(RoundedCornerShape(1.5.dp))
                    ) {
                        Box(modifier = Modifier.weight(0.58f).fillMaxHeight().background(NeonEmerald))
                        Box(modifier = Modifier.weight(0.42f).fillMaxHeight().background(LaserCrimson))
                    }
                }

                // Index / Mark
                Column(horizontalAlignment = Alignment.End) {
                    Text("Index / Mark", fontFamily = JetBrainsMono, fontSize = 9.sp, color = Outline)
                    Text("$67,410 /", fontFamily = JetBrainsMono, fontSize = 10.sp, color = OnSurface)
                    Text("$67,418", fontFamily = JetBrainsMono, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Primary)
                }
            }
        }

        // 7. Active Position Card
        item {
            activePosition?.let { pos ->
                ActivePositionCard(
                    pos = pos,
                    onTpSlClick = { showTpSlSheet = true },
                    onReverseClick = {
                        repository.reversePosition()
                        onShowToast("Position reversed: ${if (pos.isLong) "SHORT" else "LONG"}")
                    },
                    onExitClick = {
                        repository.closeActivePosition()
                        onShowToast("Position closed at market price")
                    }
                )
            } ?: Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceContainerLow, RoundedCornerShape(12.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("No Active Position", fontFamily = SpaceGrotesk, fontSize = 14.sp, color = OnSurfaceVariant)
                    Button(
                        onClick = {
                            repository.deployBot(
                                com.example.model.TradeBot(
                                    id = "bot_btc_reopen",
                                    pair = "BTC/USDT",
                                    strategyName = "Supertrend Pullback",
                                    leverage = 10,
                                    isLong = true,
                                    isActive = true,
                                    isScanning = false,
                                    timeframe = "5m",
                                    sizePercent = 10,
                                    sizeUsd = 1000.0,
                                    stopLossPercent = 1.5,
                                    trailingStopPercent = 0.8,
                                    entryPrice = 67420.0,
                                    markPrice = 67420.0,
                                    targetPrice = 69200.0,
                                    currentPnlUsd = 0.0,
                                    currentPnlPercent = 0.0,
                                    progressPercent = 0.1f
                                )
                            )
                            onShowToast("Simulated position opened: LONG 10x BTCUSDT")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainer, contentColor = Color(0xFF00363D))
                    ) {
                        Text("Open Demo Position", fontFamily = JetBrainsMono, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Pair Picker Dialog
    if (showPairDialog) {
        AlertDialog(
            onDismissRequest = { showPairDialog = false },
            title = {
                Text("Select Contract Pair", fontFamily = SpaceGrotesk, color = OnSurface)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    pairs.forEach { p ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (p == selectedPair) SurfaceContainerHigh else SurfaceContainerLowest)
                                .clickable {
                                    repository.setPair(p)
                                    showPairDialog = false
                                    onShowToast("Switched pair to $p")
                                }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(p, fontFamily = JetBrainsMono, fontWeight = FontWeight.SemiBold, color = OnSurface)
                            if (p == selectedPair) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = PrimaryContainer, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showPairDialog = false }) {
                    Text("Close", color = Primary)
                }
            },
            containerColor = SurfaceContainerHigh
        )
    }

    // TP/SL Sheet
    if (showTpSlSheet) {
        ModalBottomSheet(
            onDismissRequest = { showTpSlSheet = false },
            containerColor = SurfaceContainerHigh
        ) {
            TpSlConfigSheet(
                currentTp = activePosition?.tpPrice ?: 69200.0,
                currentSl = activePosition?.slPrice ?: 65950.0,
                currentTrail = activePosition?.trailPrice ?: 66300.0,
                onSave = { tp, sl, trail ->
                    repository.updateTpSl(tp, sl, trail)
                    showTpSlSheet = false
                    onShowToast("TP/SL updated: TP $$tp, SL $$sl")
                },
                onCancel = { showTpSlSheet = false }
            )
        }
    }
}

@Composable
private fun IndicatorChip(label: String, value: String, dotColor: Color) {
    Row(
        modifier = Modifier
            .background(SurfaceContainerLowest, RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(dotColor))
        Text(
            text = "$label: $value",
            fontFamily = JetBrainsMono,
            fontSize = 9.sp,
            color = OnSurfaceVariant
        )
    }
}

@Composable
private fun ActivePositionCard(
    pos: ActivePosition,
    onTpSlClick: () -> Unit,
    onReverseClick: () -> Unit,
    onExitClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("active_position_card"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header Row: LONG 10X BTCUSDT Perp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                if (pos.isLong) NeonEmerald.copy(alpha = 0.18f) else LaserCrimson.copy(alpha = 0.18f),
                                RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (pos.isLong) "LONG ${pos.leverage}X" else "SHORT ${pos.leverage}X",
                            fontFamily = JetBrainsMono,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (pos.isLong) NeonEmerald else LaserCrimson
                        )
                    }
                    Text(
                        text = pos.pair,
                        fontFamily = JetBrainsMono,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface
                    )
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(PrimaryContainer))
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("TradingView", fontFamily = JetBrainsMono, fontSize = 10.sp, color = Primary)
                    Icon(Icons.Default.OpenInNew, contentDescription = null, tint = Primary, modifier = Modifier.size(12.dp))
                }
            }

            // PnL & Details Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Unrealized PnL", fontFamily = JetBrainsMono, fontSize = 9.sp, color = Outline)
                    Text(
                        text = if (pos.unrealizedPnl >= 0) "+$${pos.unrealizedPnl}" else "-$${-pos.unrealizedPnl}",
                        fontFamily = JetBrainsMono,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (pos.unrealizedPnl >= 0) NeonEmerald else LaserCrimson
                    )
                    Text(
                        text = if (pos.roePercent >= 0) "+${pos.roePercent}% ROE" else "${pos.roePercent}% ROE",
                        fontFamily = JetBrainsMono,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (pos.roePercent >= 0) NeonEmerald else LaserCrimson
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Size / Margin", fontFamily = JetBrainsMono, fontSize = 9.sp, color = Outline)
                    Text("${pos.sizeBtc} BTC", fontFamily = JetBrainsMono, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = OnSurface)
                    Text("${pos.marginUsdt.toInt()} USDT (Cross)", fontFamily = JetBrainsMono, fontSize = 10.sp, color = OnSurfaceVariant)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("Est. Liq Price", fontFamily = JetBrainsMono, fontSize = 9.sp, color = Outline)
                    Text("$${pos.estLiqPrice}", fontFamily = JetBrainsMono, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = LaserCrimson)
                    Text("Risk: ${pos.riskPercent}%", fontFamily = JetBrainsMono, fontSize = 10.sp, color = Outline)
                }
            }

            // Action Buttons Row: TP/SL, Reverse, Exit Now
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onTpSlClick,
                    modifier = Modifier.weight(1f).height(38.dp).testTag("pos_tp_sl_btn"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainerHigh, contentColor = OnSurface)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.Menu, contentDescription = null, modifier = Modifier.size(13.dp))
                        Text("TP / SL", fontFamily = JetBrainsMono, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                Button(
                    onClick = onReverseClick,
                    modifier = Modifier.weight(1f).height(38.dp).testTag("pos_reverse_btn"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainerHigh, contentColor = OnSurface)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(14.dp))
                        Text("Reverse", fontFamily = JetBrainsMono, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                Button(
                    onClick = onExitClick,
                    modifier = Modifier.weight(1.2f).height(38.dp).testTag("pos_exit_btn"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC71D3A), contentColor = Color.White)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.PowerSettingsNew, contentDescription = null, modifier = Modifier.size(14.dp))
                        Text("Exit Now", fontFamily = JetBrainsMono, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun TpSlConfigSheet(
    currentTp: Double,
    currentSl: Double,
    currentTrail: Double,
    onSave: (Double, Double, Double) -> Unit,
    onCancel: () -> Unit
) {
    var tp by remember { mutableStateOf(currentTp.toString()) }
    var sl by remember { mutableStateOf(currentSl.toString()) }
    var trail by remember { mutableStateOf(currentTrail.toString()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Modify TP / SL & Trailing Stop", fontFamily = SpaceGrotesk, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = OnSurface)

        OutlinedTextField(
            value = tp,
            onValueChange = { tp = it },
            label = { Text("Take Profit (USD)") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonEmerald,
                focusedLabelColor = NeonEmerald
            )
        )

        OutlinedTextField(
            value = sl,
            onValueChange = { sl = it },
            label = { Text("Stop Loss (USD)") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = LaserCrimson,
                focusedLabelColor = LaserCrimson
            )
        )

        OutlinedTextField(
            value = trail,
            onValueChange = { trail = it },
            label = { Text("Dynamic Trailing Stop (USD)") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AlertAmber,
                focusedLabelColor = AlertAmber
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onCancel,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainerLowest)
            ) {
                Text("Cancel", color = OnSurfaceVariant)
            }
            Button(
                onClick = {
                    val tpVal = tp.toDoubleOrNull() ?: currentTp
                    val slVal = sl.toDoubleOrNull() ?: currentSl
                    val trVal = trail.toDoubleOrNull() ?: currentTrail
                    onSave(tpVal, slVal, trVal)
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainer, contentColor = Color(0xFF00363D))
            ) {
                Text("Apply Orders", fontWeight = FontWeight.Bold)
            }
        }
    }
}
