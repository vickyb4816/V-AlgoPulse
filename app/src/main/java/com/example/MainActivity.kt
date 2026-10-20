package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MarketRepository
import com.example.model.StrategyBenchmark
import com.example.ui.components.AppBottomNav
import com.example.ui.components.AppHeader
import com.example.ui.components.AppTab
import com.example.ui.screens.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class ScreenDestination {
    TABS,
    PINE_EDITOR,
    COMPARE_STRATEGIES
}

class MainActivity : ComponentActivity() {
    private val repository = MarketRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AlgoPulseApp(repository = repository)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlgoPulseApp(repository: MarketRepository) {
    val coroutineScope = rememberCoroutineScope()
    var currentTab by remember { mutableStateOf(AppTab.CHART) }
    var currentDestination by remember { mutableStateOf(ScreenDestination.TABS) }
    var deployBotInitialPair by remember { mutableStateOf<String?>(null) }

    var toastMessage by remember { mutableStateOf<String?>(null) }

    fun showToast(msg: String) {
        toastMessage = msg
        coroutineScope.launch {
            delay(2400)
            if (toastMessage == msg) {
                toastMessage = null
            }
        }
    }

    // Hardware and predictive back handling
    if (deployBotInitialPair != null) {
        BackHandler {
            deployBotInitialPair = null
        }
    } else if (currentDestination != ScreenDestination.TABS) {
        BackHandler {
            currentDestination = ScreenDestination.TABS
        }
    } else if (currentTab != AppTab.CHART) {
        BackHandler {
            currentTab = AppTab.CHART
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
            .testTag("algopulse_scaffold"),
        containerColor = Surface,
        topBar = {
            if (currentDestination == ScreenDestination.TABS) {
                AppHeader(
                    screenTitle = currentTab.title,
                    onNotificationClick = { showToast("No new system alerts") },
                    onProfileClick = { showToast("User: Vicky Bhalerao (Pro Account)") }
                )
            }
        },
        bottomBar = {
            if (currentDestination == ScreenDestination.TABS) {
                AppBottomNav(
                    currentTab = currentTab,
                    onTabSelected = { currentTab = it }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentDestination) {
                ScreenDestination.TABS -> {
                    when (currentTab) {
                        AppTab.CHART -> ChartScreen(
                            repository = repository,
                            onShowToast = { showToast(it) }
                        )
                        AppTab.STRATEGY -> StrategyScreen(
                            repository = repository,
                            onOpenCompare = { currentDestination = ScreenDestination.COMPARE_STRATEGIES },
                            onOpenPineEditor = { currentDestination = ScreenDestination.PINE_EDITOR },
                            onShowToast = { showToast(it) }
                        )
                        AppTab.AUTO_TRADE -> AutoTradeScreen(
                            repository = repository,
                            onOpenDeployBot = { deployBotInitialPair = "BTC/USDT" },
                            onShowToast = { showToast(it) }
                        )
                        AppTab.SCREENER -> ScreenerScreen(
                            repository = repository,
                            onOpenDeployBot = { pair -> deployBotInitialPair = pair },
                            onShowToast = { showToast(it) }
                        )
                        AppTab.MENU -> MenuScreen(
                            repository = repository,
                            onShowToast = { showToast(it) }
                        )
                    }
                }
                ScreenDestination.PINE_EDITOR -> {
                    PineEditorScreen(
                        repository = repository,
                        onBack = { currentDestination = ScreenDestination.TABS },
                        onShowToast = { showToast(it) }
                    )
                }
                ScreenDestination.COMPARE_STRATEGIES -> {
                    CompareStrategiesScreen(
                        repository = repository,
                        onBack = { currentDestination = ScreenDestination.TABS },
                        onSelectStrategy = { strat ->
                            currentDestination = ScreenDestination.TABS
                            currentTab = AppTab.CHART
                        },
                        onDeployStrategy = { strat ->
                            currentDestination = ScreenDestination.TABS
                            currentTab = AppTab.AUTO_TRADE
                        },
                        onShowToast = { showToast(it) }
                    )
                }
            }

            // Global Toast Notification
            AnimatedVisibility(
                visible = toastMessage != null,
                enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { -it }),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 12.dp)
            ) {
                toastMessage?.let { msg ->
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceContainerHighest),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = SecondaryContainer,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = msg,
                                fontFamily = JetBrainsMono,
                                fontSize = 11.sp,
                                color = OnSurface
                            )
                        }
                    }
                }
            }

            // Deploy Bot Modal Sheet
            if (deployBotInitialPair != null) {
                ModalBottomSheet(
                    onDismissRequest = { deployBotInitialPair = null },
                    containerColor = SurfaceContainerLow,
                    dragHandle = { BottomSheetDefaults.DragHandle(color = OutlineVariant) }
                ) {
                    DeployBotSheet(
                        initialPair = deployBotInitialPair ?: "BTC/USDT",
                        repository = repository,
                        onDismiss = { deployBotInitialPair = null },
                        onShowToast = { showToast(it) }
                    )
                }
            }
        }
    }
}
