package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun EquityCurveChart(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(130.dp)
    ) {
        val width = size.width
        val height = size.height

        // Subtle grid hairlines
        val dashEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f), 0f)
        val gridY1 = height * 0.25f
        val gridY2 = height * 0.58f
        val gridY3 = height * 0.88f

        drawLine(Color(0xFF242A37), Offset(0f, gridY1), Offset(width, gridY1), 1f, pathEffect = dashEffect)
        drawLine(Color(0xFF242A37), Offset(0f, gridY2), Offset(width, gridY2), 1f, pathEffect = dashEffect)
        drawLine(Color(0xFF242A37), Offset(0f, gridY3), Offset(width, gridY3), 1f, pathEffect = dashEffect)

        // Curve Points normalized
        val path = Path()
        val fillPath = Path()

        fun normX(x: Float) = (x / 340f) * width
        fun normY(y: Float) = (y / 120f) * height

        path.moveTo(normX(0f), normY(105f))
        fillPath.moveTo(normX(0f), height)
        fillPath.lineTo(normX(0f), normY(105f))

        path.cubicTo(normX(30f), normY(102f), normX(45f), normY(95f), normX(70f), normY(88f))
        fillPath.cubicTo(normX(30f), normY(102f), normX(45f), normY(95f), normX(70f), normY(88f))

        path.cubicTo(normX(95f), normY(82f), normX(120f), normY(92f), normX(145f), normY(68f))
        fillPath.cubicTo(normX(95f), normY(82f), normX(120f), normY(92f), normX(145f), normY(68f))

        path.cubicTo(normX(170f), normY(44f), normX(195f), normY(60f), normX(225f), normY(42f))
        fillPath.cubicTo(normX(170f), normY(44f), normX(195f), normY(60f), normX(225f), normY(42f))

        path.cubicTo(normX(255f), normY(24f), normX(285f), normY(30f), normX(310f), normY(18f))
        fillPath.cubicTo(normX(255f), normY(24f), normX(285f), normY(30f), normX(310f), normY(18f))

        path.lineTo(normX(340f), normY(12f))
        fillPath.lineTo(normX(340f), normY(12f))

        fillPath.lineTo(width, height)
        fillPath.close()

        // Area Gradient
        val areaBrush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF00E5FF).copy(alpha = 0.35f),
                Color(0xFF00F090).copy(alpha = 0.10f),
                Color(0xFF00DAF3).copy(alpha = 0.00f)
            ),
            startY = 0f,
            endY = height
        )
        drawPath(path = fillPath, brush = areaBrush)

        // Line Gradient
        val lineBrush = Brush.horizontalGradient(
            colors = listOf(
                Color(0xFF00DAF3),
                Color(0xFF00E5FF),
                Color(0xFF00F090),
                Color(0xFF58FFA5)
            )
        )

        // Neon ambient glow stroke
        drawPath(
            path = path,
            brush = lineBrush,
            style = Stroke(width = 5.5f, cap = StrokeCap.Round)
        )
        drawPath(
            path = path,
            color = Color.White.copy(alpha = 0.6f),
            style = Stroke(width = 2.0f, cap = StrokeCap.Round)
        )

        // Signal Nodes
        fun drawNode(xSvg: Float, ySvg: Float, nodeColor: Color) {
            val center = Offset(normX(xSvg), normY(ySvg))
            drawCircle(Color(0xFF080E1A), radius = 6.5f, center = center)
            drawCircle(nodeColor, radius = 4f, center = center)
        }

        drawNode(70f, 88f, Color(0xFF00DAF3))
        drawNode(145f, 68f, Color(0xFF00E5FF))
        drawNode(225f, 42f, Color(0xFF58FFA5))

        // Live ATH pulsating beacon
        val endCenter = Offset(normX(340f), normY(12f))
        drawCircle(
            Color(0xFF00F090).copy(alpha = pulseAlpha * 0.4f),
            radius = 12f,
            center = endCenter
        )
        drawCircle(Color(0xFF080E1A), radius = 7f, center = endCenter)
        drawCircle(Color(0xFF58FFA5), radius = 4.5f, center = endCenter)
    }
}
