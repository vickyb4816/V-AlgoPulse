package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import com.example.model.RadarCoin
import com.example.ui.components.SparklineChart
import com.example.ui.theme.*

@Composable
fun ScreenerScreen(
    repository: MarketRepository,
    onOpenDeployBot: (String) -> Unit,
    onShowToast: (String) -> Unit
) {
    var selectedScope by remember { mutableStateOf("Top 100") }
    var isFilterDrawerOpen by remember { mutableStateOf(false) }
    var isScanning by remember { mutableStateOf(false) }

    val radarCoins by repository.radarCoins.collectAsState()
    val allChecked = radarCoins.all { it.isChecked }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
            .padding(horizontal = 12.dp)
            .testTag("screener_screen_content"),
        contentPadding = PaddingValues(top = 8.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 1. Perpetuals Radar Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.Radar, contentDescription = null, tint = PrimaryContainer, modifier = Modifier.size(20.dp))
                    Text("Perpetuals Radar", fontFamily = SpaceGrotesk, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                }

                Box(
                    modifier = Modifier
                        .background(SurfaceContainerHigh, RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(SecondaryContainer))
                        Text("LIVE TICK", fontFamily = JetBrainsMono, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SecondaryFixed)
                    }
                }
            }
        }

        // 2. Horizon Scope Selector
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceContainerLow, RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf("Top 20", "Top 50", "Top 100", "Top 120").forEach { scope ->
                        val isSelected = scope == selectedScope
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) SurfaceContainerHighest else Color.Transparent)
                                .clickable {
                                    selectedScope = scope
                                    onShowToast("Radar scanning ${scope.uppercase()}")
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = scope,
                                fontFamily = JetBrainsMono,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) PrimaryContainer else OnSurfaceVariant
                            )
                        }
                    }
                }

                Text(
                    text = "USDT VOL",
                    fontFamily = JetBrainsMono,
                    fontSize = 10.sp,
                    color = Outline,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }

        // 3. Scan Telemetry Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(SurfaceContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.QueryStats, contentDescription = null, tint = PrimaryContainer, modifier = Modifier.size(16.dp))
                        }

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = if (selectedScope == "Top 20") "Scanned 20 Perps" else "Scanned 100 Perps",
                                    fontFamily = JetBrainsMono,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OnSurface
                                )
                                Box(modifier = Modifier.size(3.dp).clip(CircleShape).background(OutlineVariant))
                                Text("14 Signals Found", fontFamily = JetBrainsMono, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SecondaryContainer)
                            }
                            Text("Delta update: 1.4s latency • WebSocket OK", fontFamily = FontFamily.Default, fontSize = 10.sp, color = OnSurfaceVariant)
                        }
                    }

                    Button(
                        onClick = {
                            isScanning = true
                            onShowToast("Re-scanning perpetual order books...")
                        },
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainer, contentColor = Primary)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.Autorenew, contentDescription = null, modifier = Modifier.size(14.dp))
                            Text("Scan", fontFamily = JetBrainsMono, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // 4. Active Filters Bar
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("ACTIVE FILTERS", fontFamily = JetBrainsMono, fontSize = 10.sp, color = OnSurfaceVariant)
                    Row(
                        modifier = Modifier.clickable { isFilterDrawerOpen = !isFilterDrawerOpen },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = null, tint = PrimaryContainer, modifier = Modifier.size(13.dp))
                        Text("Adjust Screener Filters", fontFamily = JetBrainsMono, fontSize = 10.sp, color = PrimaryContainer)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChipTag("Vol > 2x 24h", Primary)
                    FilterChipTag("24h Chg > +3%", SecondaryContainer)
                    FilterChipTag("RSI (30-70)", OnSurface)
                    FilterChipTag("ADX > 25", Primary)
                    FilterChipTag("BB Squeeze", OnSurfaceVariant)
                    FilterChipTag("EMA200 Above", OnSurfaceVariant)
                }
            }
        }

        // 5. Expandable Filter Drawer
        item {
            AnimatedVisibility(visible = isFilterDrawerOpen) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainerHigh)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Signal Presets & Thresholds", fontFamily = SpaceGrotesk, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                            IconButton(onClick = { isFilterDrawerOpen = false }, modifier = Modifier.size(20.dp)) {
                                Icon(Icons.Default.Close, contentDescription = null, tint = OnSurfaceVariant)
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(modifier = Modifier.weight(1f).background(SurfaceContainerLowest, RoundedCornerShape(6.dp)).padding(8.dp)) {
                                Column {
                                    Text("RSI Range", fontFamily = JetBrainsMono, fontSize = 9.sp, color = OnSurfaceVariant)
                                    Text("30 - 75", fontFamily = JetBrainsMono, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                                }
                            }
                            Box(modifier = Modifier.weight(1f).background(SurfaceContainerLowest, RoundedCornerShape(6.dp)).padding(8.dp)) {
                                Column {
                                    Text("Volume Anomaly", fontFamily = JetBrainsMono, fontSize = 9.sp, color = OnSurfaceVariant)
                                    Text("> 200% avg", fontFamily = JetBrainsMono, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SecondaryContainer)
                                }
                            }
                        }

                        Button(
                            onClick = {
                                isFilterDrawerOpen = false
                                onShowToast("Applied screener rule filters")
                            },
                            modifier = Modifier.fillMaxWidth().height(36.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = OnPrimary)
                        ) {
                            Text("Apply Screener Rules", fontFamily = SpaceGrotesk, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 6. Radar Table Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Checkbox(
                        checked = allChecked,
                        onCheckedChange = { repository.setAllCoinCheck(it) },
                        colors = CheckboxDefaults.colors(checkedColor = PrimaryContainer, uncheckedColor = Outline),
                        modifier = Modifier.size(18.dp)
                    )
                    Text("PERPETUAL CONTRACT", fontFamily = JetBrainsMono, fontSize = 9.sp, color = OnSurfaceVariant)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("METRICS", fontFamily = JetBrainsMono, fontSize = 9.sp, color = OnSurfaceVariant)
                    Text("SIGNALS", fontFamily = JetBrainsMono, fontSize = 9.sp, color = OnSurfaceVariant)
                }
            }
        }

        // 7. Coin Cards
        items(radarCoins) { coin ->
            RadarCoinCard(
                coin = coin,
                onToggleCheck = { repository.toggleCoinCheck(coin.symbol) },
                onAction = {
                    if (coin.actionType == "Launch Bot") {
                        onOpenDeployBot(coin.symbol)
                    } else {
                        onShowToast("Initiated ${coin.actionType} on ${coin.symbol}")
                    }
                }
            )
        }

        // 8. Sticky Action Buttons at bottom
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onShowToast("Selected perpetuals loaded into Backtester engine") },
                    modifier = Modifier.weight(1f).height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainerHigh, contentColor = Primary)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.Biotech, contentDescription = null, modifier = Modifier.size(16.dp))
                        Text("Backtest (${radarCoins.count { it.isChecked }})", fontFamily = SpaceGrotesk, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Button(
                    onClick = { onShowToast("Targeting SOLUSDT (Confidence: 94.2%)") },
                    modifier = Modifier.weight(1f).height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SecondaryContainer, contentColor = Color(0xFF00391E))
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.RocketLaunch, contentDescription = null, modifier = Modifier.size(16.dp))
                        Text("Trade Top Pick", fontFamily = SpaceGrotesk, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterChipTag(text: String, color: Color) {
    Row(
        modifier = Modifier
            .background(SurfaceContainerHigh, RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(text, fontFamily = JetBrainsMono, fontSize = 10.sp, color = color)
        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(12.dp))
    }
}

@Composable
private fun RadarCoinCard(
    coin: RadarCoin,
    onToggleCheck: () -> Unit,
    onAction: () -> Unit
) {
    val isBullish = coin.change24h >= 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Checkbox(
                        checked = coin.isChecked,
                        onCheckedChange = { onToggleCheck() },
                        colors = CheckboxDefaults.colors(checkedColor = PrimaryContainer, uncheckedColor = Outline),
                        modifier = Modifier.size(18.dp)
                    )
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(SurfaceContainerHighest),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(coin.namePrefix, fontFamily = SpaceGrotesk, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (isBullish) SecondaryContainer else LaserCrimson)
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(coin.symbol, fontFamily = JetBrainsMono, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                            Box(
                                modifier = Modifier
                                    .background(SurfaceContainerHigh, RoundedCornerShape(3.dp))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text("${coin.leverage}x", fontFamily = JetBrainsMono, fontSize = 9.sp, color = Primary)
                            }
                        }
                        Text("Vol 24h: ${coin.vol24h}", fontFamily = JetBrainsMono, fontSize = 9.sp, color = OnSurfaceVariant)
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("$${coin.price}", fontFamily = JetBrainsMono, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        Icon(
                            imageVector = if (isBullish) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = if (isBullish) SecondaryContainer else LaserCrimson,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = if (isBullish) "+${coin.change24h}%" else "${coin.change24h}%",
                            fontFamily = JetBrainsMono,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isBullish) SecondaryContainer else LaserCrimson
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceContainerLowest, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SparklineChart(
                    points = coin.sparklinePoints,
                    lineColor = if (isBullish) SecondaryContainer else LaserCrimson,
                    modifier = Modifier.width(90.dp).height(20.dp)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text("RSI", fontFamily = JetBrainsMono, fontSize = 9.sp, color = Outline)
                        Text("${coin.rsi}", fontFamily = JetBrainsMono, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (coin.rsi > 70) TertiaryFixedDim else if (coin.rsi < 30) LaserCrimson else OnSurface)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(coin.volOrAdxLabel, fontFamily = JetBrainsMono, fontSize = 9.sp, color = Outline)
                        Text(coin.volOrAdxValue, fontFamily = JetBrainsMono, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SecondaryContainer)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(
                        modifier = Modifier
                            .background(
                                if (coin.primarySignal.contains("BUY") || coin.primarySignal.contains("MOMENTUM")) SecondaryContainer else ErrorContainer,
                                RoundedCornerShape(10.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = coin.primarySignal,
                            fontFamily = JetBrainsMono,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (coin.primarySignal.contains("BUY") || coin.primarySignal.contains("MOMENTUM")) OnSecondaryContainer else OnError
                        )
                    }
                    Box(
                        modifier = Modifier
                            .background(SurfaceContainerHighest, RoundedCornerShape(10.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(coin.secondarySignal, fontFamily = JetBrainsMono, fontSize = 8.sp, color = Primary)
                    }
                }

                Button(
                    onClick = onAction,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                    modifier = Modifier.height(28.dp),
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (coin.actionType == "Launch Bot") PrimaryContainer else SurfaceContainerHigh,
                        contentColor = if (coin.actionType == "Launch Bot") Color(0xFF00363D) else Primary
                    )
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        Icon(
                            imageVector = when (coin.actionType) {
                                "Launch Bot" -> Icons.Default.SmartToy
                                "Trade" -> Icons.Default.Bolt
                                else -> Icons.Default.Insights
                            },
                            contentDescription = null,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(coin.actionType, fontFamily = JetBrainsMono, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
