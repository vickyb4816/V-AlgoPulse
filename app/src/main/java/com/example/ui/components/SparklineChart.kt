package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SparklineChart(
    points: List<Float>,
    lineColor: Color,
    modifier: Modifier = Modifier,
    fillColor: Color? = null,
    strokeWidth: Float = 2.5f
) {
    if (points.isEmpty()) return

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        val minVal = points.minOrNull() ?: 0f
        val maxVal = points.maxOrNull() ?: 1f
        val range = (maxVal - minVal).coerceAtLeast(0.001f)

        val stepX = width / (points.size - 1).coerceAtLeast(1)

        val path = Path()
        val fillPath = Path()

        for (i in points.indices) {
            val normalizedY = 1f - ((points[i] - minVal) / range)
            val x = i * stepX
            val y = normalizedY * (height - 8f) + 4f

            if (i == 0) {
                path.moveTo(x, y)
                fillPath.moveTo(x, height)
                fillPath.lineTo(x, y)
            } else {
                path.lineTo(x, y)
                fillPath.lineTo(x, y)
            }
        }

        fillPath.lineTo(width, height)
        fillPath.close()

        fillColor?.let { fill ->
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(fill.copy(alpha = 0.35f), fill.copy(alpha = 0.0f)),
                    startY = 0f,
                    endY = height
                )
            )
        }

        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}
