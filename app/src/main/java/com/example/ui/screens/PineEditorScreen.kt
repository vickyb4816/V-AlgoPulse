package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MarketRepository
import com.example.ui.components.PineScriptCodeEditor
import com.example.ui.theme.*

@Composable
fun PineEditorScreen(
    repository: MarketRepository,
    onBack: () -> Unit,
    onRunBacktest: () -> Unit,
    onShowToast: (String) -> Unit
) {
    val initialScript by repository.pineScriptSource.collectAsState()
    var scriptText by remember(initialScript) { mutableStateOf(initialScript) }
    var showTemplateMenu by remember { mutableStateOf(false) }

    val templates = mapOf(
        "Supertrend v2 (Trend)" to """//@version=5
strategy("AlgoPulse Supertrend v2", overlay=true, margin_long=100)

atrPeriod = input.int(10, "ATR Period", minval=1)
factor = input.float(3.0, "ATR Factor", step=0.1)

[superTrend, direction] = ta.supertrend(factor, atrPeriod)

// Execution Engine Hooks
if (ta.crossover(close, superTrend))
    strategy.entry("Long", strategy.long, comment="ST_BUY")
    alert("Supertrend Long Triggered", alert.freq_once_per_bar)

if (ta.crossunder(close, superTrend))
    strategy.close("Long", comment="ST_EXIT")

// Visual Signal Rendering
plotshape(direction < 0, title="Up Trend", style=shape.triangleup)
plot(superTrend, "ST Line", color=direction < 0 ? #00F090 : #FF3B57)

// Dynamic Volatility Scaling
volRatio = ta.atr(14) / ta.sma(ta.atr(14), 50)
isVolSpike = volRatio > 1.35
if (isVolSpike)
    strategy.exit("VolExit", "Long", trail_points=close*0.012)
""",
        "Dual EMA + ATR (Trend)" to """//@version=5
strategy("Dual EMA + ATR Scalp", overlay=true)

fastEma = ta.ema(close, 20)
slowEma = ta.ema(close, 50)
atr = ta.atr(14)

longCondition = ta.crossover(fastEma, slowEma) and close > ta.ema(close, 200)
if (longCondition)
    strategy.entry("Long", strategy.long, comment="EMA_CROSS")
    strategy.exit("TP/SL", "Long", stop=close - atr*1.5, limit=close + atr*3.0)

plot(fastEma, "Fast EMA", color=#00F090)
plot(slowEma, "Slow EMA", color=#FFB454)
""",
        "Bollinger Mean Reversion" to """//@version=5
strategy("BB Mean Reversion", overlay=true)

[basis, upper, lower] = ta.bb(close, 20, 2.0)
rsi = ta.rsi(close, 14)

if (close < lower and rsi < 30)
    strategy.entry("Long", strategy.long, comment="BB_OVERSOLD")

if (close > basis or rsi > 65)
    strategy.close("Long", comment="BB_REVERT")

plot(basis, "Basis", color=#79B8FF)
plot(upper, "Upper", color=#FF7B72)
plot(lower, "Lower", color=#7EE787)
"""
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
            .testTag("pine_editor_screen")
    ) {
        // Top Navigation & Presets Bar
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
                    modifier = Modifier.size(36.dp).testTag("pine_editor_back_btn")
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
                            text = "Pine Script™ v5 Studio",
                            color = OnSurface,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(PrimaryContainer)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "STABLE",
                                color = Primary,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Text(
                        text = "Real-time client compiler & optimizer",
                        color = OnSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }

            // Quick actions
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Templates button
                Box {
                    OutlinedButton(
                        onClick = { showTemplateMenu = true },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = OnSurfaceVariant),
                        modifier = Modifier.testTag("pine_presets_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Source, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Presets", fontSize = 11.sp)
                    }

                    DropdownMenu(
                        expanded = showTemplateMenu,
                        onDismissRequest = { showTemplateMenu = false },
                        modifier = Modifier.background(SurfaceContainerLow)
                    ) {
                        templates.forEach { (name, code) ->
                            DropdownMenuItem(
                                text = { Text(name, color = OnSurface, fontSize = 13.sp) },
                                onClick = {
                                    scriptText = code
                                    showTemplateMenu = false
                                    repository.updatePineScript(code)
                                    onShowToast("Loaded template: $name")
                                }
                            )
                        }
                    }
                }

                // Run in Backtester Action
                Button(
                    onClick = {
                        repository.updatePineScript(scriptText)
                        onRunBacktest()
                    },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TertiaryContainer),
                    modifier = Modifier.testTag("pine_run_backtest_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Insights,
                        contentDescription = null,
                        tint = Tertiary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Backtest",
                        color = Tertiary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Central Pine Script Code Editor Component
        PineScriptCodeEditor(
            scriptContent = scriptText,
            onContentChange = { newContent ->
                scriptText = newContent
            },
            onSave = { savedContent ->
                repository.updatePineScript(savedContent)
                repository.addTelemetryLog("EDITOR", "Pine script code saved. (${savedContent.lines().size} lines)")
            },
            onShowToast = onShowToast,
            modifier = Modifier.weight(1f)
        )
    }
}
