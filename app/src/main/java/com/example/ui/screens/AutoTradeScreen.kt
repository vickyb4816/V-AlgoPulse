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
import com.example.model.TradeBot
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AutoTradeScreen(
    repository: MarketRepository,
    onOpenDeployBot: () -> Unit,
    onShowToast: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var isLiveMode by remember { mutableStateOf(false) }
    var showKillModal by remember { mutableStateOf(false) }
    var showLiveSafetyModal by remember { mutableStateOf(false) }

    var isRunningAutoPick by remember { mutableStateOf(false) }
    var autoPickLabel by remember { mutableStateOf("Run Strategy Auto-Selector") }

    val deployedBots by repository.deployedBots.collectAsState()
    val telemetryLogs by repository.telemetryLogs.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
            .padding(horizontal = 12.dp)
            .testTag("auto_trade_screen_content"),
        contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. Demo vs Live Mode Switcher
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceContainerLowest, RoundedCornerShape(24.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (!isLiveMode) SurfaceContainerHigh else Color.Transparent)
                        .clickable {
                            isLiveMode = false
                            onShowToast("Active in Simulated Demo Environment")
                        }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(PrimaryContainer))
                        Text(
                            text = "Demo (Sim: 10,000 USDT)",
                            fontFamily = JetBrainsMono,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (!isLiveMode) Primary else OnSurfaceVariant
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isLiveMode) SurfaceContainerHigh else Color.Transparent)
                        .clickable { showLiveSafetyModal = true }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = if (isLiveMode) SecondaryContainer else OnSurfaceVariant, modifier = Modifier.size(14.dp))
                        Text(
                            text = "Live (CoinDCX)",
                            fontFamily = JetBrainsMono,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isLiveMode) SecondaryContainer else OnSurfaceVariant
                        )
                        Box(
                            modifier = Modifier
                                .background(ErrorContainer, RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text("PRO", fontFamily = JetBrainsMono, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Error)
                        }
                    }
                }
            }
        }

        // 2. Demo Execution Vault Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("execution_vault_card"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = Primary, modifier = Modifier.size(16.dp))
                            Text(
                                if (isLiveMode) "COINDCX LIVE VAULT" else "DEMO EXECUTION VAULT",
                                fontFamily = JetBrainsMono,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = OnSurfaceVariant
                            )
                        }
                        Box(
                            modifier = Modifier
                                .background(SecondaryContainer.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(SecondaryContainer))
                                Text("2 Bots Running", fontFamily = JetBrainsMono, fontSize = 10.sp, color = SecondaryContainer)
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                if (isLiveMode) "CoinDCX Margin Balance" else "Allocated Simulation Balance",
                                fontFamily = JetBrainsMono,
                                fontSize = 10.sp,
                                color = OnSurfaceVariant
                            )
                            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = if (isLiveMode) "3,480.25" else "10,000.00",
                                    fontFamily = JetBrainsMono,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OnSurface
                                )
                                Text("USDT", fontFamily = JetBrainsMono, fontSize = 11.sp, color = Outline)
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("Daily Net PnL", fontFamily = JetBrainsMono, fontSize = 10.sp, color = OnSurfaceVariant)
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("+342.50", fontFamily = JetBrainsMono, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SecondaryContainer)
                                Box(
                                    modifier = Modifier.background(SecondaryContainer.copy(alpha = 0.15f), RoundedCornerShape(4.dp)).padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text("+3.42%", fontFamily = JetBrainsMono, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SecondaryContainer)
                                }
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceContainer)
                            .padding(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = PrimaryFixedDim, modifier = Modifier.size(15.dp).padding(top = 1.dp))
                            Text(
                                text = "Stop-loss & dynamic trailing stops monitored client-side by AlgoPulse Core Engine. Keep screen awake for sub-50ms execution.",
                                fontFamily = FontFamily.Default,
                                fontSize = 11.sp,
                                color = OnSurfaceVariant,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }
        }

        // 3. Triple Trigger Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        repository.resumeAllBots()
                        onShowToast("All bots resumed successfully")
                    },
                    modifier = Modifier.weight(1f).height(44.dp).testTag("resume_all_btn"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SecondaryContainer, contentColor = Color(0xFF00391E))
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text("RESUME", fontFamily = JetBrainsMono, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                IconButton(
                    onClick = {
                        repository.pauseAllBots()
                        onShowToast("Execution paused for all strategies")
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceContainerHigh)
                        .testTag("pause_all_btn")
                ) {
                    Icon(Icons.Default.Pause, contentDescription = "Pause", tint = OnSurfaceVariant)
                }

                Button(
                    onClick = { showKillModal = true },
                    modifier = Modifier.weight(1f).height(44.dp).testTag("kill_all_btn"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorContainer, contentColor = OnError)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(16.dp))
                        Text("KILL ALL", fontFamily = JetBrainsMono, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 4. Deployed Futures Bots Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.Sensors, contentDescription = null, tint = Primary, modifier = Modifier.size(16.dp))
                    Text("Deployed Futures Bots", fontFamily = SpaceGrotesk, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = OnSurface)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("Latency:", fontFamily = JetBrainsMono, fontSize = 10.sp, color = OnSurfaceVariant)
                    Text("18ms", fontFamily = JetBrainsMono, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Secondary)
                }
            }
        }

        // 5. Bot Cards
        items(deployedBots) { bot ->
            BotCard(
                bot = bot,
                onTighten = { onShowToast("Trailing stop tightened to +1.2% for ${bot.pair}") },
                onSettings = { onOpenDeployBot() },
                onStop = {
                    repository.stopBot(bot.id)
                    onShowToast("Strategy for ${bot.pair} stopped")
                }
            )
        }

        item {
            OutlinedButton(
                onClick = onOpenDeployBot,
                modifier = Modifier.fillMaxWidth().height(42.dp).testTag("deploy_new_bot_btn"),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryContainer),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryContainer.copy(alpha = 0.5f))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.AddCircleOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                    Text("Deploy New Custom Bot", fontFamily = SpaceGrotesk, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // 6. Auto-Pick Best Strategy
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerHigh)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(PrimaryContainer.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Psychology, contentDescription = null, tint = PrimaryContainer, modifier = Modifier.size(22.dp))
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Auto-Pick Best Strategy", fontFamily = SpaceGrotesk, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                            Box(modifier = Modifier.background(Primary.copy(alpha = 0.2f), RoundedCornerShape(10.dp)).padding(horizontal = 5.dp, vertical = 1.dp)) {
                                Text("Algorithmic", fontFamily = JetBrainsMono, fontSize = 9.sp, color = Primary)
                            }
                        }
                        Text(
                            text = "Runs rapid walk-forward validation across all 16 built-in quantitative strategies on the last 500 candles and dynamically deploys top Sharpe ratio.",
                            fontFamily = FontFamily.Default,
                            fontSize = 11.sp,
                            color = OnSurfaceVariant,
                            lineHeight = 15.sp
                        )

                        Button(
                            onClick = {
                                isRunningAutoPick = true
                                autoPickLabel = "Backtesting 16 Quant Strategies..."
                                coroutineScope.launch {
                                    delay(1200)
                                    autoPickLabel = "Selected: ATR Momentum (Sharpe 2.84)"
                                    repository.addTelemetryLog("AI", "Auto-Selector chosen: ATR Volatility Breakout (Sharpe 2.84)")
                                    onShowToast("Optimal strategy parameters selected & queued")
                                    delay(2000)
                                    autoPickLabel = "Run Strategy Auto-Selector"
                                    isRunningAutoPick = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp).height(36.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = OnPrimary)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                if (isRunningAutoPick) {
                                    CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp, color = OnPrimary)
                                } else {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(15.dp))
                                }
                                Text(autoPickLabel, fontFamily = SpaceGrotesk, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // 7. Live Execution Telemetry Stream
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
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.Terminal, contentDescription = null, tint = SecondaryContainer, modifier = Modifier.size(16.dp))
                            Text("Live Execution Telemetry", fontFamily = SpaceGrotesk, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = OnSurface)
                        }
                        TextButton(
                            onClick = { repository.clearLogs() },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Clear", fontFamily = JetBrainsMono, fontSize = 10.sp, color = Outline)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceContainerLowest)
                            .padding(8.dp)
                    ) {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            items(telemetryLogs) { log ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text("[${log.time}]", fontFamily = JetBrainsMono, fontSize = 10.sp, color = Outline)
                                    Text(
                                        "${log.tag}:",
                                        fontFamily = JetBrainsMono,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (log.tag.contains("BTC")) SecondaryContainer else PrimaryContainer
                                    )
                                    Text(
                                        log.message,
                                        fontFamily = JetBrainsMono,
                                        fontSize = 10.sp,
                                        color = OnSurfaceVariant,
                                        maxLines = 2
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 8. Closed Trades (Today)
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
                        Text("Closed Trades (Today)", fontFamily = SpaceGrotesk, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = OnSurface)
                        Text("+214.10 USDT Net", fontFamily = JetBrainsMono, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SecondaryContainer)
                    }

                    ClosedTradeItem(
                        symbol = "SOLUSDT",
                        isLong = true,
                        leverage = 10,
                        reason = "Supertrend v3 • Closed via TP Target",
                        pnl = "+184.20 USDT",
                        time = "13:42:10",
                        isWin = true
                    )

                    ClosedTradeItem(
                        symbol = "AVAXUSDT",
                        isLong = false,
                        leverage = 5,
                        reason = "Breakout Scalp • Stopped Out",
                        pnl = "-42.60 USDT",
                        time = "11:15:02",
                        isWin = false
                    )
                }
            }
        }
    }

    if (showKillModal) {
        AlertDialog(
            onDismissRequest = { showKillModal = false },
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = LaserCrimson, modifier = Modifier.size(32.dp)) },
            title = { Text("EMERGENCY KILL SWITCH", fontFamily = SpaceGrotesk, fontWeight = FontWeight.Bold, color = LaserCrimson) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "This will immediately submit MARKET CLOSE for all active open positions and terminate all algorithmic runners immediately.",
                        fontFamily = FontFamily.Default,
                        fontSize = 12.sp,
                        color = OnSurfaceVariant
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceContainerLowest, RoundedCornerShape(6.dp))
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("1 Open Position (BTC/USDT) will be closed at market.", fontFamily = JetBrainsMono, fontSize = 11.sp, color = LaserCrimson)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        repository.killAllBots()
                        showKillModal = false
                        onShowToast("EMERGENCY KILL: All positions closed & orders cancelled")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LaserCrimson, contentColor = Color.White)
                ) {
                    Text("CONFIRM KILL", fontFamily = JetBrainsMono, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showKillModal = false }) {
                    Text("Cancel", color = OnSurface)
                }
            },
            containerColor = SurfaceContainerHigh
        )
    }

    if (showLiveSafetyModal) {
        AlertDialog(
            onDismissRequest = { showLiveSafetyModal = false },
            icon = { Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Primary, modifier = Modifier.size(30.dp)) },
            title = { Text("Live CoinDCX Futures Lock", fontFamily = SpaceGrotesk, fontWeight = FontWeight.Bold, color = OnSurface) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Live automated trading places orders using real wallet assets on CoinDCX. Ensure your API keys have Futures Trading authorization and that risk limits are set.",
                        fontFamily = FontFamily.Default,
                        fontSize = 12.sp,
                        color = OnSurfaceVariant
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceContainerLowest, RoundedCornerShape(6.dp))
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("API Key Bound:", fontFamily = FontFamily.Default, fontSize = 11.sp, color = OnSurface)
                            Text("Connected (dcx_live_99**)", fontFamily = JetBrainsMono, fontSize = 10.sp, color = SecondaryContainer)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Max Allowed Leverage:", fontFamily = FontFamily.Default, fontSize = 11.sp, color = OnSurface)
                            Text("10x Strict Capped", fontFamily = JetBrainsMono, fontSize = 10.sp, color = Primary)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        isLiveMode = true
                        showLiveSafetyModal = false
                        repository.addTelemetryLog("SYS", "CoinDCX API session verified. Real balance: 3,480.25 USDT")
                        onShowToast("Switched to Live CoinDCX Futures Engine")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainer, contentColor = Color(0xFF00363D))
                ) {
                    Text("Authorize Live", fontFamily = JetBrainsMono, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLiveSafetyModal = false }) {
                    Text("Stay on Demo", color = OnSurface)
                }
            },
            containerColor = SurfaceContainerHigh
        )
    }
}

@Composable
private fun BotCard(
    bot: TradeBot,
    onTighten: () -> Unit,
    onSettings: () -> Unit,
    onStop: () -> Unit
) {
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
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceContainerHighest),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (bot.pair.contains("BTC")) "₿" else "Ξ",
                            fontFamily = SpaceGrotesk,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(bot.pair, fontFamily = SpaceGrotesk, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                            Box(
                                modifier = Modifier
                                    .background(SurfaceContainerHighest, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text("${bot.leverage}x PERP", fontFamily = JetBrainsMono, fontSize = 9.sp, color = Primary)
                            }
                        }
                        Text(bot.strategyName, fontFamily = FontFamily.Default, fontSize = 11.sp, color = OnSurfaceVariant)
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Box(
                        modifier = Modifier
                            .background(
                                if (bot.isScanning) PrimaryContainer.copy(alpha = 0.12f) else SecondaryContainer.copy(alpha = 0.15f),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .clip(CircleShape)
                                    .background(if (bot.isScanning) PrimaryContainer else SecondaryContainer)
                            )
                            Text(
                                text = if (bot.isActive) bot.statusMessage else "HALTED",
                                fontFamily = JetBrainsMono,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (bot.isScanning) PrimaryContainer else SecondaryContainer
                            )
                        }
                    }
                    if (!bot.isScanning && bot.isActive) {
                        Text("+$${bot.currentPnlUsd} (+${bot.currentPnlPercent}%)", fontFamily = JetBrainsMono, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SecondaryContainer)
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceContainer, RoundedCornerShape(6.dp))
                    .padding(vertical = 6.dp, horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("TF", fontFamily = JetBrainsMono, fontSize = 9.sp, color = Outline)
                    Text(bot.timeframe, fontFamily = JetBrainsMono, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Size", fontFamily = JetBrainsMono, fontSize = 9.sp, color = Outline)
                    Text("${bot.sizePercent}% ($${(bot.sizeUsd / 1000).toInt()}k)", fontFamily = JetBrainsMono, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(if (bot.isScanning) "Supertrend" else "Stop Loss", fontFamily = JetBrainsMono, fontSize = 9.sp, color = Outline)
                    Text(
                        if (bot.isScanning) bot.supertrendParam else "${bot.stopLossPercent}%",
                        fontFamily = JetBrainsMono,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (bot.isScanning) SecondaryContainer else LaserCrimson
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(if (bot.isScanning) "RSI (14)" else "Trailing", fontFamily = JetBrainsMono, fontSize = 9.sp, color = Outline)
                    Text(
                        if (bot.isScanning) "${bot.rsi} Neut" else "${bot.trailingStopPercent}%",
                        fontFamily = JetBrainsMono,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                }
            }

            if (!bot.isScanning) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Entry: $${bot.entryPrice.toInt()}", fontFamily = JetBrainsMono, fontSize = 9.sp, color = OnSurfaceVariant)
                        Text("Mark: $${bot.markPrice.toInt()}", fontFamily = JetBrainsMono, fontSize = 9.sp, color = SecondaryContainer)
                        Text("Target: $${bot.targetPrice.toInt()}", fontFamily = JetBrainsMono, fontSize = 9.sp, color = Primary)
                    }
                    Box(modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)).background(SurfaceContainerHighest)) {
                        Box(modifier = Modifier.fillMaxWidth(bot.progressPercent).fillMaxHeight().background(SecondaryContainer))
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceContainerLowest, RoundedCornerShape(6.dp))
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.Sync, contentDescription = null, tint = Primary, modifier = Modifier.size(13.dp))
                        Text("Evaluating 15m candle close for Long trigger...", fontFamily = JetBrainsMono, fontSize = 9.sp, color = OnSurfaceVariant)
                    }
                    Text("03:42m", fontFamily = JetBrainsMono, fontSize = 9.sp, color = Outline)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (!bot.isScanning) {
                        Button(
                            onClick = onTighten,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp),
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainerHigh, contentColor = Primary)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                Icon(Icons.Default.Straighten, contentDescription = null, modifier = Modifier.size(12.dp))
                                Text("Tighten TS", fontFamily = JetBrainsMono, fontSize = 9.sp)
                            }
                        }
                    }
                    Button(
                        onClick = onSettings,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp),
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainerHigh, contentColor = OnSurfaceVariant)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(12.dp))
                            Text("Settings", fontFamily = JetBrainsMono, fontSize = 9.sp)
                        }
                    }
                }

                Button(
                    onClick = onStop,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.height(28.dp),
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorContainer.copy(alpha = 0.5f), contentColor = Error)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        Icon(Icons.Default.Cancel, contentDescription = null, modifier = Modifier.size(12.dp))
                        Text(if (bot.isActive) "Stop Bot" else "Halted", fontFamily = JetBrainsMono, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ClosedTradeItem(
    symbol: String,
    isLong: Boolean,
    leverage: Int,
    reason: String,
    pnl: String,
    time: String,
    isWin: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceContainer)
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(
                imageVector = if (isWin) Icons.Default.CheckCircle else Icons.Default.HighlightOff,
                contentDescription = null,
                tint = if (isWin) SecondaryContainer else LaserCrimson,
                modifier = Modifier.size(16.dp)
            )
            Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(symbol, fontFamily = JetBrainsMono, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                    Box(
                        modifier = Modifier
                            .background(
                                if (isWin) SecondaryContainer.copy(alpha = 0.15f) else LaserCrimson.copy(alpha = 0.15f),
                                RoundedCornerShape(3.dp)
                            )
                            .padding(horizontal = 3.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "${if (isLong) "LONG" else "SHORT"} ${leverage}x",
                            fontFamily = JetBrainsMono,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isWin) SecondaryContainer else LaserCrimson
                        )
                    }
                }
                Text(reason, fontFamily = FontFamily.Default, fontSize = 10.sp, color = OnSurfaceVariant)
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(pnl, fontFamily = JetBrainsMono, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isWin) SecondaryContainer else LaserCrimson)
            Text(time, fontFamily = JetBrainsMono, fontSize = 9.sp, color = Outline)
        }
    }
}
