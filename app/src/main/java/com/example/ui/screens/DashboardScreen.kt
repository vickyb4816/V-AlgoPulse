package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MarketRepository
import com.example.model.PerpetualTicker
import com.example.model.PriceMovement
import com.example.ui.components.SparklineChart
import com.example.ui.theme.*
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    repository: MarketRepository,
    onNavigateToChart: (String) -> Unit,
    onOpenDeployBot: (String) -> Unit,
    onShowToast: (String) -> Unit
) {
    val tickers by repository.perpetualTickers.collectAsState()
    val marketSummary by repository.perpMarketSummary.collectAsState()
    val activePosition by repository.activePosition.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedSortBy by remember { mutableStateOf("Volume") }
    var selectedMarginType by remember { mutableStateOf("USDT-M") }

    val categories = listOf("All", "Favorites ★", "Layer 1", "DeFi", "AI & Memes", "Top Gainers")
    val sortOptions = listOf("Volume", "Change %", "Funding", "Open Interest")

    // Filter and Sort Tickers
    val filteredTickers = remember(tickers, searchQuery, selectedCategory, selectedSortBy) {
        tickers.filter { ticker ->
            val matchesSearch = ticker.symbol.contains(searchQuery, ignoreCase = true) ||
                    ticker.baseAsset.contains(searchQuery, ignoreCase = true) ||
                    ticker.displayName.contains(searchQuery, ignoreCase = true)

            val matchesCategory = when (selectedCategory) {
                "All" -> true
                "Favorites ★" -> ticker.isFavorite
                "Top Gainers" -> ticker.priceChangePercent24h > 4.0
                else -> ticker.category.equals(selectedCategory, ignoreCase = true)
            }

            matchesSearch && matchesCategory
        }.sortedWith { a, b ->
            when (selectedSortBy) {
                "Volume" -> b.volume24hUsd.compareTo(a.volume24hUsd)
                "Change %" -> b.priceChangePercent24h.compareTo(a.priceChangePercent24h)
                "Funding" -> b.fundingRatePercent.compareTo(a.fundingRatePercent)
                "Open Interest" -> b.openInterestUsd.compareTo(a.openInterestUsd)
                else -> b.volume24hUsd.compareTo(a.volume24hUsd)
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
            .padding(horizontal = 12.dp)
            .testTag("dashboard_screen_content"),
        contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. Global Perpetual Futures Telemetry Bar
        item {
            MarketSummaryStrip(
                summary = marketSummary,
                onShowToast = onShowToast
            )
        }

        // 2. Active Perpetual Position Banner (if open)
        if (activePosition != null) {
            item {
                ActivePositionBanner(
                    position = activePosition!!,
                    onFlip = {
                        repository.reversePosition()
                        onShowToast("Reversed position direction")
                    },
                    onClose = {
                        repository.closeActivePosition()
                        onShowToast("Closed active perpetual position")
                    },
                    onNavigateToChart = { onNavigateToChart(activePosition!!.pair.replace(" Perp", "")) }
                )
            }
        }

        // 3. Flagship Perpetual Contracts Showcase Carousel
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Primary)
                        )
                        Text(
                            text = "FLAGSHIP PERPETUALS",
                            fontFamily = JetBrainsMono,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnSurfaceVariant,
                            letterSpacing = 1.sp
                        )
                    }

                    Text(
                        text = "Real-Time 1.2s Websocket Tick",
                        fontFamily = JetBrainsMono,
                        fontSize = 10.sp,
                        color = Primary.copy(alpha = 0.8f)
                    )
                }

                val flagshipSymbols = listOf("BTC/USDT", "ETH/USDT", "SOL/USDT")
                val flagshipTickers = tickers.filter { it.symbol in flagshipSymbols }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(flagshipTickers, key = { it.symbol }) { ticker ->
                        FlagshipPerpCard(
                            ticker = ticker,
                            onClick = { onNavigateToChart(ticker.symbol) },
                            onDeploy = { onOpenDeployBot(ticker.symbol) }
                        )
                    }
                }
            }
        }

        // 4. Search & Controls Bar
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Search Input
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = "Search perpetual futures (e.g. BTC, SOL, SUI)...",
                            fontSize = 12.sp,
                            color = OnSurfaceVariant
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = OnSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = OnSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceContainerLowest,
                        unfocusedContainerColor = SurfaceContainerLowest,
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = OutlineVariant
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("dashboard_search_input")
                )

                // Margin Type Selector (USDT-M vs COIN-M)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .background(SurfaceContainerLowest, RoundedCornerShape(8.dp))
                            .padding(2.dp)
                    ) {
                        listOf("USDT-M", "COIN-M").forEach { type ->
                            val isSelected = selectedMarginType == type
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) SurfaceContainerHigh else Color.Transparent)
                                    .clickable { selectedMarginType = type }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = type,
                                    fontFamily = JetBrainsMono,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Primary else OnSurfaceVariant
                                )
                            }
                        }
                    }

                    // Sort By Dropdown Pills
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Sort:",
                            fontFamily = JetBrainsMono,
                            fontSize = 10.sp,
                            color = OnSurfaceVariant
                        )
                        sortOptions.take(2).forEach { sort ->
                            val isSelected = selectedSortBy == sort
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) Primary.copy(alpha = 0.15f) else SurfaceContainerLowest)
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) Primary else Color.Transparent,
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .clickable { selectedSortBy = sort }
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = sort,
                                    fontFamily = JetBrainsMono,
                                    fontSize = 10.sp,
                                    color = if (isSelected) Primary else OnSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Category Filter Chips Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { category ->
                        val isSelected = selectedCategory == category
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = category },
                            label = {
                                Text(
                                    text = category,
                                    fontSize = 11.sp,
                                    fontFamily = SpaceGrotesk,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryContainer.copy(alpha = 0.18f),
                                selectedLabelColor = Primary,
                                containerColor = SurfaceContainerLowest,
                                labelColor = OnSurfaceVariant
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) Primary else OutlineVariant
                            ),
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("category_chip_${category.lowercase().replace(" ", "_")}")
                        )
                    }
                }
            }
        }

        // 5. Perpetual Tickers Table Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CONTRACT / VOL",
                    fontFamily = JetBrainsMono,
                    fontSize = 10.sp,
                    color = OnSurfaceVariant,
                    modifier = Modifier.weight(1.3f)
                )
                Text(
                    text = "LAST PRICE / 24H",
                    fontFamily = JetBrainsMono,
                    fontSize = 10.sp,
                    color = OnSurfaceVariant,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1.2f)
                )
                Text(
                    text = "FUNDING / OI",
                    fontFamily = JetBrainsMono,
                    fontSize = 10.sp,
                    color = OnSurfaceVariant,
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 6. Real-Time Perpetual Futures Ticker List
        if (filteredTickers.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterAltOff,
                            contentDescription = null,
                            tint = OnSurfaceVariant,
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = "No perpetual contracts match your criteria",
                            fontSize = 13.sp,
                            color = OnSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                        TextButton(onClick = {
                            searchQuery = ""
                            selectedCategory = "All"
                        }) {
                            Text("Reset Filters", color = Primary)
                        }
                    }
                }
            }
        } else {
            items(filteredTickers, key = { it.symbol }) { ticker ->
                PerpetualTickerRow(
                    ticker = ticker,
                    onToggleFavorite = { repository.togglePerpFavorite(ticker.symbol) },
                    onNavigateToChart = { onNavigateToChart(ticker.symbol) },
                    onOpenDeployBot = { onOpenDeployBot(ticker.symbol) }
                )
            }
        }

        // 7. Live Perpetual Telemetry & Liquidation Stream Tape
        item {
            PerpLiquidationTapeCard(onShowToast = onShowToast)
        }
    }
}

// ---------------------------------------------------------
// Subcomponents
// ---------------------------------------------------------

@Composable
fun MarketSummaryStrip(
    summary: com.example.model.PerpMarketSummary,
    onShowToast: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("market_summary_strip"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(
            listOf(Primary.copy(alpha = 0.25f), Color.Transparent)
        ))
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Sensors,
                        contentDescription = "Live",
                        tint = Primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "PERPETUAL FUTURES MARKET TELEMETRY",
                        fontFamily = JetBrainsMono,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface
                    )
                }

                // Fear & Greed pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Primary.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${summary.fearGreedIndex} • ${summary.fearGreedLabel}",
                        fontFamily = JetBrainsMono,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                }
            }

            // Metric 4-Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 24h Perp Volume
                MetricCell(
                    label = "24h Perp Vol",
                    value = "$72.84B",
                    subtext = "+8.4% 24h",
                    isPositive = true
                )

                // Open Interest
                MetricCell(
                    label = "Open Interest",
                    value = "$18.42B",
                    subtext = "+4.2% 24h",
                    isPositive = true
                )

                // 24h Liquidations
                MetricCell(
                    label = "24h Liq Total",
                    value = "$128.4M",
                    subtext = "L $74M / S $54M",
                    isPositive = null
                )

                // Avg Funding
                MetricCell(
                    label = "Avg Funding",
                    value = "+0.0094%",
                    subtext = "Next: 03h 41m",
                    isPositive = true
                )
            }
        }
    }
}

@Composable
fun MetricCell(
    label: String,
    value: String,
    subtext: String,
    isPositive: Boolean?
) {
    Column {
        Text(
            text = label,
            fontFamily = JetBrainsMono,
            fontSize = 9.sp,
            color = OnSurfaceVariant
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontFamily = JetBrainsMono,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = OnSurface
        )
        Text(
            text = subtext,
            fontFamily = JetBrainsMono,
            fontSize = 9.sp,
            color = when (isPositive) {
                true -> Primary
                false -> Error
                null -> OnSurfaceVariant
            }
        )
    }
}

@Composable
fun ActivePositionBanner(
    position: com.example.model.ActivePosition,
    onFlip: () -> Unit,
    onClose: () -> Unit,
    onNavigateToChart: () -> Unit
) {
    val isProfit = position.unrealizedPnl >= 0
    val pnlColor = if (isProfit) Primary else Error

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("active_position_banner"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerHigh),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(
            listOf(pnlColor.copy(alpha = 0.5f), Color.Transparent)
        ))
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (position.isLong) Primary.copy(alpha = 0.2f) else Error.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (position.isLong) "LONG ${position.leverage}x" else "SHORT ${position.leverage}x",
                            fontFamily = JetBrainsMono,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (position.isLong) Primary else Error
                        )
                    }

                    Text(
                        text = position.pair,
                        fontFamily = SpaceGrotesk,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface
                    )
                }

                Text(
                    text = "${if (isProfit) "+" else ""}$${String.format(Locale.US, "%.2f", position.unrealizedPnl)} (${if (isProfit) "+" else ""}${String.format(Locale.US, "%.2f", position.roePercent)}%)",
                    fontFamily = JetBrainsMono,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = pnlColor
                )
            }

            // Entry / Mark / Est Liq
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Entry: $${String.format(Locale.US, "%.1f", position.entryPrice)}",
                    fontFamily = JetBrainsMono,
                    fontSize = 10.sp,
                    color = OnSurfaceVariant
                )
                Text(
                    text = "Mark: $${String.format(Locale.US, "%.1f", position.markPrice)}",
                    fontFamily = JetBrainsMono,
                    fontSize = 10.sp,
                    color = OnSurface
                )
                Text(
                    text = "Est Liq: $${String.format(Locale.US, "%.1f", position.estLiqPrice)}",
                    fontFamily = JetBrainsMono,
                    fontSize = 10.sp,
                    color = Error
                )
            }

            // Quick actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onNavigateToChart,
                    modifier = Modifier.weight(1f).height(32.dp),
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainerHighest),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Chart", fontSize = 11.sp, color = OnSurface)
                }

                Button(
                    onClick = onFlip,
                    modifier = Modifier.weight(1f).height(32.dp),
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceBright),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Flip Direction", fontSize = 11.sp, color = OnSurface)
                }

                Button(
                    onClick = onClose,
                    modifier = Modifier.weight(1f).height(32.dp),
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Error.copy(alpha = 0.2f)),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Close", fontSize = 11.sp, color = Error, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun FlagshipPerpCard(
    ticker: PerpetualTicker,
    onClick: () -> Unit,
    onDeploy: () -> Unit
) {
    val isPositive = ticker.priceChangePercent24h >= 0
    val trendColor = if (isPositive) Primary else Error

    // Dynamic Flash on live tick
    val flashColor by animateColorAsState(
        targetValue = when (ticker.priceMovement) {
            PriceMovement.UP -> Primary.copy(alpha = 0.28f)
            PriceMovement.DOWN -> Error.copy(alpha = 0.28f)
            PriceMovement.STABLE -> SurfaceContainerLowest
        },
        animationSpec = tween(durationMillis = 350),
        label = "flashColor"
    )

    Card(
        modifier = Modifier
            .width(220.dp)
            .clickable(onClick = onClick)
            .testTag("flagship_perp_${ticker.symbol.replace("/", "_")}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = flashColor),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                listOf(
                    trendColor.copy(alpha = 0.4f),
                    OutlineVariant.copy(alpha = 0.2f)
                )
            )
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header: Symbol + Leverage badge
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
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(
                                when (ticker.baseAsset) {
                                    "BTC" -> Color(0xFFF7931A).copy(alpha = 0.2f)
                                    "ETH" -> Color(0xFF627EEA).copy(alpha = 0.2f)
                                    else -> Primary.copy(alpha = 0.2f)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = ticker.baseAsset.take(1),
                            fontFamily = JetBrainsMono,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (ticker.baseAsset) {
                                "BTC" -> Color(0xFFF7931A)
                                "ETH" -> Color(0xFF8A9EF5)
                                else -> Primary
                            }
                        )
                    }

                    Column {
                        Text(
                            text = ticker.symbol,
                            fontFamily = SpaceGrotesk,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnSurface
                        )
                        Text(
                            text = "PERP • ${ticker.maxLeverage}x",
                            fontFamily = JetBrainsMono,
                            fontSize = 9.sp,
                            color = OnSurfaceVariant
                        )
                    }
                }

                // 24h Change Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(trendColor.copy(alpha = 0.15f))
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${if (isPositive) "+" else ""}${String.format(Locale.US, "%.2f", ticker.priceChangePercent24h)}%",
                        fontFamily = JetBrainsMono,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = trendColor
                    )
                }
            }

            // Price & Sparkline
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = formatPrice(ticker.lastPrice),
                        fontFamily = JetBrainsMono,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface
                    )
                    Text(
                        text = "Mark: ${formatPrice(ticker.markPrice)}",
                        fontFamily = JetBrainsMono,
                        fontSize = 9.sp,
                        color = OnSurfaceVariant
                    )
                }

                Box(modifier = Modifier.size(width = 64.dp, height = 28.dp)) {
                    SparklineChart(
                        points = ticker.sparklinePoints,
                        lineColor = trendColor,
                        fillColor = trendColor.copy(alpha = 0.12f),
                        strokeWidth = 2f
                    )
                }
            }

            // Stats: Funding & 24h Vol
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Funding: +${String.format(Locale.US, "%.4f", ticker.fundingRatePercent)}%",
                    fontFamily = JetBrainsMono,
                    fontSize = 9.sp,
                    color = Primary
                )
                Text(
                    text = "Vol: $${formatVolume(ticker.volume24hUsd)}",
                    fontFamily = JetBrainsMono,
                    fontSize = 9.sp,
                    color = OnSurfaceVariant
                )
            }

            // Quick Actions: Trade & Bot
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = onClick,
                    modifier = Modifier.weight(1f).height(28.dp),
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainerHighest),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Trade", fontSize = 10.sp, color = OnSurface, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = onDeploy,
                    modifier = Modifier.weight(1f).height(28.dp),
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary.copy(alpha = 0.2f)),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Bot", fontSize = 10.sp, color = Primary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PerpetualTickerRow(
    ticker: PerpetualTicker,
    onToggleFavorite: () -> Unit,
    onNavigateToChart: () -> Unit,
    onOpenDeployBot: () -> Unit
) {
    val isPositive = ticker.priceChangePercent24h >= 0
    val trendColor = if (isPositive) Primary else Error

    // Live tick animation flash background
    val animatedBg by animateColorAsState(
        targetValue = when (ticker.priceMovement) {
            PriceMovement.UP -> Primary.copy(alpha = 0.18f)
            PriceMovement.DOWN -> Error.copy(alpha = 0.18f)
            PriceMovement.STABLE -> SurfaceContainerLowest
        },
        animationSpec = tween(durationMillis = 350),
        label = "rowBgFlash"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onNavigateToChart)
            .testTag("ticker_row_${ticker.symbol.replace("/", "_")}"),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = animatedBg),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(
                listOf(
                    when (ticker.priceMovement) {
                        PriceMovement.UP -> Primary.copy(alpha = 0.6f)
                        PriceMovement.DOWN -> Error.copy(alpha = 0.6f)
                        PriceMovement.STABLE -> Color.White.copy(alpha = 0.05f)
                    },
                    Color.Transparent
                )
            )
        )
    ) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Top Main Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 1. Symbol & Favorite & Leverage
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1.3f)
                ) {
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier.size(24.dp).testTag("fav_button_${ticker.symbol}")
                    ) {
                        Icon(
                            imageVector = if (ticker.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Favorite",
                            tint = if (ticker.isFavorite) Color(0xFFFFD700) else OnSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = ticker.symbol,
                                fontFamily = SpaceGrotesk,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnSurface
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(SurfaceContainerHighest)
                                    .padding(horizontal = 3.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "${ticker.maxLeverage}x",
                                    fontFamily = JetBrainsMono,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Primary
                                )
                            }
                        }

                        Text(
                            text = "Vol $${formatVolume(ticker.volume24hUsd)}",
                            fontFamily = JetBrainsMono,
                            fontSize = 9.sp,
                            color = OnSurfaceVariant
                        )
                    }
                }

                // 2. Last Price & 24h Change
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.weight(1.2f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        if (ticker.priceMovement != PriceMovement.STABLE) {
                            Icon(
                                imageVector = if (ticker.priceMovement == PriceMovement.UP) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                                contentDescription = null,
                                tint = if (ticker.priceMovement == PriceMovement.UP) Primary else Error,
                                modifier = Modifier.size(12.dp)
                            )
                        }

                        Text(
                            text = formatPrice(ticker.lastPrice),
                            fontFamily = JetBrainsMono,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (ticker.priceMovement) {
                                PriceMovement.UP -> Primary
                                PriceMovement.DOWN -> Error
                                PriceMovement.STABLE -> OnSurface
                            }
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(trendColor.copy(alpha = 0.15f))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "${if (isPositive) "+" else ""}${String.format(Locale.US, "%.2f", ticker.priceChangePercent24h)}%",
                            fontFamily = JetBrainsMono,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = trendColor
                        )
                    }
                }

                // 3. Funding & Sparkline
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "${if (ticker.fundingRatePercent >= 0) "+" else ""}${String.format(Locale.US, "%.4f", ticker.fundingRatePercent)}%",
                        fontFamily = JetBrainsMono,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (ticker.fundingRatePercent >= 0) Primary else Error
                    )

                    Box(modifier = Modifier.size(width = 54.dp, height = 18.dp)) {
                        SparklineChart(
                            points = ticker.sparklinePoints,
                            lineColor = trendColor,
                            fillColor = trendColor.copy(alpha = 0.12f),
                            strokeWidth = 1.8f
                        )
                    }
                }
            }

            // Bottom Sub-Row: Long/Short Sentiment Ratio & Quick Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Long / Short Bar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "${ticker.longRatioPercent.toInt()}% L",
                        fontFamily = JetBrainsMono,
                        fontSize = 8.sp,
                        color = Primary
                    )

                    // Dual Color Progress Bar
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Error.copy(alpha = 0.6f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(fraction = (ticker.longRatioPercent / 100f).toFloat())
                                .background(Primary)
                        )
                    }

                    Text(
                        text = "${ticker.shortRatioPercent.toInt()}% S",
                        fontFamily = JetBrainsMono,
                        fontSize = 8.sp,
                        color = Error
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Action Pills
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Button(
                        onClick = onNavigateToChart,
                        modifier = Modifier.height(26.dp),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainerHigh),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Text("Chart", fontSize = 9.sp, color = OnSurface)
                    }

                    Button(
                        onClick = onOpenDeployBot,
                        modifier = Modifier.height(26.dp),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary.copy(alpha = 0.2f)),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Text("Deploy Bot", fontSize = 9.sp, color = Primary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun PerpLiquidationTapeCard(onShowToast: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("liquidation_tape_card"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Error)
                    )
                    Text(
                        text = "LIVE DERIVATIVES TELEMETRY & LIQUIDATIONS",
                        fontFamily = JetBrainsMono,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnSurfaceVariant
                    )
                }

                Text(
                    text = "Latency: 18ms",
                    fontFamily = JetBrainsMono,
                    fontSize = 9.sp,
                    color = Primary
                )
            }

            listOf(
                Triple("BTC/USDT Perp", "Short $240,000 Liquidated @ $67,420", Error),
                Triple("SOL/USDT Perp", "Whale Buy 12,000 Contracts ($1.77M)", Primary),
                Triple("ETH/USDT Perp", "Long $180,000 Liquidated @ $3,450", Error),
                Triple("NEAR/USDT Perp", "Breakout Vol Spike (+310% Delta)", Primary)
            ).forEach { (pair, event, color) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(SurfaceContainerLow)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = pair,
                        fontFamily = JetBrainsMono,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface
                    )
                    Text(
                        text = event,
                        fontFamily = JetBrainsMono,
                        fontSize = 9.sp,
                        color = color
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------
// Price & Volume Formatters
// ---------------------------------------------------------

private fun formatPrice(price: Double): String {
    return when {
        price >= 1000.0 -> String.format(Locale.US, "$%,.2f", price)
        price >= 1.0 -> String.format(Locale.US, "$%.2f", price)
        else -> String.format(Locale.US, "$%.4f", price)
    }
}

private fun formatVolume(vol: Double): String {
    return when {
        vol >= 1_000_000_000 -> String.format(Locale.US, "%.2fB", vol / 1_000_000_000)
        vol >= 1_000_000 -> String.format(Locale.US, "%.1fM", vol / 1_000_000)
        else -> String.format(Locale.US, "%.0fK", vol / 1_000)
    }
}
