package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeployBotSheet(
    repository: MarketRepository,
    initialPair: String = "BTC/USDT",
    onDismiss: () -> Unit,
    onSuccess: (String) -> Unit
) {
    var selectedPair by remember { mutableStateOf(initialPair.replace("USDT.P", "/USDT").replace("USDT", "/USDT")) }
    var selectedStrategy by remember { mutableStateOf("Supertrend + EMA Pullback v2") }
    var isLong by remember { mutableStateOf(true) }
    var leverage by remember { mutableFloatStateOf(10f) }
    var sizePercent by remember { mutableFloatStateOf(15f) }
    var stopLossPercent by remember { mutableFloatStateOf(1.5f) }
    var trailingStopPercent by remember { mutableFloatStateOf(0.8f) }
    var selectedTf by remember { mutableStateOf("15m") }
    var isLiveExchange by remember { mutableStateOf(false) }

    var showPairMenu by remember { mutableStateOf(false) }
    var showStrategyMenu by remember { mutableStateOf(false) }

    val pairs = listOf("BTC/USDT", "ETH/USDT", "SOL/USDT", "NEAR/USDT", "AVAX/USDT", "SUI/USDT", "DOGE/USDT")
    val strategies = listOf(
        "Supertrend + EMA Pullback v2",
        "EMA Golden Cross + ATR Filter",
        "Bollinger Band Mean Reversion",
        "Keltner Channel Squeeze",
        "MACD Zero-Lag Histogram",
        "RSI Divergence Confluence"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = SurfaceContainerLowest,
        dragHandle = {
            BottomSheetDefaults.DragHandle(color = Outline)
        },
        modifier = Modifier.testTag("deploy_bot_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PrimaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SmartToy,
                            contentDescription = "Bot",
                            tint = Primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Deploy Quantitative Bot",
                            color = OnSurface,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Automated sub-50ms execution engine",
                            color = OnSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp).testTag("close_deploy_bot")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = OnSurfaceVariant
                    )
                }
            }

            HorizontalDivider(color = OutlineVariant.copy(alpha = 0.5f))

            // Direction Selection (Long vs Short)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Surface)
                    .border(1.dp, OutlineVariant, RoundedCornerShape(10.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isLong) Primary.copy(alpha = 0.25f) else Color.Transparent)
                        .clickable { isLong = true }
                        .testTag("direction_long"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = if (isLong) Primary else OnSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "LONG (Bullish)",
                            color = if (isLong) Primary else OnSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (!isLong) Secondary.copy(alpha = 0.25f) else Color.Transparent)
                        .clickable { isLong = false }
                        .testTag("direction_short"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingDown,
                            contentDescription = null,
                            tint = if (!isLong) Secondary else OnSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "SHORT (Bearish)",
                            color = if (!isLong) Secondary else OnSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // Asset Pair Selection
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "TARGET PAIR",
                    color = OnSurfaceVariant,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Box {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Surface)
                            .border(1.dp, OutlineVariant, RoundedCornerShape(10.dp))
                            .clickable { showPairMenu = true }
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                            .testTag("select_pair_button"),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CurrencyBitcoin,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = selectedPair,
                                color = OnSurface,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = OnSurfaceVariant
                        )
                    }

                    DropdownMenu(
                        expanded = showPairMenu,
                        onDismissRequest = { showPairMenu = false },
                        modifier = Modifier.background(SurfaceContainerLow)
                    ) {
                        pairs.forEach { pair ->
                            DropdownMenuItem(
                                text = { Text(pair, color = OnSurface) },
                                onClick = {
                                    selectedPair = pair
                                    showPairMenu = false
                                }
                            )
                        }
                    }
                }
            }

            // Strategy Preset Selection
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "ALGORITHMIC MODEL",
                    color = OnSurfaceVariant,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Box {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Surface)
                            .border(1.dp, OutlineVariant, RoundedCornerShape(10.dp))
                            .clickable { showStrategyMenu = true }
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                            .testTag("select_strategy_button"),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = Tertiary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = selectedStrategy,
                                color = OnSurface,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = OnSurfaceVariant
                        )
                    }

                    DropdownMenu(
                        expanded = showStrategyMenu,
                        onDismissRequest = { showStrategyMenu = false },
                        modifier = Modifier.background(SurfaceContainerLow)
                    ) {
                        strategies.forEach { strat ->
                            DropdownMenuItem(
                                text = { Text(strat, color = OnSurface) },
                                onClick = {
                                    selectedStrategy = strat
                                    showStrategyMenu = false
                                }
                            )
                        }
                    }
                }
            }

            // Timeframe selection
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "SIGNAL TIMEFRAME",
                    color = OnSurfaceVariant,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("1m", "5m", "15m", "1h", "4h").forEach { tf ->
                        val isSelected = selectedTf == tf
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) Primary.copy(alpha = 0.2f) else Surface)
                                .border(
                                    1.dp,
                                    if (isSelected) Primary else OutlineVariant,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedTf = tf }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tf,
                                color = if (isSelected) Primary else OnSurfaceVariant,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Sliders: Leverage Factor
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "LEVERAGE FACTOR",
                        color = OnSurfaceVariant,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${leverage.toInt()}x Cross",
                        color = Primary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Slider(
                    value = leverage,
                    onValueChange = { leverage = it },
                    valueRange = 1f..50f,
                    steps = 48,
                    colors = SliderDefaults.colors(
                        thumbColor = Primary,
                        activeTrackColor = Primary,
                        inactiveTrackColor = SurfaceBright
                    ),
                    modifier = Modifier.testTag("leverage_slider")
                )
            }

            // Sliders: Margin Allocation
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "PORTFOLIO MARGIN ALLOCATION",
                        color = OnSurfaceVariant,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${sizePercent.toInt()}% (~$${(10000 * sizePercent / 100).toInt()} USDT)",
                        color = OnSurface,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Slider(
                    value = sizePercent,
                    onValueChange = { sizePercent = it },
                    valueRange = 5f..50f,
                    steps = 9,
                    colors = SliderDefaults.colors(
                        thumbColor = Primary,
                        activeTrackColor = Primary,
                        inactiveTrackColor = SurfaceBright
                    ),
                    modifier = Modifier.testTag("size_slider")
                )
            }

            // Stop Loss & Trailing Stop Sliders
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("STOP LOSS", color = OnSurfaceVariant, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text("${String.format("%.1f", stopLossPercent)}%", color = Secondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = stopLossPercent,
                        onValueChange = { stopLossPercent = it },
                        valueRange = 0.5f..5.0f,
                        colors = SliderDefaults.colors(
                            thumbColor = Secondary,
                            activeTrackColor = Secondary,
                            inactiveTrackColor = SurfaceBright
                        )
                    )
                }

                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("TRAIL STOP", color = OnSurfaceVariant, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text("${String.format("%.1f", trailingStopPercent)}%", color = Primary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = trailingStopPercent,
                        onValueChange = { trailingStopPercent = it },
                        valueRange = 0.2f..3.0f,
                        colors = SliderDefaults.colors(
                            thumbColor = Primary,
                            activeTrackColor = Primary,
                            inactiveTrackColor = SurfaceBright
                        )
                    )
                }
            }

            // Environment selection
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceContainerLow)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (isLiveExchange) Icons.Default.FlashOn else Icons.Default.Shield,
                        contentDescription = null,
                        tint = if (isLiveExchange) Secondary else Primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Column {
                        Text(
                            text = if (isLiveExchange) "Live Production Exchange" else "Demo Sandbox Vault (10,000 USDT)",
                            color = OnSurface,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (isLiveExchange) "Real funds with API keys" else "Simulated execution, zero risk",
                            color = OnSurfaceVariant,
                            fontSize = 10.sp
                        )
                    }
                }
                Switch(
                    checked = isLiveExchange,
                    onCheckedChange = { isLiveExchange = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Secondary,
                        checkedTrackColor = Secondary.copy(alpha = 0.3f),
                        uncheckedThumbColor = Primary,
                        uncheckedTrackColor = Primary.copy(alpha = 0.3f)
                    )
                )
            }

            // Action Button
            Button(
                onClick = {
                    val sizeUsd = 10000.0 * (sizePercent / 100.0)
                    val basePrice = when {
                        selectedPair.contains("BTC") -> 67420.0
                        selectedPair.contains("ETH") -> 3480.0
                        selectedPair.contains("SOL") -> 148.50
                        selectedPair.contains("NEAR") -> 6.42
                        selectedPair.contains("AVAX") -> 28.90
                        else -> 100.0
                    }
                    val targetPrice = if (isLong) basePrice * 1.045 else basePrice * 0.955
                    val newBot = TradeBot(
                        id = "bot_${UUID.randomUUID().toString().take(6)}",
                        pair = selectedPair,
                        strategyName = selectedStrategy,
                        leverage = leverage.toInt(),
                        isLong = isLong,
                        isActive = true,
                        isScanning = true,
                        timeframe = selectedTf,
                        sizePercent = sizePercent.toInt(),
                        sizeUsd = sizeUsd,
                        stopLossPercent = stopLossPercent.toDouble(),
                        trailingStopPercent = trailingStopPercent.toDouble(),
                        entryPrice = basePrice,
                        markPrice = basePrice,
                        targetPrice = targetPrice,
                        currentPnlUsd = 0.0,
                        currentPnlPercent = 0.0,
                        progressPercent = 0.05f,
                        statusMessage = "INITIALIZING"
                    )
                    repository.deployBot(newBot)
                    onSuccess("Deployed ${if (isLong) "LONG" else "SHORT"} bot for $selectedPair ($selectedStrategy) at ${leverage.toInt()}x leverage")
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("confirm_deploy_bot_btn"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.RocketLaunch,
                        contentDescription = null,
                        tint = SurfaceContainerLowest,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "DEPLOY QUANT ENGINE NOW",
                        color = SurfaceContainerLowest,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
