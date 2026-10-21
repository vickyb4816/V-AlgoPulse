package com.example.model

data class Candle(
    val timestamp: Long,
    val open: Float,
    val high: Float,
    val low: Float,
    val close: Float,
    val volume: Float,
    val timeLabel: String
)

data class ActivePosition(
    val pair: String = "BTCUSDT Perp",
    val isLong: Boolean = true,
    val leverage: Int = 10,
    val unrealizedPnl: Double = 420.80,
    val roePercent: Double = 6.31,
    val sizeBtc: Double = 0.074,
    val marginUsdt: Double = 500.0,
    val isCross: Boolean = true,
    val estLiqPrice: Double = 61040.20,
    val riskPercent: Double = 14.2,
    val entryPrice: Double = 66800.0,
    val markPrice: Double = 67420.50,
    val tpPrice: Double = 69200.0,
    val slPrice: Double = 65950.0,
    val trailPrice: Double = 66300.0
)

data class TradeBot(
    val id: String,
    val pair: String,
    val strategyName: String,
    val leverage: Int,
    val isLong: Boolean,
    val isActive: Boolean,
    val isScanning: Boolean,
    val timeframe: String,
    val sizePercent: Int,
    val sizeUsd: Double,
    val stopLossPercent: Double,
    val trailingStopPercent: Double,
    val entryPrice: Double,
    val markPrice: Double,
    val targetPrice: Double,
    val currentPnlUsd: Double,
    val currentPnlPercent: Double,
    val progressPercent: Float,
    val rsi: Double = 31.2,
    val supertrendParam: String = "10 / 3.0",
    val statusMessage: String = "Running"
)

data class BacktestFill(
    val id: Int,
    val isLong: Boolean,
    val pair: String,
    val outcomeType: String,
    val entryPrice: Double,
    val exitPrice: Double,
    val feeUsd: Double,
    val returnPercent: Double,
    val timeAgo: String
)

data class RadarCoin(
    val symbol: String,
    val namePrefix: String,
    val leverage: Int,
    val vol24h: String,
    val price: Double,
    val change24h: Double,
    val rsi: Double,
    val volOrAdxLabel: String,
    val volOrAdxValue: String,
    val primarySignal: String,
    val secondarySignal: String,
    val sparklinePoints: List<Float>,
    val isChecked: Boolean = false,
    val actionType: String = "Launch Bot"
)

data class StrategyBenchmark(
    val rank: Int,
    val name: String,
    val category: String,
    val tradesCount: Int,
    val profitFactor: Double,
    val netYieldPercent: Double,
    val winRatePercent: Double,
    val sharpeRatio: Double,
    val maxDrawdownPercent: Double,
    val sparklinePoints: List<Float>,
    val badgeNote: String = "Zero Slippage Overrun"
)

data class TelemetryLog(
    val time: String,
    val tag: String,
    val message: String
)

enum class PriceMovement {
    UP, DOWN, STABLE
}

data class PerpetualTicker(
    val symbol: String,                    // e.g. "BTC/USDT"
    val displayName: String,               // e.g. "BTC/USDT Perp"
    val baseAsset: String,                 // "BTC"
    val quoteAsset: String,                // "USDT"
    val category: String,                  // "Layer 1", "DeFi", "AI & Memes", "Infrastructure"
    val markPrice: Double,                 // 67421.26
    val indexPrice: Double,                // 67418.50
    val lastPrice: Double,                 // 67420.80
    val priceChange24h: Double,            // 2480.50
    val priceChangePercent24h: Double,     // +3.82%
    val high24h: Double,                   // 68140.00
    val low24h: Double,                    // 64920.00
    val volume24hUsd: Double,              // 28450000000.0
    val volume24hContracts: Long,          // 421000
    val fundingRatePercent: Double,        // 0.0100 (+0.0100%)
    val nextFundingSeconds: Long,          // countdown seconds
    val openInterestUsd: Double,           // 4850000000.0
    val openInterestDeltaPercent: Double,  // +4.2%
    val longRatioPercent: Double,          // 58.4%
    val shortRatioPercent: Double,         // 41.6%
    val sparklinePoints: List<Float>,      // trend
    val maxLeverage: Int = 100,
    val isFavorite: Boolean = false,
    val priceMovement: PriceMovement = PriceMovement.STABLE
)

data class PerpMarketSummary(
    val total24hVolumeUsd: Double = 72840000000.0,
    val totalOpenInterestUsd: Double = 18420000000.0,
    val liquidations24hUsd: Double = 128450000.0,
    val longLiquidationsUsd: Double = 74200000.0,
    val shortLiquidationsUsd: Double = 54250000.0,
    val avgFundingRatePercent: Double = 0.0094,
    val fearGreedIndex: Int = 68,
    val fearGreedLabel: String = "Greed"
)

