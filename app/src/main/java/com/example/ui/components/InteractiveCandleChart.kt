package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterCenterFocus
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Candle
import com.example.ui.theme.*

@Composable
fun InteractiveCandleChart(
    modifier: Modifier = Modifier,
    candles: List<Candle> = defaultCandles,
    tpPrice: Float = 69200f,
    entryPrice: Float = 66800f,
    trailPrice: Float = 66300f,
    slPrice: Float = 65950f,
    currentPrice: Float = 67420.50f,
    showIndicators: Boolean = true
) {
    var touchOffset by remember { mutableStateOf<Offset?>(null) }
    var selectedCandle by remember { mutableStateOf<Candle?>(null) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(310.dp)
            .background(SurfaceContainerLowest)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { offset ->
                            touchOffset = if (touchOffset == null) offset else null
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset -> touchOffset = offset },
                        onDragEnd = { touchOffset = null },
                        onDragCancel = { touchOffset = null },
                        onDrag = { change, _ -> touchOffset = change.position }
                    )
                }
        ) {
            val width = size.width
            val height = size.height
            val chartBottom = height - 44f
            val chartTop = 16f
            val chartHeight = chartBottom - chartTop
            val volumeHeight = 50f
            val volumeBottom = chartBottom - 10f

            val minPrice = 65200f
            val maxPrice = 69800f
            val priceRange = maxPrice - minPrice

            fun yForPrice(p: Float): Float {
                val clamped = p.coerceIn(minPrice, maxPrice)
                val ratio = (clamped - minPrice) / priceRange
                return chartBottom - (ratio * chartHeight)
            }

            // Grid lines
            val gridPrices = listOf(66000f, 67000f, 68000f, 69000f)
            val dashEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
            for (gp in gridPrices) {
                val y = yForPrice(gp)
                drawLine(
                    color = Color(0xFF1E2838),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1f,
                    pathEffect = dashEffect
                )
            }

            // Volume area background lines
            drawLine(
                color = Color(0xFF161E2C),
                start = Offset(0f, volumeBottom),
                end = Offset(width, volumeBottom),
                strokeWidth = 1f
            )

            // Supertrend ribbon fill & step line (bullish emerald)
            if (showIndicators) {
                val supertrendPath = Path()
                val stepX1 = width * 0.05f
                val stepX2 = width * 0.35f
                val stepX3 = width * 0.65f
                val stepX4 = width * 0.95f

                val stY1 = yForPrice(65750f)
                val stY2 = yForPrice(66200f)
                val stY3 = yForPrice(66900f)

                supertrendPath.moveTo(stepX1, stY1)
                supertrendPath.lineTo(stepX2, stY1)
                supertrendPath.lineTo(stepX2, stY2)
                supertrendPath.lineTo(stepX3, stY2)
                supertrendPath.lineTo(stepX3, stY3)
                supertrendPath.lineTo(stepX4, stY3)

                drawPath(
                    path = supertrendPath,
                    color = Color(0xFF00F090),
                    style = Stroke(width = 2.5f)
                )

                // EMA 20 curve (Electric Cyan)
                val ema20Path = Path()
                val emaPoints = listOf(
                    Offset(0f, yForPrice(65600f)),
                    Offset(width * 0.2f, yForPrice(65950f)),
                    Offset(width * 0.45f, yForPrice(66400f)),
                    Offset(width * 0.7f, yForPrice(66950f)),
                    Offset(width * 0.9f, yForPrice(67380f)),
                    Offset(width, yForPrice(67450f))
                )
                ema20Path.moveTo(emaPoints[0].x, emaPoints[0].y)
                for (i in 1 until emaPoints.size) {
                    val p0 = emaPoints[i - 1]
                    val p1 = emaPoints[i]
                    val cx = (p0.x + p1.x) / 2f
                    ema20Path.cubicTo(cx, p0.y, cx, p1.y, p1.x, p1.y)
                }
                drawPath(path = ema20Path, color = Color(0xFF00E5FF), style = Stroke(width = 2f))

                // EMA 50 curve (Ocean Blue)
                val ema50Path = Path()
                val ema50Points = listOf(
                    Offset(0f, yForPrice(65350f)),
                    Offset(width * 0.25f, yForPrice(65600f)),
                    Offset(width * 0.5f, yForPrice(66000f)),
                    Offset(width * 0.75f, yForPrice(66400f)),
                    Offset(width, yForPrice(66700f))
                )
                ema50Path.moveTo(ema50Points[0].x, ema50Points[0].y)
                for (i in 1 until ema50Points.size) {
                    val p0 = ema50Points[i - 1]
                    val p1 = ema50Points[i]
                    val cx = (p0.x + p1.x) / 2f
                    ema50Path.cubicTo(cx, p0.y, cx, p1.y, p1.x, p1.y)
                }
                drawPath(path = ema50Path, color = Color(0xFF00B4D8), style = Stroke(width = 1.8f))
            }

            // Draw Candlesticks & Volume
            val candleCount = candles.size
            val candleSlotWidth = width / (candleCount + 1)
            val candleBodyWidth = candleSlotWidth * 0.55f

            for (i in candles.indices) {
                val c = candles[i]
                val cx = (i + 1) * candleSlotWidth
                val isBullish = c.close >= c.open
                val candleColor = if (isBullish) Color(0xFF00F090) else Color(0xFFFF3B57)

                val highY = yForPrice(c.high)
                val lowY = yForPrice(c.low)
                val openY = yForPrice(c.open)
                val closeY = yForPrice(c.close)

                val bodyTop = minOf(openY, closeY)
                val bodyBottom = maxOf(openY, closeY)
                val bodyHeight = maxOf(3f, bodyBottom - bodyTop)

                // High/Low wick
                drawLine(
                    color = candleColor,
                    start = Offset(cx, highY),
                    end = Offset(cx, lowY),
                    strokeWidth = 1.5f
                )

                // Body
                drawRect(
                    color = candleColor,
                    topLeft = Offset(cx - candleBodyWidth / 2f, bodyTop),
                    size = Size(candleBodyWidth, bodyHeight)
                )

                // Volume Bar at the bottom
                val volRatio = (c.volume / 100f).coerceIn(0.1f, 1f)
                val volBarH = volRatio * volumeHeight
                drawRect(
                    color = candleColor.copy(alpha = 0.65f),
                    topLeft = Offset(cx - candleBodyWidth / 2f, volumeBottom - volBarH),
                    size = Size(candleBodyWidth, volBarH)
                )
            }

            // Target Horizontal Lines
            fun drawTargetLine(price: Float, color: Color) {
                val y = yForPrice(price)
                drawLine(
                    color = color.copy(alpha = 0.8f),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1.5f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
                )
            }

            drawTargetLine(tpPrice, Color(0xFF00F090))
            drawTargetLine(entryPrice, Color(0xFF00E5FF))
            drawTargetLine(trailPrice, Color(0xFFFFB020))
            drawTargetLine(slPrice, Color(0xFFFF3B57))

            // Current price live hairline
            val curY = yForPrice(currentPrice)
            drawLine(
                color = Color(0xFF00E5FF).copy(alpha = 0.9f),
                start = Offset(0f, curY),
                end = Offset(width, curY),
                strokeWidth = 1f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)
            )

            // Touch crosshair
            touchOffset?.let { touch ->
                drawLine(
                    color = Color.White.copy(alpha = 0.4f),
                    start = Offset(touch.x, 0f),
                    end = Offset(touch.x, height),
                    strokeWidth = 1f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)
                )
                drawLine(
                    color = Color.White.copy(alpha = 0.4f),
                    start = Offset(0f, touch.y),
                    end = Offset(width, touch.y),
                    strokeWidth = 1f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)
                )
            }
        }

        // Target Pills floating over chart
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 8.dp, top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.End
        ) {
            TargetBadge(label = "● TP: $69,200.00 (+3.59%)", textColor = NeonEmerald, bgColor = Color(0xFF082618))
            TargetBadge(label = "● ENTRY: $66,800.00", textColor = ElectricCyan, bgColor = Color(0xFF08222A))
            TargetBadge(label = "● TRAIL: $66,300.00", textColor = AlertAmber, bgColor = Color(0xFF281E0C))
            TargetBadge(label = "● SL: $65,950.00 (-1.27%)", textColor = LaserCrimson, bgColor = Color(0xFF2A0D12))
        }

        // Live Current Price Badge anchored to right
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(y = 105.dp)
                .background(Color(0xFF00E5FF), RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = "$$currentPrice",
                fontFamily = JetBrainsMono,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF080E1A)
            )
        }

        // Drawing toolbar floating bottom-left
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 8.dp, bottom = 24.dp)
                .background(SurfaceContainerHigh.copy(alpha = 0.85f), RoundedCornerShape(6.dp))
                .padding(horizontal = 4.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            IconButton(onClick = {}, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.CalendarMonth, contentDescription = "Interval", tint = OnSurfaceVariant, modifier = Modifier.size(14.dp))
            }
            IconButton(onClick = {}, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Edit, contentDescription = "Draw", tint = OnSurfaceVariant, modifier = Modifier.size(14.dp))
            }
            IconButton(onClick = {}, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.FilterCenterFocus, contentDescription = "Focus", tint = ElectricCyan, modifier = Modifier.size(14.dp))
            }
        }

        // Bottom Time Scale Labels
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("14:00", fontFamily = JetBrainsMono, fontSize = 10.sp, color = Outline)
            Text("14:30", fontFamily = JetBrainsMono, fontSize = 10.sp, color = Outline)
            Text("15:00", fontFamily = JetBrainsMono, fontSize = 10.sp, color = Outline)
            Text("15:30", fontFamily = JetBrainsMono, fontSize = 10.sp, color = Outline)
            Text("16:00 (Live)", fontFamily = JetBrainsMono, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ElectricCyan)
        }
    }
}

@Composable
private fun TargetBadge(label: String, textColor: Color, bgColor: Color) {
    Box(
        modifier = Modifier
            .background(bgColor.copy(alpha = 0.9f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = label,
            fontFamily = JetBrainsMono,
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            color = textColor
        )
    }
}

val defaultCandles = listOf(
    Candle(1, 65400f, 65650f, 65350f, 65600f, 35f, "14:00"),
    Candle(2, 65600f, 65800f, 65550f, 65750f, 42f, "14:10"),
    Candle(3, 65750f, 66100f, 65700f, 65900f, 28f, "14:20"),
    Candle(4, 65900f, 66300f, 65850f, 66250f, 65f, "14:30"),
    Candle(5, 66250f, 66400f, 66050f, 66100f, 52f, "14:40"),
    Candle(6, 66100f, 66600f, 66050f, 66550f, 78f, "14:50"),
    Candle(7, 66550f, 66900f, 66400f, 66850f, 45f, "15:00"),
    Candle(8, 66850f, 67100f, 66700f, 67050f, 88f, "15:10"),
    Candle(9, 67050f, 67350f, 66950f, 67200f, 95f, "15:20"),
    Candle(10, 67200f, 67600f, 67150f, 67550f, 110f, "15:30"),
    Candle(11, 67550f, 67700f, 67300f, 67420.5f, 125f, "16:00")
)
