package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.FormatIndentIncrease
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
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
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PineEditorScreen(
    repository: MarketRepository,
    onBack: () -> Unit,
    onShowToast: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var pineCode by remember { mutableStateOf(repository.pineScriptSource.value) }
    var activeScriptTab by remember { mutableStateOf("Supertrend_Pullback.pine") }
    var saveStatusText by remember { mutableStateOf("Save") }
    var isCompiling by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "cursor")
    val cursorAlpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursor"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("pine_editor_screen")
    ) {
        // 1. Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceContainerLowest)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = OnSurfaceVariant)
                }
                Column {
                    Text("Pine Script V5 Editor & Studio", fontFamily = SpaceGrotesk, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                    Text("AlgoPulse Terminal", fontFamily = JetBrainsMono, fontSize = 10.sp, color = OnSurfaceVariant)
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                    modifier = Modifier
                        .background(SurfaceContainerLow, RoundedCornerShape(10.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text("BTCUSDT", fontFamily = JetBrainsMono, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                        Text("LIVE", fontFamily = JetBrainsMono, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = SecondaryContainer)
                    }
                }
                IconButton(onClick = { onShowToast("Editor preferences") }, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More", tint = OnSurfaceVariant, modifier = Modifier.size(18.dp))
                }
            }
        }

        // 2. Status & Controls Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceContainerLow)
                .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(modifier = Modifier.background(PrimaryContainer.copy(alpha = 0.12f), RoundedCornerShape(10.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                    Text("v5 Compiler Active", fontFamily = JetBrainsMono, fontSize = 9.sp, color = PrimaryFixedDim)
                }
                Box(modifier = Modifier.background(SecondaryContainer.copy(alpha = 0.15f), RoundedCornerShape(10.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(SecondaryContainer))
                        Text("AST Ready", fontFamily = JetBrainsMono, fontSize = 9.sp, color = SecondaryFixed)
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(
                    onClick = {
                        repository.updatePineScript(pineCode)
                        saveStatusText = "Saved ✓"
                        onShowToast("Pine script saved successfully")
                        coroutineScope.launch {
                            delay(1800)
                            saveStatusText = "Save"
                        }
                    },
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                    modifier = Modifier.height(28.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainerHigh, contentColor = OnSurface)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        Icon(Icons.Default.Save, contentDescription = null, tint = PrimaryFixedDim, modifier = Modifier.size(12.dp))
                        Text(saveStatusText, fontFamily = JetBrainsMono, fontSize = 10.sp)
                    }
                }

                Button(
                    onClick = {
                        isCompiling = true
                        onShowToast("Compiling Pine AST...")
                        coroutineScope.launch {
                            delay(400)
                            isCompiling = false
                            onShowToast("Pine Script v5 compiled successfully in 14ms")
                        }
                    },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                    modifier = Modifier.height(28.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SecondaryContainer, contentColor = Color(0xFF00391E))
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                        Text("Run", fontFamily = JetBrainsMono, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 3. File Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceContainerLow)
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf("Supertrend_Pullback.pine", "EMA_Cross_ATR.pine", "RSI_Divergence.pine").forEach { file ->
                val isActive = file == activeScriptTab
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isActive) SurfaceContainerHigh else SurfaceContainerLowest)
                        .clickable {
                            activeScriptTab = file
                            onShowToast("Switched to $file")
                        }
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.Code, contentDescription = null, tint = if (isActive) PrimaryFixedDim else OnSurfaceVariant, modifier = Modifier.size(12.dp))
                        Text(file, fontFamily = JetBrainsMono, fontSize = 10.sp, color = if (isActive) PrimaryFixedDim else OnSurfaceVariant)
                        if (isActive) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = Outline, modifier = Modifier.size(12.dp))
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceContainerLowest)
                    .clickable { onShowToast("Created new Pine script template") }
                    .padding(horizontal = 8.dp, vertical = 5.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Primary, modifier = Modifier.size(13.dp))
                    Text("New Script", fontFamily = JetBrainsMono, fontSize = 10.sp, color = Primary)
                }
            }
        }

        // 4. Quick Toolbar & Snippets
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceContainerLowest)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = { onShowToast("Undo") }, modifier = Modifier.size(28.dp).background(SurfaceContainerHigh, RoundedCornerShape(4.dp))) {
                    Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = "Undo", tint = OnSurfaceVariant, modifier = Modifier.size(14.dp))
                }
                IconButton(onClick = { onShowToast("Redo") }, modifier = Modifier.size(28.dp).background(SurfaceContainerHigh, RoundedCornerShape(4.dp))) {
                    Icon(Icons.AutoMirrored.Filled.Redo, contentDescription = "Redo", tint = OnSurfaceVariant, modifier = Modifier.size(14.dp))
                }
                IconButton(onClick = { onShowToast("Indented") }, modifier = Modifier.size(28.dp).background(SurfaceContainerHigh, RoundedCornerShape(4.dp))) {
                    Icon(Icons.AutoMirrored.Filled.FormatIndentIncrease, contentDescription = "Indent", tint = OnSurfaceVariant, modifier = Modifier.size(14.dp))
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(
                    modifier = Modifier
                        .background(SurfaceContainer, RoundedCornerShape(4.dp))
                        .clickable {
                            pineCode += "\nmyRsi = ta.rsi(close, 14)"
                            onShowToast("Inserted ta.rsi(close, 14)")
                        }
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    Text("+ ta.rsi", fontFamily = JetBrainsMono, fontSize = 9.sp, color = PrimaryFixedDim)
                }
                Box(
                    modifier = Modifier
                        .background(SurfaceContainer, RoundedCornerShape(4.dp))
                        .clickable {
                            pineCode += "\nmyEma = ta.ema(close, 20)"
                            onShowToast("Inserted ta.ema(close, 20)")
                        }
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    Text("+ ta.ema", fontFamily = JetBrainsMono, fontSize = 9.sp, color = PrimaryFixedDim)
                }
                Box(
                    modifier = Modifier
                        .background(SurfaceContainer, RoundedCornerShape(4.dp))
                        .clickable {
                            pineCode += "\nstrategy.entry(\"Long\", strategy.long)"
                            onShowToast("Inserted strategy.entry(\"Long\", strategy.long)")
                        }
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    Text("+ entry", fontFamily = JetBrainsMono, fontSize = 9.sp, color = SecondaryFixed)
                }
            }
        }

        // 5. Code Canvas (Line numbers + editable code area)
        val codeLines = pineCode.split("\n")
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(SurfaceContainerLowest)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(vertical = 6.dp)
            ) {
                items(codeLines.size) { index ->
                    val lineNum = index + 1
                    val text = codeLines[index]
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 1.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Line number
                        Text(
                            text = "$lineNum",
                            fontFamily = JetBrainsMono,
                            fontSize = 11.sp,
                            color = OutlineVariant,
                            modifier = Modifier.width(32.dp).padding(end = 8.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.End
                        )
                        // Syntax formatted line
                        val lineColor = when {
                            text.startsWith("//") -> OutlineVariant
                            text.contains("strategy(") || text.contains("input.") -> PrimaryFixedDim
                            text.contains("if") || text.contains("ta.") -> SecondaryFixed
                            text.contains("alert(") -> PrimaryContainer
                            else -> OnSurface
                        }
                        Row(modifier = Modifier.weight(1f)) {
                            Text(
                                text = text,
                                fontFamily = JetBrainsMono,
                                fontSize = 11.sp,
                                color = lineColor,
                                maxLines = 1
                            )
                            if (index == codeLines.lastIndex) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp, 12.dp)
                                        .background(PrimaryContainer.copy(alpha = cursorAlpha))
                                )
                            }
                        }
                    }
                }
            }
        }

        // 6. Compiler Diagnostics Panel
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceContainer)
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.Terminal, contentDescription = null, tint = PrimaryFixedDim, modifier = Modifier.size(15.dp))
                    Text("Compiler Diagnostics", fontFamily = SpaceGrotesk, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                }
                Text("Build #88", fontFamily = JetBrainsMono, fontSize = 10.sp, color = OnSurfaceVariant)
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(modifier = Modifier.background(SecondaryContainer.copy(alpha = 0.15f), RoundedCornerShape(10.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SecondaryFixed, modifier = Modifier.size(11.dp))
                        Text("0 Errors", fontFamily = JetBrainsMono, fontSize = 9.sp, color = SecondaryFixed)
                    }
                }
                Box(modifier = Modifier.background(ErrorContainer.copy(alpha = 0.3f), RoundedCornerShape(10.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = TertiaryFixed, modifier = Modifier.size(11.dp))
                        Text("1 Warning", fontFamily = JetBrainsMono, fontSize = 9.sp, color = TertiaryFixed)
                    }
                }
                Text("Local client-side execution active", fontFamily = JetBrainsMono, fontSize = 9.sp, color = Outline)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceContainerLowest, RoundedCornerShape(6.dp))
                    .padding(6.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("[14:32:01] Pine v5 AST Compiled successfully in 14ms", fontFamily = JetBrainsMono, fontSize = 9.sp, color = SecondaryFixed)
                    Text("[14:32:01] Warning (Line 18): 'volRatio' calculated on dynamic ATR might induce slippage", fontFamily = JetBrainsMono, fontSize = 9.sp, color = TertiaryFixed)
                    Text("[14:32:02] Ready for backtest injection", fontFamily = JetBrainsMono, fontSize = 9.sp, color = PrimaryFixedDim)
                }
            }
        }

        // 7. Bottom Action Buttons
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceContainerLow)
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Button(
                onClick = {
                    onShowToast("Pine Script injected into Backtester matrix")
                    onBack()
                },
                modifier = Modifier.fillMaxWidth().height(42.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainer, contentColor = Color(0xFF00363D))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(18.dp))
                    Text("Inject into Backtester", fontFamily = SpaceGrotesk, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Button(
                onClick = { onShowToast("Pine script saved to Google Drive Vault") },
                modifier = Modifier.fillMaxWidth().height(36.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainer, contentColor = OnSurfaceVariant)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.Backup, contentDescription = null, tint = PrimaryFixedDim, modifier = Modifier.size(15.dp))
                    Text("Save to Google Drive Vault", fontFamily = SpaceGrotesk, fontSize = 11.sp)
                }
            }
        }
    }
}
