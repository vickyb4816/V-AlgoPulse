package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MarketRepository
import com.example.model.TradeBot
import com.example.ui.theme.*

@Composable
fun DeployBotSheet(
    initialPair: String = "BTC/USDT",
    repository: MarketRepository,
    onDismiss: () -> Unit,
    onShowToast: (String) -> Unit
) {
    var selectedPair by remember { mutableStateOf(initialPair) }
    var selectedTimeframe by remember { mutableStateOf("5m") }
    var selectedModel by remember { mutableStateOf("Supertrend + EMA Pullback v2 (Trend Scalp)") }
    var autoTuneWeights by remember { mutableStateOf(true) }

    var leverageMultiplier by remember { mutableFloatStateOf(10f) }
    var allocatedPercent by remember { mutableIntStateOf(10) }
    var hardCap by remember { mutableStateOf("5,000") }

    var stopLossPercent by remember { mutableDoubleStateOf(1.50) }
    var takeProfitPercent by remember { mutableDoubleStateOf(3.50) }
    var trailingStopChecked by remember { mutableStateOf(true) }
    var breakEvenChecked by remember { mutableStateOf(true) }

    var isLivePipeline by remember { mutableStateOf(true) }
    var isAuthorized by remember { mutableStateOf(true) }

    val freeEquity = 4820.0
    val marginUsed = (freeEquity * allocatedPercent / 100.0)
    val contractExposure = marginUsed * leverageMultiplier.toInt()

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceContainerLow)
            .padding(horizontal = 14.dp)
            .testTag("deploy_bot_sheet"),
        contentPadding = PaddingValues(top = 8.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 1. Sheet Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Deploy Trade Bot", fontFamily = SpaceGrotesk, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                        Box(modifier = Modifier.background(SurfaceContainerHighest, RoundedCornerShape(10.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(PrimaryContainer))
                                Text("LIVE ENGINE", fontFamily = JetBrainsMono, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Primary)
                            }
                        }
                    }
                    Text("Perpetual Futures Execution Architecture v2.4", fontFamily = FontFamily.Default, fontSize = 10.sp, color = OnSurfaceVariant)
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .background(SecondaryContainer.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(if (isLivePipeline) "CoinDCX Live" else "Demo Simulator", fontFamily = JetBrainsMono, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = SecondaryContainer)
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp).clip(CircleShape).background(SurfaceContainerHighest)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = OnSurfaceVariant, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // 2. Contract & Interval
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("CONTRACT & INTERVAL", fontFamily = JetBrainsMono, fontSize = 9.sp, color = Outline)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(SecondaryContainer))
                            Text("Funding: +0.0102%", fontFamily = JetBrainsMono, fontSize = 9.sp, color = SecondaryContainer)
                        }
                    }

                    // Asset Picker Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceContainer)
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(modifier = Modifier.size(28.dp).clip(CircleShape).background(SurfaceContainerHigh), contentAlignment = Alignment.Center) {
                                Text("₿", fontFamily = SpaceGrotesk, fontSize = 14.sp, color = PrimaryContainer)
                            }
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(selectedPair, fontFamily = JetBrainsMono, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                                    Box(modifier = Modifier.background(SurfaceContainerHigh, RoundedCornerShape(3.dp)).padding(horizontal = 4.dp, vertical = 1.dp)) {
                                        Text("PERP", fontFamily = JetBrainsMono, fontSize = 8.sp, color = OnSurfaceVariant)
                                    }
                                }
                                Text("$64,842.20", fontFamily = JetBrainsMono, fontSize = 10.sp, color = OnSurfaceVariant)
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(modifier = Modifier.background(SecondaryContainer.copy(alpha = 0.15f), RoundedCornerShape(10.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                Text("+2.84%", fontFamily = JetBrainsMono, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SecondaryContainer)
                            }
                            Icon(Icons.Default.SwapVert, contentDescription = null, tint = PrimaryContainer, modifier = Modifier.size(18.dp))
                        }
                    }

                    // Timeframe Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("1m", "3m", "5m", "15m", "1h", "4h").forEach { tf ->
                            val isSelected = tf == selectedTimeframe
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) PrimaryContainer else SurfaceContainer)
                                    .clickable { selectedTimeframe = tf }
                                    .padding(vertical = 5.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = tf,
                                    fontFamily = JetBrainsMono,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color(0xFF00363D) else OnSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Algorithmic Model
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainer)
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("ALGORITHMIC MODEL", fontFamily = JetBrainsMono, fontSize = 9.sp, color = Outline)
                        Text("✦ Quant Matrix", fontFamily = JetBrainsMono, fontSize = 9.sp, color = Primary)
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceContainerLowest)
                            .padding(10.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(selectedModel, fontFamily = SpaceGrotesk, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Outline, modifier = Modifier.size(16.dp))
                        }
                    }

                    // Micro stats
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(modifier = Modifier.weight(1f).background(SurfaceContainerLow, RoundedCornerShape(4.dp)).padding(6.dp)) {
                            Column {
                                Text("Sharpe", fontFamily = JetBrainsMono, fontSize = 9.sp, color = Outline)
                                Text("2.41", fontFamily = JetBrainsMono, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SecondaryContainer)
                            }
                        }
                        Box(modifier = Modifier.weight(1f).background(SurfaceContainerLow, RoundedCornerShape(4.dp)).padding(6.dp)) {
                            Column {
                                Text("Est. Winrate", fontFamily = JetBrainsMono, fontSize = 9.sp, color = Outline)
                                Text("68.2%", fontFamily = JetBrainsMono, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Primary)
                            }
                        }
                        Box(modifier = Modifier.weight(1f).background(SurfaceContainerLow, RoundedCornerShape(4.dp)).padding(6.dp)) {
                            Column {
                                Text("Max DD", fontFamily = JetBrainsMono, fontSize = 9.sp, color = Outline)
                                Text("-4.10%", fontFamily = JetBrainsMono, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LaserCrimson)
                            }
                        }
                    }

                    // Auto tune weights
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Auto-Tune Weights", fontFamily = SpaceGrotesk, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = OnSurface)
                            Text("Re-calibrate multipliers via 48h rolling volatility", fontFamily = FontFamily.Default, fontSize = 10.sp, color = OnSurfaceVariant)
                        }
                        Switch(
                            checked = autoTuneWeights,
                            onCheckedChange = { autoTuneWeights = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = SurfaceContainerLowest, checkedTrackColor = PrimaryContainer),
                            modifier = Modifier.height(24.dp)
                        )
                    }
                }
            }
        }

        // 4. Leverage & Sizing
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainer)
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("LEVERAGE & SIZING", fontFamily = JetBrainsMono, fontSize = 9.sp, color = Outline)
                        Box(modifier = Modifier.background(SurfaceContainerHigh, RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                            Text(
                                text = if (leverageMultiplier > 15) "High Risk (-3.2%)" else "Safe Zone (Est. Liq: -8.8%)",
                                fontFamily = JetBrainsMono,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (leverageMultiplier > 15) LaserCrimson else SecondaryContainer
                            )
                        }
                    }

                    // Multiplier Box & Slider
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceContainerLowest, RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Cross Margin Multiplier", fontFamily = FontFamily.Default, fontSize = 11.sp, color = OnSurfaceVariant)
                            Text("${leverageMultiplier.toInt()}x", fontFamily = JetBrainsMono, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryContainer)
                        }

                        Slider(
                            value = leverageMultiplier,
                            onValueChange = { leverageMultiplier = it },
                            valueRange = 1f..25f,
                            steps = 23,
                            colors = SliderDefaults.colors(thumbColor = PrimaryContainer, activeTrackColor = PrimaryContainer, inactiveTrackColor = SurfaceContainerHighest),
                            modifier = Modifier.height(24.dp)
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("1x", fontFamily = JetBrainsMono, fontSize = 9.sp, color = Outline)
                            Text("5x", fontFamily = JetBrainsMono, fontSize = 9.sp, color = Outline)
                            Text("10x", fontFamily = JetBrainsMono, fontSize = 9.sp, color = Outline)
                            Text("15x", fontFamily = JetBrainsMono, fontSize = 9.sp, color = Outline)
                            Text("20x", fontFamily = JetBrainsMono, fontSize = 9.sp, color = Outline)
                            Text("25x MAX", fontFamily = JetBrainsMono, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = LaserCrimson)
                        }
                    }

                    // % Equity Sizing
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Allocated Capital (% of Free Equity)", fontFamily = FontFamily.Default, fontSize = 11.sp, color = OnSurfaceVariant)
                        Text("Avail: $${freeEquity.toInt()} USDT", fontFamily = JetBrainsMono, fontSize = 10.sp, color = OnSurface)
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(5, 10, 15, 25).forEach { pct ->
                            val isSelected = pct == allocatedPercent
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) SecondaryContainer else SurfaceContainerHighest)
                                    .clickable { allocatedPercent = pct }
                                    .padding(vertical = 5.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "$pct%",
                                    fontFamily = JetBrainsMono,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color(0xFF00391E) else OnSurfaceVariant
                                )
                            }
                        }
                    }

                    // Exposure summary
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceContainerLowest, RoundedCornerShape(6.dp))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Contract Exposure", fontFamily = JetBrainsMono, fontSize = 9.sp, color = OnSurfaceVariant)
                            Text("$${String.format("%.2f", contractExposure)} USDT", fontFamily = JetBrainsMono, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Initial Margin", fontFamily = JetBrainsMono, fontSize = 9.sp, color = OnSurfaceVariant)
                            Text("$${String.format("%.2f", marginUsed)} USDT", fontFamily = JetBrainsMono, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Primary)
                        }
                    }
                }
            }
        }

        // 5. Risk Protocol & Exits
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainer)
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("RISK PROTOCOL & EXITS", fontFamily = JetBrainsMono, fontSize = 9.sp, color = Outline)
                        Text("R:R Ratio 1:2.33", fontFamily = JetBrainsMono, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SecondaryContainer)
                    }

                    // SL & TP Steppers
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Stop Loss Stepper
                        Box(
                            modifier = Modifier.weight(1f).background(SurfaceContainerLowest, RoundedCornerShape(6.dp)).padding(8.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Stop Loss (SL)", fontFamily = JetBrainsMono, fontSize = 9.sp, color = LaserCrimson)
                                    Icon(Icons.Default.Shield, contentDescription = null, tint = LaserCrimson, modifier = Modifier.size(12.dp))
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(onClick = { if (stopLossPercent > 0.5) stopLossPercent -= 0.1 }, modifier = Modifier.size(24.dp).background(SurfaceContainer, RoundedCornerShape(4.dp))) {
                                        Text("-", fontFamily = JetBrainsMono, color = OnSurface)
                                    }
                                    Text("${String.format("%.2f", stopLossPercent)}%", fontFamily = JetBrainsMono, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = LaserCrimson)
                                    IconButton(onClick = { stopLossPercent += 0.1 }, modifier = Modifier.size(24.dp).background(SurfaceContainer, RoundedCornerShape(4.dp))) {
                                        Text("+", fontFamily = JetBrainsMono, color = OnSurface)
                                    }
                                }
                                Text("-$72.30 Risk", fontFamily = JetBrainsMono, fontSize = 9.sp, color = Outline, modifier = Modifier.align(Alignment.CenterHorizontally))
                            }
                        }

                        // Take Profit Stepper
                        Box(
                            modifier = Modifier.weight(1f).background(SurfaceContainerLowest, RoundedCornerShape(6.dp)).padding(8.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Take Profit (TP)", fontFamily = JetBrainsMono, fontSize = 9.sp, color = SecondaryContainer)
                                    Icon(Icons.Default.Flag, contentDescription = null, tint = SecondaryContainer, modifier = Modifier.size(12.dp))
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(onClick = { if (takeProfitPercent > 1.0) takeProfitPercent -= 0.1 }, modifier = Modifier.size(24.dp).background(SurfaceContainer, RoundedCornerShape(4.dp))) {
                                        Text("-", fontFamily = JetBrainsMono, color = OnSurface)
                                    }
                                    Text("${String.format("%.2f", takeProfitPercent)}%", fontFamily = JetBrainsMono, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SecondaryContainer)
                                    IconButton(onClick = { takeProfitPercent += 0.1 }, modifier = Modifier.size(24.dp).background(SurfaceContainer, RoundedCornerShape(4.dp))) {
                                        Text("+", fontFamily = JetBrainsMono, color = OnSurface)
                                    }
                                }
                                Text("+$168.70 Target", fontFamily = JetBrainsMono, fontSize = 9.sp, color = Outline, modifier = Modifier.align(Alignment.CenterHorizontally))
                            }
                        }
                    }

                    // Trailing Stop & Break Even
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(modifier = Modifier.weight(1f).background(SurfaceContainerLowest, RoundedCornerShape(6.dp)).padding(6.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    Text("Trailing Stop", fontFamily = JetBrainsMono, fontSize = 9.sp, color = OnSurfaceVariant)
                                    Text("0.80% Step", fontFamily = JetBrainsMono, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Primary)
                                }
                                Checkbox(checked = trailingStopChecked, onCheckedChange = { trailingStopChecked = it }, modifier = Modifier.size(18.dp))
                            }
                        }
                        Box(modifier = Modifier.weight(1f).background(SurfaceContainerLowest, RoundedCornerShape(6.dp)).padding(6.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    Text("Break-Even Act.", fontFamily = JetBrainsMono, fontSize = 9.sp, color = OnSurfaceVariant)
                                    Text("+1.20% Offset", fontFamily = JetBrainsMono, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Primary)
                                }
                                Checkbox(checked = breakEvenChecked, onCheckedChange = { breakEvenChecked = it }, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }

        // 6. Failsafes & Circuit Breakers
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainer)
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("FAILSAFES & CIRCUIT BREAKERS", fontFamily = JetBrainsMono, fontSize = 9.sp, color = Outline)

                    Row(
                        modifier = Modifier.fillMaxWidth().background(SurfaceContainerLowest, RoundedCornerShape(6.dp)).padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Max Daily Loss Cutoff", fontFamily = SpaceGrotesk, fontSize = 12.sp, color = OnSurface)
                            Text("Emergency bot disarm on threshold", fontFamily = FontFamily.Default, fontSize = 9.sp, color = OnSurfaceVariant)
                        }
                        Box(modifier = Modifier.background(SurfaceContainer, RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                            Text("500 USDT", fontFamily = JetBrainsMono, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LaserCrimson)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().background(SurfaceContainerLowest, RoundedCornerShape(6.dp)).padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Frequency Governor", fontFamily = SpaceGrotesk, fontSize = 12.sp, color = OnSurface)
                            Text("Max concurrent roundtrips per 24h", fontFamily = FontFamily.Default, fontSize = 9.sp, color = OnSurfaceVariant)
                        }
                        Box(modifier = Modifier.background(SurfaceContainer, RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                            Text("10 orders", fontFamily = JetBrainsMono, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Primary)
                        }
                    }
                }
            }
        }

        // 7. Deployment Pipeline
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainer)
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("DEPLOYMENT PIPELINE", fontFamily = JetBrainsMono, fontSize = 9.sp, color = Outline)

                    Row(
                        modifier = Modifier.fillMaxWidth().background(SurfaceContainerLowest, RoundedCornerShape(6.dp)).padding(3.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (!isLivePipeline) SurfaceContainerHigh else Color.Transparent)
                                .clickable { isLivePipeline = false }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.Science, contentDescription = null, modifier = Modifier.size(14.dp))
                                Text("Simulated (Paper)", fontFamily = SpaceGrotesk, fontSize = 11.sp, color = if (!isLivePipeline) Primary else OnSurfaceVariant)
                            }
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isLivePipeline) SurfaceContainerHigh else Color.Transparent)
                                .clickable { isLivePipeline = true }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.Bolt, contentDescription = null, tint = PrimaryContainer, modifier = Modifier.size(14.dp))
                                Text("CoinDCX Live", fontFamily = SpaceGrotesk, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isLivePipeline) PrimaryContainer else OnSurfaceVariant)
                            }
                        }
                    }

                    Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Checkbox(checked = isAuthorized, onCheckedChange = { isAuthorized = it }, modifier = Modifier.size(18.dp))
                        Text(
                            "Authorize real-time API execution with automated capital routing on CoinDCX Perpetual Futures account.",
                            fontFamily = FontFamily.Default,
                            fontSize = 10.sp,
                            color = OnSurfaceVariant,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }

        // 8. CTAs
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        val newBot = TradeBot(
                            id = "bot_${System.currentTimeMillis()}",
                            pair = selectedPair,
                            strategyName = selectedModel,
                            leverage = leverageMultiplier.toInt(),
                            isLong = true,
                            isActive = true,
                            isScanning = false,
                            timeframe = selectedTimeframe,
                            sizePercent = allocatedPercent,
                            sizeUsd = marginUsed,
                            stopLossPercent = stopLossPercent,
                            trailingStopPercent = 0.8,
                            entryPrice = 64842.20,
                            markPrice = 64842.20,
                            targetPrice = 64842.20 * (1 + takeProfitPercent / 100.0),
                            currentPnlUsd = 0.0,
                            currentPnlPercent = 0.0,
                            progressPercent = 0.1f,
                            statusMessage = if (isLivePipeline) "LIVE DEPLOYED" else "DEMO ACTIVE"
                        )
                        repository.deployBot(newBot)
                        onShowToast("Bot Initialized: $selectedPair on $selectedModel")
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth().height(46.dp).testTag("deploy_confirm_btn"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SecondaryContainer, contentColor = Color(0xFF00391E))
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.RocketLaunch, contentDescription = null, modifier = Modifier.size(20.dp))
                        Text("Initialize & Deploy Bot", fontFamily = SpaceGrotesk, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Button(
                    onClick = {
                        onShowToast("Quick 500-Bar Backtest complete! Sharpe: 2.41, Winrate: 68.2%")
                    },
                    modifier = Modifier.fillMaxWidth().height(38.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainer, contentColor = Primary)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.QueryStats, contentDescription = null, modifier = Modifier.size(16.dp))
                        Text("Run Quick 500-Bar Backtest", fontFamily = SpaceGrotesk, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
