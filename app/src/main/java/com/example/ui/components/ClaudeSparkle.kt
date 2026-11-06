package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ClaudeSparkle(
    modifier: Modifier = Modifier,
    size: Dp = 56.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val midX = w / 2f
        val midY = h / 2f
        val starPath = Path().apply {
            moveTo(midX, 0f)
            cubicTo(midX, midY * 0.5f, midX + w * 0.25f, midY, w, midY)
            cubicTo(midX + w * 0.25f, midY, midX, midY * 1.5f, midX, h)
            cubicTo(midX, midY * 1.5f, midX - w * 0.25f, midY, 0f, midY)
            cubicTo(midX - w * 0.25f, midY, midX, midY * 0.5f, midX, 0f)
            close()
        }
        drawPath(
            path = starPath,
            brush = Brush.linearGradient(
                listOf(
                    Color(0xFF60A5FA),
                    Color(0xFF34D399),
                    Color(0xFFFBBF24),
                    Color(0xFFF43F5E)
                )
            )
        )
    }
}
