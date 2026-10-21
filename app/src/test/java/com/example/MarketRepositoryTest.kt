package com.example

import com.example.data.MarketRepository
import com.example.model.TradeBot
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class MarketRepositoryTest {

    private lateinit var repository: MarketRepository

    @Before
    fun setUp() {
        repository = MarketRepository()
    }

    @Test
    fun testInitialPriceAndPair() {
        assertEquals("BTC/USDT", repository.selectedPair.value)
        assertEquals("15m", repository.selectedTimeframe.value)
        assertTrue(repository.currentPrice.value > 0)
    }

    @Test
    fun testDeployBotAndStopBot() {
        val testBot = TradeBot(
            id = "test_bot_1",
            pair = "SOL/USDT",
            strategyName = "Supertrend Scalper",
            leverage = 20,
            isLong = true,
            isActive = true,
            isScanning = false,
            timeframe = "5m",
            sizePercent = 10,
            sizeUsd = 1000.0,
            stopLossPercent = 1.5,
            trailingStopPercent = 0.8,
            entryPrice = 145.0,
            markPrice = 148.0,
            targetPrice = 155.0,
            currentPnlUsd = 30.0,
            currentPnlPercent = 3.0,
            progressPercent = 0.5f,
            statusMessage = "RUNNING"
        )

        repository.deployBot(testBot)
        val bots = repository.deployedBots.value
        assertTrue(bots.any { it.id == "test_bot_1" })

        repository.stopBot("test_bot_1")
        val stoppedBot = repository.deployedBots.value.find { it.id == "test_bot_1" }
        assertNotNull(stoppedBot)
        assertFalse(stoppedBot!!.isActive)
        assertEquals("HALTED", stoppedBot.statusMessage)
    }

    @Test
    fun testKillAllBots() {
        repository.killAllBots()
        val bots = repository.deployedBots.value
        assertTrue(bots.all { !it.isActive && it.statusMessage == "KILLED" })
        assertNull(repository.activePosition.value)
    }

    @Test
    fun testPositionReverseAndClose() {
        assertNotNull(repository.activePosition.value)
        val initialIsLong = repository.activePosition.value!!.isLong

        repository.reversePosition()
        assertEquals(!initialIsLong, repository.activePosition.value!!.isLong)

        repository.closeActivePosition()
        assertNull(repository.activePosition.value)
    }

    @Test
    fun testRadarCoinToggle() {
        val initialCoins = repository.radarCoins.value
        val firstCoin = initialCoins.first()
        val initialChecked = firstCoin.isChecked

        repository.toggleCoinCheck(firstCoin.symbol)
        val updatedCoin = repository.radarCoins.value.first { it.symbol == firstCoin.symbol }
        assertEquals(!initialChecked, updatedCoin.isChecked)

        repository.setAllCoinCheck(true)
        assertTrue(repository.radarCoins.value.all { it.isChecked })
    }

    @Test
    fun testPerpetualTickersPresenceAndFavorite() {
        val tickers = repository.perpetualTickers.value
        assertTrue(tickers.isNotEmpty())
        val btcTicker = tickers.find { it.symbol == "BTC/USDT" }
        assertNotNull(btcTicker)
        assertTrue(btcTicker!!.lastPrice > 0)

        val initialFav = btcTicker.isFavorite
        repository.togglePerpFavorite("BTC/USDT")
        val updatedFav = repository.perpetualTickers.value.find { it.symbol == "BTC/USDT" }!!.isFavorite
        assertEquals(!initialFav, updatedFav)
    }

    @Test
    fun testSetPairSyncsPerpPrice() {
        repository.setPair("SOL/USDT")
        assertEquals("SOL/USDT", repository.selectedPair.value)
        val solTicker = repository.perpetualTickers.value.find { it.symbol == "SOL/USDT" }
        assertNotNull(solTicker)
        assertEquals(solTicker!!.lastPrice, repository.currentPrice.value, 0.001)
    }

    @Test
    fun testPineScriptUpdate() {
        val testScript = "//@version=5\nstrategy(\"Custom RSI Scalp\")\n"
        repository.updatePineScript(testScript)
        assertEquals(testScript, repository.pineScriptSource.value)
    }
}
