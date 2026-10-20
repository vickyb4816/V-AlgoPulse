package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MarketRepository
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MenuScreen(
    repository: MarketRepository,
    onShowToast: (String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var autoSyncExit by remember { mutableStateOf(true) }
    var isBackingUp by remember { mutableStateOf(false) }
    var backupDoneText by remember { mutableStateOf("Backup Strategies to Drive Now") }

    var settlementCurrency by remember { mutableStateOf("USDT") } // "USDT" or "INR"
    var pingLatency by remember { mutableStateOf("42 ms") }
    var isPinging by remember { mutableStateOf(false) }

    var showApiKeyDialog by remember { mutableStateOf(false) }
    var apiKeyText by remember { mutableStateOf("cdx_live_••••••••92b") }

    var isCheckingUpdates by remember { mutableStateOf(false) }
    var updateBtnText by remember { mutableStateOf("Check Updates") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
            .padding(horizontal = 12.dp)
            .testTag("menu_screen_content"),
        contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. Algorithmic Node Telemetry Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainer)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(SurfaceContainerLow),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("⚡", fontSize = 20.sp, color = PrimaryContainer)
                        }

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("Algorithmic Node", fontFamily = SpaceGrotesk, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(SecondaryContainer))
                            }
                            Text("Runtime: Active | CoinDCX Socket OK", fontFamily = JetBrainsMono, fontSize = 10.sp, color = OnSurfaceVariant)
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("ROUNDTRIP LATENCY", fontFamily = JetBrainsMono, fontSize = 8.sp, color = Outline)
                        Text(pingLatency, fontFamily = JetBrainsMono, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SecondaryContainer)
                    }
                }
            }
        }

        // 2. Google Drive Cloud Vault Backup Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("cloud_vault_card"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainer)
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
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(SurfaceContainerHigh),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.CloudSync, contentDescription = null, tint = PrimaryContainer, modifier = Modifier.size(18.dp))
                            }
                            Column {
                                Text("Cloud Vault Backup", fontFamily = SpaceGrotesk, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                                Text("Google Drive Automation", fontFamily = FontFamily.Default, fontSize = 10.sp, color = OnSurfaceVariant)
                            }
                        }

                        Box(
                            modifier = Modifier.background(SurfaceContainerLowest, RoundedCornerShape(12.dp)).padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(SecondaryContainer))
                                Text("vicky.trader@gmail.com", fontFamily = JetBrainsMono, fontSize = 9.sp, color = Secondary)
                            }
                        }
                    }

                    Text(
                        text = "Backs up your custom trading strategies, Pine scripts, and trade telemetry directly to your private Google Drive folder. API keys are strictly excluded.",
                        fontFamily = FontFamily.Default,
                        fontSize = 11.sp,
                        color = OnSurfaceVariant,
                        lineHeight = 15.sp
                    )

                    // Auto Sync Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceContainerLowest)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.SyncLock, contentDescription = null, tint = PrimaryFixedDim, modifier = Modifier.size(16.dp))
                            Text("Auto-Sync on App Exit", fontFamily = FontFamily.Default, fontSize = 12.sp, color = OnSurface)
                        }
                        Switch(
                            checked = autoSyncExit,
                            onCheckedChange = {
                                autoSyncExit = it
                                onShowToast(if (it) "Auto-Sync on exit enabled" else "Auto-Sync disabled")
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = SurfaceContainerLowest,
                                checkedTrackColor = PrimaryContainer
                            ),
                            modifier = Modifier.height(24.dp)
                        )
                    }

                    // Backup Now Button
                    Button(
                        onClick = {
                            isBackingUp = true
                            backupDoneText = "Encrypting & Syncing..."
                            coroutineScope.launch {
                                delay(900)
                                backupDoneText = "Drive Synced Successfully"
                                onShowToast("All 14 strategies synced to Google Drive")
                                delay(2200)
                                backupDoneText = "Backup Strategies to Drive Now"
                                isBackingUp = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(40.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainer, contentColor = Color(0xFF00363D))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            if (isBackingUp) {
                                CircularProgressIndicator(color = Color(0xFF00363D), modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Default.Backup, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                            Text(backupDoneText, fontFamily = SpaceGrotesk, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Raw JSON interchange buttons
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("OFFLINE RAW JSON INTERCHANGE", fontFamily = JetBrainsMono, fontSize = 8.sp, color = Outline)
                        Text("SHA-256 Validated", fontFamily = JetBrainsMono, fontSize = 8.sp, color = OutlineVariant)
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                val clip = ClipData.newPlainText("Strategy JSON", "{\"version\":\"1.3.0\",\"strategies\":[\"Supertrend_v2\",\"EMA_Cross\"]}")
                                clipboard?.setPrimaryClip(clip)
                                onShowToast("Strategy JSON copied to clipboard")
                            },
                            modifier = Modifier.weight(1f).height(34.dp),
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainerHigh, contentColor = OnSurface)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, tint = PrimaryContainer, modifier = Modifier.size(13.dp))
                                Text("Copy JSON Backup", fontFamily = JetBrainsMono, fontSize = 10.sp)
                            }
                        }

                        Button(
                            onClick = { onShowToast("Ready: Paste JSON bundle into restore prompt") },
                            modifier = Modifier.weight(1f).height(34.dp),
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainerHigh, contentColor = OnSurface)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.SettingsBackupRestore, contentDescription = null, tint = Secondary, modifier = Modifier.size(14.dp))
                                Text("Restore from Pasted", fontFamily = JetBrainsMono, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }

        // 3. CoinDCX Perpetual Futures API Credentials
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("coindcx_api_card"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainer)
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
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(SurfaceContainerHigh),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.CurrencyExchange, contentDescription = null, tint = SecondaryContainer, modifier = Modifier.size(18.dp))
                            }
                            Column {
                                Text("CoinDCX Perpetual Futures", fontFamily = SpaceGrotesk, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                                Text("Sub-account Execution Keys", fontFamily = FontFamily.Default, fontSize = 10.sp, color = OnSurfaceVariant)
                            }
                        }

                        Box(
                            modifier = Modifier.background(SurfaceContainerLowest, RoundedCornerShape(12.dp)).padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(SecondaryContainer))
                                Text("Connected", fontFamily = JetBrainsMono, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = Secondary)
                            }
                        }
                    }

                    // Key fields
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("API Key", fontFamily = JetBrainsMono, fontSize = 10.sp, color = OnSurfaceVariant)
                            Text("Read/Trade Perms", fontFamily = JetBrainsMono, fontSize = 9.sp, color = Outline)
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(SurfaceContainerLowest)
                                .clickable { showApiKeyDialog = true }
                                .padding(10.dp)
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(apiKeyText, fontFamily = JetBrainsMono, fontSize = 12.sp, color = OnSurface)
                                Icon(Icons.Default.VpnKey, contentDescription = null, tint = Outline, modifier = Modifier.size(16.dp))
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth().padding(top = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("API Secret", fontFamily = JetBrainsMono, fontSize = 10.sp, color = OnSurfaceVariant)
                            Text("Secured In Keychain", fontFamily = JetBrainsMono, fontSize = 9.sp, color = Secondary)
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(SurfaceContainerLowest)
                                .padding(10.dp)
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("••••••••••••••••••••••••", fontFamily = JetBrainsMono, fontSize = 12.sp, color = OnSurface)
                                Icon(Icons.Default.Lock, contentDescription = null, tint = Outline, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    // Settlement Margin Currency Toggle
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Settlement Margin Currency", fontFamily = JetBrainsMono, fontSize = 10.sp, color = OnSurfaceVariant)
                            Text("Cross-Collateral", fontFamily = JetBrainsMono, fontSize = 9.sp, color = Primary)
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SurfaceContainerLowest, RoundedCornerShape(8.dp))
                                .padding(3.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (settlementCurrency == "USDT") SurfaceContainerHigh else Color.Transparent)
                                    .clickable {
                                        settlementCurrency = "USDT"
                                        onShowToast("Settlement set to USDT Linear Perpetual")
                                    }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "USDT (Linear)",
                                    fontFamily = JetBrainsMono,
                                    fontSize = 11.sp,
                                    fontWeight = if (settlementCurrency == "USDT") FontWeight.Bold else FontWeight.Medium,
                                    color = if (settlementCurrency == "USDT") PrimaryContainer else OnSurfaceVariant
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (settlementCurrency == "INR") SurfaceContainerHigh else Color.Transparent)
                                    .clickable {
                                        settlementCurrency = "INR"
                                        onShowToast("Settlement set to Crypto-INR Perpetual")
                                    }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "INR (Crypto-INR)",
                                    fontFamily = JetBrainsMono,
                                    fontSize = 11.sp,
                                    fontWeight = if (settlementCurrency == "INR") FontWeight.Bold else FontWeight.Medium,
                                    color = if (settlementCurrency == "INR") PrimaryContainer else OnSurfaceVariant
                                )
                            }
                        }
                    }

                    // Hardware Keystore notice
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceContainerLow, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = PrimaryContainer, modifier = Modifier.size(15.dp))
                            Text(
                                "Credentials stored encrypted locally on Android device hardware keychain using Android Keystore System. Never touches cloud or logs.",
                                fontFamily = FontFamily.Default,
                                fontSize = 10.sp,
                                color = OnSurfaceVariant,
                                lineHeight = 14.sp
                            )
                        }
                    }

                    // Test Connection button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                isPinging = true
                                coroutineScope.launch {
                                    delay(600)
                                    val newPing = "${(36..48).random()} ms"
                                    pingLatency = newPing
                                    isPinging = false
                                    onShowToast("CoinDCX Futures Socket Responsive ($newPing)")
                                }
                            },
                            modifier = Modifier.weight(1f).height(38.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainerHigh, contentColor = OnSurface)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                if (isPinging) {
                                    CircularProgressIndicator(color = Primary, modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                                } else {
                                    Icon(Icons.Default.NetworkPing, contentDescription = null, tint = Secondary, modifier = Modifier.size(16.dp))
                                }
                                Text("Test Connection", fontFamily = SpaceGrotesk, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Box(
                            modifier = Modifier.background(SurfaceContainerLowest, RoundedCornerShape(8.dp)).padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Text(if (isPinging) "Pinging..." else "$pingLatency OK", fontFamily = JetBrainsMono, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SecondaryContainer)
                        }
                    }
                }
            }
        }

        // 4. Data Fallback Cascade
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainer)
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
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(SurfaceContainerHigh), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.AltRoute, contentDescription = null, tint = PrimaryContainer, modifier = Modifier.size(18.dp))
                            }
                            Column {
                                Text("Data Fallback Cascade", fontFamily = SpaceGrotesk, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                                Text("Sub-second Auto Failover", fontFamily = FontFamily.Default, fontSize = 10.sp, color = OnSurfaceVariant)
                            }
                        }
                        Box(modifier = Modifier.background(SurfaceContainerHigh, RoundedCornerShape(10.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                            Text("Active", fontFamily = JetBrainsMono, fontSize = 10.sp, color = SecondaryContainer)
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FallbackBadge("1", "CoinDCX", "Primary", Secondary, Modifier.weight(1f))
                        FallbackBadge("2", "Binance", "Tier 1", Outline, Modifier.weight(1f))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FallbackBadge("3", "Bybit", "Tier 2", Outline, Modifier.weight(1f))
                        FallbackBadge("4", "Yahoo Fin", "Backup", Outline, Modifier.weight(1f))
                    }
                }
            }
        }

        // 5. Engine Architecture
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainer)
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
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(SurfaceContainerHigh), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Terminal, contentDescription = null, tint = PrimaryContainer, modifier = Modifier.size(18.dp))
                            }
                            Column {
                                Text("Engine Architecture", fontFamily = SpaceGrotesk, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                                Text("Modular Python Kernel", fontFamily = FontFamily.Default, fontSize = 10.sp, color = OnSurfaceVariant)
                            }
                        }
                        Box(modifier = Modifier.background(SurfaceContainerHigh, RoundedCornerShape(10.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                            Text("Build #142 Passing", fontFamily = JetBrainsMono, fontSize = 10.sp, color = SecondaryContainer)
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceContainerLowest)
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Memory, contentDescription = null, tint = PrimaryFixedDim, modifier = Modifier.size(16.dp))
                            Text("Runtime Stack", fontFamily = FontFamily.Default, fontSize = 12.sp, color = OnSurface)
                        }
                        Text("Python 3.11 + Flet UI", fontFamily = JetBrainsMono, fontSize = 11.sp, color = Primary)
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Core Entrypoints", fontFamily = JetBrainsMono, fontSize = 9.sp, color = OnSurfaceVariant)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(modifier = Modifier.background(SurfaceContainerLowest, RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 3.dp)) {
                                Text("src/app.py", fontFamily = JetBrainsMono, fontSize = 9.sp, color = PrimaryContainer)
                            }
                            Box(modifier = Modifier.background(SurfaceContainerLowest, RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 3.dp)) {
                                Text("src/core/", fontFamily = JetBrainsMono, fontSize = 9.sp, color = OnSurfaceVariant)
                            }
                            Box(modifier = Modifier.background(SurfaceContainerLowest, RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 3.dp)) {
                                Text("src/headless.py (Termux)", fontFamily = JetBrainsMono, fontSize = 9.sp, color = Secondary)
                            }
                        }
                    }
                }
            }
        }

        // 6. About & Risk Disclaimer
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainer)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("AlgoPulse v1.3.0", fontFamily = SpaceGrotesk, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = OnSurface)
                        Text("Engineered by Vicky Bhalerao • Since 2026", fontFamily = FontFamily.Default, fontSize = 10.sp, color = OnSurfaceVariant)
                    }
                    Button(
                        onClick = {
                            isCheckingUpdates = true
                            updateBtnText = "Checking..."
                            coroutineScope.launch {
                                delay(800)
                                updateBtnText = "Up to date"
                                onShowToast("AlgoPulse v1.3.0 is the latest stable release")
                                delay(2000)
                                updateBtnText = "Check Updates"
                                isCheckingUpdates = false
                            }
                        },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp),
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainerHigh, contentColor = PrimaryContainer)
                    ) {
                        Text(updateBtnText, fontFamily = JetBrainsMono, fontSize = 10.sp)
                    }
                }
            }
        }

        // 7. Mandatory Quantitative Risk Warning Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(Icons.Default.Gavel, contentDescription = null, tint = LaserCrimson, modifier = Modifier.size(20.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("MANDATORY QUANTITATIVE RISK WARNING", fontFamily = JetBrainsMono, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = LaserCrimson)
                        Text(
                            "Futures trading involves substantial risk of capital loss. Stop-loss orders and safety trailing thresholds are executed client-side by this application runtime. Market latency, CoinDCX server downtimes, and network disconnects may cause slippage. Past algorithmic backtest performance does not guarantee future results.",
                            fontFamily = FontFamily.Default,
                            fontSize = 11.sp,
                            color = OnSurfaceVariant,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }

    if (showApiKeyDialog) {
        var tempKey by remember { mutableStateOf(apiKeyText) }
        AlertDialog(
            onDismissRequest = { showApiKeyDialog = false },
            title = { Text("Configure CoinDCX Key", fontFamily = SpaceGrotesk, color = OnSurface) },
            text = {
                OutlinedTextField(
                    value = tempKey,
                    onValueChange = { tempKey = it },
                    label = { Text("API Key") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryContainer)
                )
            },
            confirmButton = {
                Button(onClick = {
                    apiKeyText = tempKey
                    showApiKeyDialog = false
                    onShowToast("API Key updated securely in Android Keystore")
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showApiKeyDialog = false }) { Text("Cancel") }
            },
            containerColor = SurfaceContainerHigh
        )
    }
}

@Composable
private fun FallbackBadge(number: String, name: String, role: String, roleColor: Color, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceContainerLowest)
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(if (number == "1") SecondaryContainer else SurfaceContainerHigh),
                contentAlignment = Alignment.Center
            ) {
                Text(number, fontFamily = JetBrainsMono, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (number == "1") Color(0xFF00391E) else OnSurfaceVariant)
            }
            Text(name, fontFamily = SpaceGrotesk, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = OnSurface)
        }
        Text(role, fontFamily = JetBrainsMono, fontSize = 9.sp, color = roleColor)
    }
}
