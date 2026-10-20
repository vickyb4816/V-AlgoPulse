package com.example

import com.example.data.MarketRepository
import com.example.model.TradeBot
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ExampleUnitTest {
    private lateinit var repository: MarketRepository

    @Before
    fun setUp() {
        repository = MarketRepository()
    }

    @Test
    fun testInitialRepositoryState() {
        assertEquals("BTC/USDT", repository.selectedPair.value)
        assertEquals("15m", repository.selectedTimeframe.value)
        assertNotNull(repository.activePosition.value)
        assertEquals(2, repository.deployedBots.value.size)
        assertEquals(16, repository.strategyBenchmarks.value.size)
    }

    @Test
    fun testReversePosition() {
        val initialLong = repository.activePosition.value!!.isLong
        repository.reversePosition()
        val afterLong = repository.activePosition.value!!.isLong
        assertNotEquals(initialLong, afterLong)
    }

    @Test
    fun testClosePosition() {
        assertNotNull(repository.activePosition.value)
        repository.closeActivePosition()
        assertNull(repository.activePosition.value)
    }

    @Test
    fun testUpdateTpSl() {
        repository.updateTpSl(70000.0, 64000.0, 65000.0)
        assertEquals(70000.0, repository.activePosition.value!!.tpPrice, 0.01)
        assertEquals(64000.0, repository.activePosition.value!!.slPrice, 0.01)
    }

    @Test
    fun testDeployBot() {
        val countBefore = repository.deployedBots.value.size
        repository.deployBot(
            TradeBot(
                id = "test_bot_1",
                pair = "SOL/USDT",
                strategyName = "Test Strategy",
                leverage = 10,
                isLong = true,
                isActive = true,
                isScanning = false,
                timeframe = "15m",
                sizePercent = 10,
                sizeUsd = 1000.0,
                stopLossPercent = 1.5,
                trailingStopPercent = 0.8,
                entryPrice = 145.0,
                markPrice = 148.0,
                targetPrice = 160.0,
                currentPnlUsd = 20.0,
                currentPnlPercent = 2.0,
                progressPercent = 0.5f
            )
        )
        assertEquals(countBefore + 1, repository.deployedBots.value.size)
    }

    @Test
    fun testToggleCoinCheck() {
        val coin = repository.radarCoins.value.first()
        val before = coin.isChecked
        repository.toggleCoinCheck(coin.symbol)
        val after = repository.radarCoins.value.first().isChecked
        assertNotEquals(before, after)
    }
}
