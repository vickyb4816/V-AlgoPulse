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
    val outcomeType: String, // TARGET HIT, STOP LOSS, TRAIL EXIT
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
    val actionType: String = "Launch Bot" // "Launch Bot", "Trade", "Watch"
)

data class StrategyBenchmark(
    val rank: Int,
    val name: String,
    val category: String, // "Trend", "Reversion", "Volatility"
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
