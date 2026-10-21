package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.ui.components.AppBottomNav
import com.example.ui.components.AppHeader
import com.example.ui.components.AppTab
import com.example.ui.screens.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

enum class SubScreen {
    NONE,
    PINE_EDITOR,
    COMPARE_STRATEGIES
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AlgoPulseApp()
            }
        }
    }
}

@Composable
fun AlgoPulseApp() {
    val repository = remember { MarketRepository() }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var currentTab by remember { mutableStateOf(AppTab.DASHBOARD) }
    var currentSubScreen by remember { mutableStateOf(SubScreen.NONE) }

    var showDeploySheet by remember { mutableStateOf(false) }
    var deployInitialPair by remember { mutableStateOf("BTC/USDT") }

    var showNotificationsDialog by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }

    fun showToast(message: String) {
        coroutineScope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(message)
        }
    }

    // Android Hardware / Predictive Back handling
    BackHandler(enabled = currentSubScreen != SubScreen.NONE || currentTab != AppTab.DASHBOARD) {
        if (currentSubScreen != SubScreen.NONE) {
            currentSubScreen = SubScreen.NONE
        } else if (currentTab != AppTab.DASHBOARD) {
            currentTab = AppTab.DASHBOARD
        }
    }

    val screenTitle = when {
        currentSubScreen == SubScreen.PINE_EDITOR -> "Pine Script™ v5 Studio"
        currentSubScreen == SubScreen.COMPARE_STRATEGIES -> "Strategy Leaderboard"
        currentTab == AppTab.DASHBOARD -> "Perpetual Dashboard"
        currentTab == AppTab.CHART -> "Chart Terminal"
        currentTab == AppTab.STRATEGY -> "Backtest Matrix"
        currentTab == AppTab.AUTO_TRADE -> "Auto Trade Vault"
        currentTab == AppTab.SCREENER -> "Perpetuals Radar"
        currentTab == AppTab.MENU -> "Engine Settings"
        else -> "AlgoPulse"
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("main_scaffold"),
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .padding(bottom = if (currentSubScreen == SubScreen.NONE) 70.dp else 16.dp)
                    .testTag("app_snackbar_host")
            ) { data ->
                Snackbar(
                    containerColor = SurfaceContainerHigh,
                    contentColor = OnSurface,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(data.visuals.message, fontSize = 12.sp)
                }
            }
        },
        topBar = {
            if (currentSubScreen == SubScreen.NONE) {
                AppHeader(
                    screenTitle = screenTitle,
                    onBackClick = null,
                    onNotificationClick = { showNotificationsDialog = true },
                    onProfileClick = { showProfileDialog = true }
                )
            }
        },
        bottomBar = {
            if (currentSubScreen == SubScreen.NONE) {
                AppBottomNav(
                    currentTab = currentTab,
                    onTabSelected = { newTab ->
                        currentTab = newTab
                    }
                )
            }
        },
        containerColor = Surface
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Screen Routing
            when (currentSubScreen) {
                SubScreen.PINE_EDITOR -> {
                    PineEditorScreen(
                        repository = repository,
                        onBack = { currentSubScreen = SubScreen.NONE },
                        onRunBacktest = {
                            currentSubScreen = SubScreen.NONE
                            currentTab = AppTab.STRATEGY
                            showToast("Pine script loaded into Backtest Engine")
                        },
                        onShowToast = ::showToast
                    )
                }
                SubScreen.COMPARE_STRATEGIES -> {
                    CompareStrategiesScreen(
                        repository = repository,
                        onBack = { currentSubScreen = SubScreen.NONE },
                        onSelectStrategyForBacktest = { stratName ->
                            currentSubScreen = SubScreen.NONE
                            currentTab = AppTab.STRATEGY
                            showToast("Configured $stratName for backtesting")
                        },
                        onDeployStrategy = { stratName ->
                            currentSubScreen = SubScreen.NONE
                            deployInitialPair = "SOL/USDT"
                            showDeploySheet = true
                        },
                        onShowToast = ::showToast
                    )
                }
                SubScreen.NONE -> {
                    when (currentTab) {
                        AppTab.DASHBOARD -> {
                            DashboardScreen(
                                repository = repository,
                                onNavigateToChart = { pair ->
                                    repository.setPair(pair)
                                    currentTab = AppTab.CHART
                                },
                                onOpenDeployBot = { pair ->
                                    deployInitialPair = pair
                                    showDeploySheet = true
                                },
                                onShowToast = ::showToast
                            )
                        }
                        AppTab.CHART -> {
                            ChartScreen(
                                repository = repository,
                                onShowToast = ::showToast
                            )
                        }
                        AppTab.STRATEGY -> {
                            StrategyScreen(
                                repository = repository,
                                onOpenCompare = { currentSubScreen = SubScreen.COMPARE_STRATEGIES },
                                onOpenPineEditor = { currentSubScreen = SubScreen.PINE_EDITOR },
                                onShowToast = ::showToast
                            )
                        }
                        AppTab.AUTO_TRADE -> {
                            AutoTradeScreen(
                                repository = repository,
                                onOpenDeployBot = {
                                    deployInitialPair = "BTC/USDT"
                                    showDeploySheet = true
                                },
                                onShowToast = ::showToast
                            )
                        }
                        AppTab.SCREENER -> {
                            ScreenerScreen(
                                repository = repository,
                                onOpenDeployBot = { pair ->
                                    deployInitialPair = pair
                                    showDeploySheet = true
                                },
                                onShowToast = ::showToast
                            )
                        }
                        AppTab.MENU -> {
                            MenuScreen(
                                repository = repository,
                                onShowToast = ::showToast
                            )
                        }
                    }
                }
            }
        }
    }

    // Deploy Quantitative Bot Sheet
    if (showDeploySheet) {
        DeployBotSheet(
            repository = repository,
            initialPair = deployInitialPair,
            onDismiss = { showDeploySheet = false },
            onSuccess = { msg ->
                showToast(msg)
                currentTab = AppTab.AUTO_TRADE
            }
        )
    }

    // Notifications Dialog
    if (showNotificationsDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationsDialog = false },
            containerColor = SurfaceContainerLowest,
            shape = RoundedCornerShape(16.dp),
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Notifications, contentDescription = null, tint = Primary)
                    Text("Execution Alerts", color = OnSurface, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf(
                        "14:22:05" to "Trailing stop moved to $66,420.00 (Locked +$128.40)",
                        "14:18:10" to "ETHUSDT RSI 14 entered oversold region (28.4)",
                        "14:12:33" to "SOLUSDT volume anomaly filtered cleanly",
                        "14:05:01" to "Engine heartbeat active. Latency: 18ms"
                    ).forEach { (time, msg) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(SurfaceContainerLow)
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(time, color = Primary, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                            Text(msg, color = OnSurface, fontSize = 11.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showNotificationsDialog = false }) {
                    Text("Dismiss", color = Primary, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Profile & Vault Dialog
    if (showProfileDialog) {
        AlertDialog(
            onDismissRequest = { showProfileDialog = false },
            containerColor = SurfaceContainerLowest,
            shape = RoundedCornerShape(16.dp),
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.AccountBalanceWallet, contentDescription = null, tint = Primary)
                    Text("Trading Account & Vault", color = OnSurface, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("DEMO VAULT BALANCE", color = OnSurfaceVariant, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("10,342.50 USDT", color = Primary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Text("Simulated Capital • 0% Real Financial Risk", color = OnSurfaceVariant, fontSize = 11.sp)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Exchange Gateway", color = OnSurface, fontSize = 12.sp)
                        Text("CoinDCX / Binance REST", color = Tertiary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Order Engine Ping", color = OnSurface, fontSize = 12.sp)
                        Text("18 ms (Ultra-low)", color = Primary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            showProfileDialog = false
                            showToast("Demo Vault balance reset to 10,000.00 USDT")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceBright)
                    ) {
                        Text("Reset Demo Balance to 10,000 USDT", color = OnSurface, fontSize = 11.sp)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showProfileDialog = false }) {
                    Text("Close", color = Primary, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
