package com.example.data

import com.example.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

class MarketRepository {

    private val repositoryScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    // Perpetual Futures Global Market Summary
    private val _perpMarketSummary = MutableStateFlow(PerpMarketSummary())
    val perpMarketSummary: StateFlow<PerpMarketSummary> = _perpMarketSummary.asStateFlow()

    // Real-Time Perpetual Futures Tickers
    private val _perpetualTickers = MutableStateFlow<List<PerpetualTicker>>(
        listOf(
            PerpetualTicker(
                symbol = "BTC/USDT",
                displayName = "BTC/USDT Perp",
                baseAsset = "BTC",
                quoteAsset = "USDT",
                category = "Layer 1",
                markPrice = 67421.26,
                indexPrice = 67418.50,
                lastPrice = 67420.80,
                priceChange24h = 2480.50,
                priceChangePercent24h = 3.82,
                high24h = 68140.00,
                low24h = 64920.00,
                volume24hUsd = 28450000000.0,
                volume24hContracts = 421000,
                fundingRatePercent = 0.0100,
                nextFundingSeconds = 14200,
                openInterestUsd = 4850000000.0,
                openInterestDeltaPercent = 4.2,
                longRatioPercent = 58.4,
                shortRatioPercent = 41.6,
                sparklinePoints = listOf(65200f, 65800f, 66100f, 65900f, 66800f, 67200f, 67420f),
                maxLeverage = 100,
                isFavorite = true
            ),
            PerpetualTicker(
                symbol = "ETH/USDT",
                displayName = "ETH/USDT Perp",
                baseAsset = "ETH",
                quoteAsset = "USDT",
                category = "Layer 1",
                markPrice = 3482.40,
                indexPrice = 3481.90,
                lastPrice = 3482.10,
                priceChange24h = 138.80,
                priceChangePercent24h = 4.15,
                high24h = 3540.00,
                low24h = 3320.00,
                volume24hUsd = 14200000000.0,
                volume24hContracts = 4080000,
                fundingRatePercent = 0.0085,
                nextFundingSeconds = 14200,
                openInterestUsd = 2920000000.0,
                openInterestDeltaPercent = 2.8,
                longRatioPercent = 54.2,
                shortRatioPercent = 45.8,
                sparklinePoints = listOf(3340f, 3380f, 3410f, 3390f, 3450f, 3470f, 3482f),
                maxLeverage = 75,
                isFavorite = true
            ),
            PerpetualTicker(
                symbol = "SOL/USDT",
                displayName = "SOL/USDT Perp",
                baseAsset = "SOL",
                quoteAsset = "USDT",
                category = "Layer 1",
                markPrice = 148.20,
                indexPrice = 148.15,
                lastPrice = 148.25,
                priceChange24h = 10.75,
                priceChangePercent24h = 7.82,
                high24h = 152.40,
                low24h = 136.80,
                volume24hUsd = 6840000000.0,
                volume24hContracts = 46100000,
                fundingRatePercent = 0.0125,
                nextFundingSeconds = 14200,
                openInterestUsd = 1420000000.0,
                openInterestDeltaPercent = 8.4,
                longRatioPercent = 64.1,
                shortRatioPercent = 35.9,
                sparklinePoints = listOf(137f, 139f, 142f, 141f, 145f, 147f, 148.2f),
                maxLeverage = 50,
                isFavorite = true
            ),
            PerpetualTicker(
                symbol = "NEAR/USDT",
                displayName = "NEAR/USDT Perp",
                baseAsset = "NEAR",
                quoteAsset = "USDT",
                category = "Layer 1",
                markPrice = 6.42,
                indexPrice = 6.418,
                lastPrice = 6.421,
                priceChange24h = 0.708,
                priceChangePercent24h = 12.40,
                high24h = 6.68,
                low24h = 5.65,
                volume24hUsd = 840000000.0,
                volume24hContracts = 130000000,
                fundingRatePercent = 0.0150,
                nextFundingSeconds = 14200,
                openInterestUsd = 310000000.0,
                openInterestDeltaPercent = 14.2,
                longRatioPercent = 68.0,
                shortRatioPercent = 32.0,
                sparklinePoints = listOf(5.7f, 5.9f, 6.1f, 6.0f, 6.3f, 6.4f, 6.42f),
                maxLeverage = 20,
                isFavorite = false
            ),
            PerpetualTicker(
                symbol = "AVAX/USDT",
                displayName = "AVAX/USDT Perp",
                baseAsset = "AVAX",
                quoteAsset = "USDT",
                category = "Layer 1",
                markPrice = 28.90,
                indexPrice = 28.92,
                lastPrice = 28.88,
                priceChange24h = -1.26,
                priceChangePercent24h = -4.20,
                high24h = 30.80,
                low24h = 28.40,
                volume24hUsd = 620000000.0,
                volume24hContracts = 21400000,
                fundingRatePercent = -0.0050,
                nextFundingSeconds = 14200,
                openInterestUsd = 195000000.0,
                openInterestDeltaPercent = -3.1,
                longRatioPercent = 44.5,
                shortRatioPercent = 55.5,
                sparklinePoints = listOf(30.5f, 30.2f, 29.8f, 29.5f, 29.1f, 28.7f, 28.9f),
                maxLeverage = 25,
                isFavorite = false
            ),
            PerpetualTicker(
                symbol = "SUI/USDT",
                displayName = "SUI/USDT Perp",
                baseAsset = "SUI",
                quoteAsset = "USDT",
                category = "Layer 1",
                markPrice = 1.92,
                indexPrice = 1.918,
                lastPrice = 1.922,
                priceChange24h = 0.168,
                priceChangePercent24h = 9.60,
                high24h = 1.98,
                low24h = 1.74,
                volume24hUsd = 1120000000.0,
                volume24hContracts = 583000000,
                fundingRatePercent = 0.0110,
                nextFundingSeconds = 14200,
                openInterestUsd = 420000000.0,
                openInterestDeltaPercent = 11.5,
                longRatioPercent = 61.2,
                shortRatioPercent = 38.8,
                sparklinePoints = listOf(1.75f, 1.78f, 1.82f, 1.85f, 1.89f, 1.91f, 1.92f),
                maxLeverage = 20,
                isFavorite = true
            ),
            PerpetualTicker(
                symbol = "DOGE/USDT",
                displayName = "DOGE/USDT Perp",
                baseAsset = "DOGE",
                quoteAsset = "USDT",
                category = "AI & Memes",
                markPrice = 0.1420,
                indexPrice = 0.1419,
                lastPrice = 0.1421,
                priceChange24h = 0.0069,
                priceChangePercent24h = 5.10,
                high24h = 0.1485,
                low24h = 0.1340,
                volume24hUsd = 1450000000.0,
                volume24hContracts = 10200000000L,
                fundingRatePercent = 0.0090,
                nextFundingSeconds = 14200,
                openInterestUsd = 380000000.0,
                openInterestDeltaPercent = 6.2,
                longRatioPercent = 56.0,
                shortRatioPercent = 44.0,
                sparklinePoints = listOf(0.135f, 0.137f, 0.139f, 0.138f, 0.141f, 0.143f, 0.142f),
                maxLeverage = 25,
                isFavorite = false
            ),
            PerpetualTicker(
                symbol = "BNB/USDT",
                displayName = "BNB/USDT Perp",
                baseAsset = "BNB",
                quoteAsset = "USDT",
                category = "Layer 1",
                markPrice = 586.50,
                indexPrice = 586.40,
                lastPrice = 586.60,
                priceChange24h = 12.05,
                priceChangePercent24h = 2.10,
                high24h = 594.00,
                low24h = 572.00,
                volume24hUsd = 2150000000.0,
                volume24hContracts = 3660000,
                fundingRatePercent = 0.0075,
                nextFundingSeconds = 14200,
                openInterestUsd = 590000000.0,
                openInterestDeltaPercent = 1.9,
                longRatioPercent = 53.0,
                shortRatioPercent = 47.0,
                sparklinePoints = listOf(574f, 578f, 581f, 580f, 584f, 585f, 586.5f),
                maxLeverage = 50,
                isFavorite = false
            ),
            PerpetualTicker(
                symbol = "ARB/USDT",
                displayName = "ARB/USDT Perp",
                baseAsset = "ARB",
                quoteAsset = "USDT",
                category = "Infrastructure",
                markPrice = 0.6240,
                indexPrice = 0.6242,
                lastPrice = 0.6238,
                priceChange24h = -0.0092,
                priceChangePercent24h = -1.45,
                high24h = 0.6550,
                low24h = 0.6120,
                volume24hUsd = 410000000.0,
                volume24hContracts = 657000000,
                fundingRatePercent = -0.0020,
                nextFundingSeconds = 14200,
                openInterestUsd = 145000000.0,
                openInterestDeltaPercent = -0.8,
                longRatioPercent = 48.0,
                shortRatioPercent = 52.0,
                sparklinePoints = listOf(0.635f, 0.632f, 0.628f, 0.630f, 0.626f, 0.622f, 0.624f),
                maxLeverage = 20,
                isFavorite = false
            ),
            PerpetualTicker(
                symbol = "LINK/USDT",
                displayName = "LINK/USDT Perp",
                baseAsset = "LINK",
                quoteAsset = "USDT",
                category = "DeFi",
                markPrice = 11.85,
                indexPrice = 11.84,
                lastPrice = 11.86,
                priceChange24h = 0.367,
                priceChangePercent24h = 3.20,
                high24h = 12.15,
                low24h = 11.40,
                volume24hUsd = 540000000.0,
                volume24hContracts = 45500000,
                fundingRatePercent = 0.0080,
                nextFundingSeconds = 14200,
                openInterestUsd = 210000000.0,
                openInterestDeltaPercent = 4.1,
                longRatioPercent = 57.5,
                shortRatioPercent = 42.5,
                sparklinePoints = listOf(11.45f, 11.55f, 11.68f, 11.62f, 11.75f, 11.82f, 11.85f),
                maxLeverage = 25,
                isFavorite = false
            ),
            PerpetualTicker(
                symbol = "RENDER/USDT",
                displayName = "RENDER/USDT Perp",
                baseAsset = "RENDER",
                quoteAsset = "USDT",
                category = "AI & Memes",
                markPrice = 5.74,
                indexPrice = 5.735,
                lastPrice = 5.745,
                priceChange24h = 0.428,
                priceChangePercent24h = 8.05,
                high24h = 5.92,
                low24h = 5.25,
                volume24hUsd = 380000000.0,
                volume24hContracts = 66100000,
                fundingRatePercent = 0.0115,
                nextFundingSeconds = 14200,
                openInterestUsd = 168000000.0,
                openInterestDeltaPercent = 9.8,
                longRatioPercent = 63.8,
                shortRatioPercent = 36.2,
                sparklinePoints = listOf(5.30f, 5.42f, 5.55f, 5.50f, 5.65f, 5.71f, 5.74f),
                maxLeverage = 20,
                isFavorite = false
            )
        )
    )
    val perpetualTickers: StateFlow<List<PerpetualTicker>> = _perpetualTickers.asStateFlow()

    // Current Market Telemetry
    private val _currentPrice = MutableStateFlow(67421.26)
    val currentPrice: StateFlow<Double> = _currentPrice.asStateFlow()

    private val _priceChangePercent = MutableStateFlow(3.84)
    val priceChangePercent: StateFlow<Double> = _priceChangePercent.asStateFlow()

    private val _selectedPair = MutableStateFlow("BTC/USDT")
    val selectedPair: StateFlow<String> = _selectedPair.asStateFlow()

    private val _selectedTimeframe = MutableStateFlow("15m")
    val selectedTimeframe: StateFlow<String> = _selectedTimeframe.asStateFlow()

    init {
        startRealtimeTickerSimulation()
    }

    private fun startRealtimeTickerSimulation() {
        repositoryScope.launch {
            while (isActive) {
                delay(1300)
                simulatePerpetualTick()
            }
        }
    }

    private fun simulatePerpetualTick() {
        val currentList = _perpetualTickers.value
        if (currentList.isEmpty()) return

        // Pick 1-2 random tickers to update
        val targetIndices = listOf(
            Random.nextInt(currentList.size),
            Random.nextInt(currentList.size)
        ).distinct()

        val updated = currentList.mapIndexed { index, ticker ->
            if (index in targetIndices) {
                // Micro price change (-0.12% to +0.14%)
                val pctChange = (Random.nextDouble(-0.0012, 0.0014))
                val priceDiff = ticker.lastPrice * pctChange
                val newPrice = kotlin.math.max(0.0001, ticker.lastPrice + priceDiff)
                val newMark = kotlin.math.max(0.0001, ticker.markPrice + (priceDiff * 0.98))
                val isUp = priceDiff >= 0
                val move = if (isUp) PriceMovement.UP else PriceMovement.DOWN
                val newSparkline = (ticker.sparklinePoints.takeLast(10) + newPrice.toFloat()).takeLast(12)
                val newChangePct = ticker.priceChangePercent24h + (pctChange * 10)

                // If selected pair, sync currentPrice
                if (ticker.symbol == _selectedPair.value) {
                    _currentPrice.value = newPrice
                    _priceChangePercent.value = newChangePct
                }

                ticker.copy(
                    lastPrice = newPrice,
                    markPrice = newMark,
                    priceMovement = move,
                    priceChangePercent24h = newChangePct,
                    sparklinePoints = newSparkline
                )
            } else {
                ticker
            }
        }
        _perpetualTickers.value = updated

        // Dim highlight flash back to STABLE after 600ms
        repositoryScope.launch {
            delay(600)
            _perpetualTickers.update { list ->
                list.map { t ->
                    if (t.priceMovement != PriceMovement.STABLE) {
                        t.copy(priceMovement = PriceMovement.STABLE)
                    } else t
                }
            }
        }
    }

    // Active Position
    private val _activePosition = MutableStateFlow<ActivePosition?>(ActivePosition())
    val activePosition: StateFlow<ActivePosition?> = _activePosition.asStateFlow()


    // Deployed Bots
    private val _deployedBots = MutableStateFlow<List<TradeBot>>(
        listOf(
            TradeBot(
                id = "bot_1",
                pair = "BTC/USDT",
                strategyName = "EMA Golden Cross + ATR",
                leverage = 10,
                isLong = true,
                isActive = true,
                isScanning = false,
                timeframe = "5m",
                sizePercent = 15,
                sizeUsd = 1500.0,
                stopLossPercent = 1.50,
                trailingStopPercent = 0.80,
                entryPrice = 65820.0,
                markPrice = 66420.0,
                targetPrice = 68123.70,
                currentPnlUsd = 128.40,
                currentPnlPercent = 8.56,
                progressPercent = 0.48f,
                statusMessage = "LONG ACTIVE"
            ),
            TradeBot(
                id = "bot_2",
                pair = "ETH/USDT",
                strategyName = "Supertrend Scalper v3",
                leverage = 5,
                isLong = true,
                isActive = true,
                isScanning = true,
                timeframe = "15m",
                sizePercent = 10,
                sizeUsd = 1000.0,
                stopLossPercent = 1.20,
                trailingStopPercent = 0.60,
                entryPrice = 3450.0,
                markPrice = 3480.0,
                targetPrice = 3620.0,
                currentPnlUsd = 0.0,
                currentPnlPercent = 0.0,
                progressPercent = 0.15f,
                rsi = 31.2,
                supertrendParam = "10 / 3.0",
                statusMessage = "SCANNING"
            )
        )
    )
    val deployedBots: StateFlow<List<TradeBot>> = _deployedBots.asStateFlow()

    // Telemetry logs
    private val _telemetryLogs = MutableStateFlow<List<TelemetryLog>>(
        listOf(
            TelemetryLog("14:22:05", "BTCUSDT", "Trailing stop moved dynamically to $66,420.00 (Locked +$128.40)"),
            TelemetryLog("14:18:10", "ETHUSDT", "RSI 14 entered oversold region (28.4). Volume delta +12.4%"),
            TelemetryLog("14:12:33", "SOLUSDT", "Volume anomaly filtered. Signal skipped due to ATR spread."),
            TelemetryLog("14:05:01", "SYS", "Demo engine synchronized with CoinDCX orderbook depth. Ping: 18ms")
        )
    )
    val telemetryLogs: StateFlow<List<TelemetryLog>> = _telemetryLogs.asStateFlow()

    // Backtest results
    private val _backtestTrades = MutableStateFlow<List<BacktestFill>>(
        listOf(
            BacktestFill(56, true, "SOL", "TARGET HIT", 142.50, 151.20, 1.42, 6.10, "14m ago"),
            BacktestFill(55, false, "SOL", "STOP LOSS", 148.10, 149.30, 1.15, -0.81, "48m ago"),
            BacktestFill(54, true, "SOL", "TRAIL EXIT", 139.80, 144.20, 1.38, 3.14, "2h 15m ago"),
            BacktestFill(53, false, "SOL", "TARGET HIT", 146.00, 141.10, 1.52, 3.35, "3h 40m ago"),
            BacktestFill(52, true, "SOL", "STOP LOSS", 143.20, 141.90, 1.20, -0.91, "5h 10m ago"),
            BacktestFill(51, true, "SOL", "TARGET HIT", 135.40, 143.80, 1.65, 6.20, "7h 25m ago"),
            BacktestFill(50, true, "SOL", "TARGET HIT", 133.10, 138.90, 1.30, 4.35, "9h 12m ago"),
            BacktestFill(49, false, "SOL", "TRAIL EXIT", 139.00, 136.20, 1.25, 2.01, "11h 05m ago")
        )
    )
    val backtestTrades: StateFlow<List<BacktestFill>> = _backtestTrades.asStateFlow()

    // Radar Screener Coins
    private val _radarCoins = MutableStateFlow<List<RadarCoin>>(
        listOf(
            RadarCoin(
                symbol = "SOLUSDT",
                namePrefix = "S",
                leverage = 20,
                vol24h = "$1.82B",
                price = 148.20,
                change24h = 7.82,
                rsi = 68.2,
                volOrAdxLabel = "Vol",
                volOrAdxValue = "+240%",
                primarySignal = "SUPERTREND BUY",
                secondarySignal = "EMA200 Above",
                sparklinePoints = listOf(20f, 18f, 12f, 15f, 6f, 9f, 2f),
                isChecked = true,
                actionType = "Launch Bot"
            ),
            RadarCoin(
                symbol = "NEARUSDT",
                namePrefix = "N",
                leverage = 10,
                vol24h = "$642M",
                price = 6.42,
                change24h = 12.40,
                rsi = 74.1,
                volOrAdxLabel = "Vol",
                volOrAdxValue = "+310%",
                primarySignal = "BB Squeeze Breakout",
                secondarySignal = "MACD Cross",
                sparklinePoints = listOf(22f, 19f, 16f, 8f, 10f, 1f),
                isChecked = true,
                actionType = "Trade"
            ),
            RadarCoin(
                symbol = "AVAXUSDT",
                namePrefix = "A",
                leverage = 20,
                vol24h = "$418M",
                price = 28.90,
                change24h = -4.20,
                rsi = 26.5,
                volOrAdxLabel = "Vol",
                volOrAdxValue = "1.2x",
                primarySignal = "OVERSOLD REBOUND",
                secondarySignal = "Support S1 Test",
                sparklinePoints = listOf(4f, 6f, 14f, 12f, 22f),
                isChecked = false,
                actionType = "Watch"
            ),
            RadarCoin(
                symbol = "SUIUSDT",
                namePrefix = "S",
                leverage = 15,
                vol24h = "$890M",
                price = 1.92,
                change24h = 9.60,
                rsi = 64.3,
                volOrAdxLabel = "ADX",
                volOrAdxValue = "32.1 Trend",
                primarySignal = "TRENDING MOMENTUM",
                secondarySignal = "Bull Flag",
                sparklinePoints = listOf(18f, 16f, 8f, 11f, 4f, 2f),
                isChecked = true,
                actionType = "Launch Bot"
            ),
            RadarCoin(
                symbol = "DOGEUSDT",
                namePrefix = "D",
                leverage = 25,
                vol24h = "$1.15B",
                price = 0.1420,
                change24h = 5.10,
                rsi = 58.0,
                volOrAdxLabel = "Vol",
                volOrAdxValue = "+88%",
                primarySignal = "Supertrend Bullish",
                secondarySignal = "Momentum Inflow",
                sparklinePoints = listOf(16f, 15f, 9f, 11f, 6f),
                isChecked = false,
                actionType = "Trade"
            )
        )
    )
    val radarCoins: StateFlow<List<RadarCoin>> = _radarCoins.asStateFlow()

    // 16 Quantitative Strategies Benchmark Matrix
    private val _strategyBenchmarks = MutableStateFlow<List<StrategyBenchmark>>(
        listOf(
            StrategyBenchmark(1, "Supertrend + EMA Pullback v2", "Trend", 56, 2.84, 142.8, 68.4, 2.41, -8.6, listOf(20f, 18f, 14f, 17f, 9f, 11f, 3f, 2f), "Zero Slippage Overrun"),
            StrategyBenchmark(2, "EMA Golden Cross + ATR Filter", "Trend", 44, 2.31, 118.2, 62.1, 2.08, -11.2, listOf(21f, 16f, 19f, 10f, 14f, 5f, 4f), "Avg Dur: 4.8 Bars"),
            StrategyBenchmark(3, "Bollinger Band Mean Reversion", "Reversion", 82, 2.15, 94.5, 71.0, 1.95, -6.4, listOf(18f, 12f, 15f, 8f, 10f, 6f, 5f), "Lowest Drawdown (-6.4%)"),
            StrategyBenchmark(4, "Keltner Channel Squeeze", "Volatility", 38, 1.98, 82.0, 58.5, 1.74, -13.1, listOf(22f, 16f, 18f, 11f, 12f, 7f), "BB/KC Ratio: 0.82"),
            StrategyBenchmark(5, "MACD Zero-Lag Histogram", "Trend", 61, 1.76, 64.3, 54.0, 1.52, -15.8, listOf(23f, 18f, 20f, 12f, 10f), "Fast 12 / Slow 26"),
            StrategyBenchmark(6, "RSI Multi-Timeframe Divergence", "Reversion", 29, 2.05, 58.9, 66.7, 1.48, -7.9, listOf(24f, 19f, 14f, 11f), "15m & 1h Confluence"),
            StrategyBenchmark(7, "VWAP Mean Deviation Rebound", "Reversion", 74, 1.88, 52.4, 61.2, 1.42, -9.2, listOf(20f, 16f, 18f, 13f, 8f), "VWAP ±2.5 StdDev"),
            StrategyBenchmark(8, "Donchian Channel High Breakout", "Volatility", 42, 1.62, 49.8, 51.5, 1.38, -14.6, listOf(22f, 17f, 19f, 12f, 9f), "20-Bar Channel Period"),
            StrategyBenchmark(9, "Supertrend Heikin-Ashi Filter", "Trend", 51, 1.92, 46.2, 64.0, 1.35, -8.1, listOf(19f, 15f, 16f, 10f, 7f), "Smoothed HA Candlesticks"),
            StrategyBenchmark(10, "Stochastic RSI Extreme Scalp", "Reversion", 95, 1.58, 41.0, 63.5, 1.29, -10.4, listOf(21f, 17f, 16f, 12f, 11f), "Fast Scalp < 15m"),
            StrategyBenchmark(11, "Volume Weighted Supertrend", "Trend", 48, 1.68, 38.5, 59.2, 1.24, -11.8, listOf(22f, 18f, 17f, 13f, 12f), "OBV Volume Confirmed"),
            StrategyBenchmark(12, "ADX Directional Trend Accelerator", "Trend", 36, 1.55, 34.1, 56.0, 1.18, -12.9, listOf(23f, 19f, 18f, 14f, 13f), "ADX > 28 Momentum Filter"),
            StrategyBenchmark(13, "Liquidation Sweep Mean Reversion", "Volatility", 33, 2.10, 29.8, 65.4, 1.14, -7.2, listOf(18f, 15f, 14f, 11f, 9f), "Order Book Depth Sweep"),
            StrategyBenchmark(14, "Parabolic SAR Trend Trailing", "Trend", 68, 1.42, 22.6, 52.8, 1.05, -16.4, listOf(24f, 21f, 19f, 16f, 15f), "Accel 0.02 / Max 0.2"),
            StrategyBenchmark(15, "Chande Momentum Oscillator", "Reversion", 40, 1.25, 8.4, 50.2, 0.88, -14.1, listOf(22f, 20f, 18f, 17f, 16f), "CMO Extreme Bounds"),
            StrategyBenchmark(16, "Hull Moving Average Scalper", "Trend", 84, 1.18, 4.2, 49.0, 0.76, -18.2, listOf(24f, 22f, 21f, 19f, 18f), "HMA 21 Zero-Lag Curve")
        )
    )
    val strategyBenchmarks: StateFlow<List<StrategyBenchmark>> = _strategyBenchmarks.asStateFlow()

    // Pine Script Source code
    private val _pineScriptSource = MutableStateFlow(
"""//@version=5
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
    alert("Volatility Surge Detected")

// End of Pine Script Logic"""
    )
    val pineScriptSource: StateFlow<String> = _pineScriptSource.asStateFlow()

    // Actions
    fun setPair(pair: String) {
        _selectedPair.value = pair
        val ticker = _perpetualTickers.value.find { it.symbol == pair || it.displayName.startsWith(pair) }
        if (ticker != null) {
            _currentPrice.value = ticker.lastPrice
            _priceChangePercent.value = ticker.priceChangePercent24h
        }
    }

    fun togglePerpFavorite(symbol: String) {
        _perpetualTickers.update { list ->
            list.map { if (it.symbol == symbol) it.copy(isFavorite = !it.isFavorite) else it }
        }
    }

    fun setTimeframe(tf: String) {
        _selectedTimeframe.value = tf
    }

    fun updatePineScript(newScript: String) {
        _pineScriptSource.value = newScript
    }

    fun toggleCoinCheck(symbol: String) {
        _radarCoins.update { list ->
            list.map { if (it.symbol == symbol) it.copy(isChecked = !it.isChecked) else it }
        }
    }

    fun setAllCoinCheck(checked: Boolean) {
        _radarCoins.update { list ->
            list.map { it.copy(isChecked = checked) }
        }
    }

    fun addTelemetryLog(tag: String, message: String) {
        val now = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.US).format(java.util.Date())
        _telemetryLogs.update { list ->
            listOf(TelemetryLog(now, tag, message)) + list.take(30)
        }
    }

    fun clearLogs() {
        _telemetryLogs.value = emptyList()
    }

    fun stopBot(botId: String) {
        _deployedBots.update { list ->
            list.map { if (it.id == botId) it.copy(isActive = false, statusMessage = "HALTED") else it }
        }
    }

    fun killAllBots() {
        _deployedBots.update { list ->
            list.map { it.copy(isActive = false, statusMessage = "KILLED") }
        }
        _activePosition.value = null
        addTelemetryLog("CRIT", "EMERGENCY KILL TRIGGER: Market close routed. Engine halted.")
    }

    fun resumeAllBots() {
        _deployedBots.update { list ->
            list.map { it.copy(isActive = true, statusMessage = if (it.isScanning) "SCANNING" else "RUNNING") }
        }
        addTelemetryLog("SYS", "All trading bots resumed successfully.")
    }

    fun pauseAllBots() {
        _deployedBots.update { list ->
            list.map { it.copy(isActive = false, statusMessage = "PAUSED") }
        }
        addTelemetryLog("SYS", "Execution paused across all strategies.")
    }

    fun reversePosition() {
        _activePosition.update { current ->
            if (current != null) {
                val newIsLong = !current.isLong
                val newPnl = -current.unrealizedPnl
                current.copy(
                    isLong = newIsLong,
                    unrealizedPnl = newPnl,
                    roePercent = -current.roePercent
                )
            } else {
                ActivePosition(isLong = false)
            }
        }
        addTelemetryLog("ORDER", "Position flipped to ${_activePosition.value?.isLong?.let { if (it) "LONG" else "SHORT" }}")
    }

    fun closeActivePosition() {
        _activePosition.value = null
        addTelemetryLog("ORDER", "Active position closed at market price $67,420.50")
    }

    fun updateTpSl(tp: Double, sl: Double, trail: Double) {
        _activePosition.update { current ->
            current?.copy(
                tpPrice = tp,
                slPrice = sl,
                trailPrice = trail
            )
        }
        addTelemetryLog("SYS", "TP set to $$tp, SL set to $$sl")
    }

    fun deployBot(bot: TradeBot) {
        _deployedBots.update { list ->
            listOf(bot) + list
        }
        addTelemetryLog("ENGINE", "Initialized new bot for ${bot.pair} (${bot.strategyName})")
    }
}
