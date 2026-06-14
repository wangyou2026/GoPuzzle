package com.gopuzzle.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.gopuzzle.app.domain.model.Board
import com.gopuzzle.app.domain.model.Point
import com.gopuzzle.app.domain.model.Stone
import com.gopuzzle.app.domain.model.StonePlacement
import com.gopuzzle.app.ui.theme.*
import kotlin.math.min

@Composable
fun GoBoard(
    boardSize: Int,
    stones: List<StonePlacement>,
    modifier: Modifier = Modifier,
    currentPlayer: Stone = Stone.BLACK,
    showCoordinates: Boolean = true,
    highlightedPoints: Set<Point> = emptySet(),
    wrongPoint: Point? = null,
    correctPoint: Point? = null,
    lastMovePoint: Point? = null,
    onStoneClick: ((Point) -> Unit)? = null,
    enabled: Boolean = true
) {
    val board = remember { Board(boardSize) }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .pointerInput(enabled, boardSize) {
                if (enabled && onStoneClick != null) {
                    detectTapGestures { offset ->
                        val point = calculatePointFromOffset(offset, size.width.toFloat(), size.height.toFloat(), boardSize)
                        point?.let { onStoneClick(it) }
                    }
                }
            }
    ) {
        val padding = 32.dp.toPx()
        val boardAreaSize = size.width - padding * 2
        val cellSize = boardAreaSize / (boardSize - 1)

        // Draw board background (wood texture effect)
        drawRect(
            color = BoardBackground,
            topLeft = Offset(padding - 8.dp.toPx(), padding - 8.dp.toPx()),
            size = androidx.compose.ui.geometry.Size(boardAreaSize + 16.dp.toPx(), boardAreaSize + 16.dp.toPx())
        )

        // Draw grid lines
        drawGrid(boardSize, padding, cellSize)

        // Draw star points (星位)
        drawStarPoints(boardSize, padding, cellSize)

        // Draw coordinates
        if (showCoordinates) {
            drawCoordinates(boardSize, padding, cellSize)
        }

        // Draw stones
        stones.forEach { placement ->
            drawStone(
                placement = placement,
                padding = padding,
                cellSize = cellSize,
                isHighlighted = placement.point in highlightedPoints,
                isWrong = placement.point == wrongPoint,
                isCorrect = placement.point == correctPoint,
                isLastMove = placement.point == lastMovePoint
            )
        }
    }
}

private fun DrawScope.drawGrid(boardSize: Int, padding: Float, cellSize: Float) {
    val gridColor = Color(0xFF8B4513)

    // Draw vertical lines
    for (i in 0 until boardSize) {
        val x = padding + i * cellSize
        drawLine(
            color = gridColor,
            start = Offset(x, padding),
            end = Offset(x, padding + (boardSize - 1) * cellSize),
            strokeWidth = 1.5.dp.toPx(),
            cap = StrokeCap.Round
        )
    }

    // Draw horizontal lines
    for (i in 0 until boardSize) {
        val y = padding + i * cellSize
        drawLine(
            color = gridColor,
            start = Offset(padding, y),
            end = Offset(padding + (boardSize - 1) * cellSize, y),
            strokeWidth = 1.5.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

private fun DrawScope.drawStarPoints(boardSize: Int, padding: Float, cellSize: Float) {
    val starPointRadius = when {
        boardSize == 9 -> 4.dp.toPx()
        boardSize == 13 -> 3.5.dp.toPx()
        boardSize == 19 -> 3.dp.toPx()
        else -> 3.dp.toPx()
    }

    // Calculate star point positions based on board size
    val starPoints = when (boardSize) {
        9 -> listOf(
            Point(2, 2), Point(6, 2), Point(4, 4), Point(2, 6), Point(6, 6)
        )
        13 -> listOf(
            Point(3, 3), Point(9, 3), Point(6, 6), Point(3, 9), Point(9, 9)
        )
        19 -> listOf(
            Point(3, 3), Point(9, 3), Point(15, 3),
            Point(3, 9), Point(9, 9), Point(15, 9),
            Point(3, 15), Point(9, 15), Point(15, 15)
        )
        else -> emptyList()
    }

    starPoints.forEach { point ->
        val x = padding + point.x * cellSize
        val y = padding + point.y * cellSize
        drawCircle(
            color = Color(0xFF8B4513),
            radius = starPointRadius,
            center = Offset(x, y)
        )
    }
}

private fun DrawScope.drawCoordinates(boardSize: Int, padding: Float, cellSize: Float) {
    val textColor = Color(0xFF8B4513)
    val textSize = 10.dp.toPx()

    // Draw column labels (A-T, skipping I)
    val cols = "ABCDEFGHJKLMNOPQRST"
    for (i in 0 until min(boardSize, 19)) {
        val x = padding + i * cellSize
        val label = cols.getOrElse(i) { (i + 1).toString() }
        // Draw at top
        drawContext.canvas.nativeCanvas.drawText(
            label,
            x,
            padding - 12.dp.toPx(),
            android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#8B4513")
                textSize = textSize
                textAlign = android.graphics.Paint.Align.CENTER
            }
        )
        // Draw at bottom
        drawContext.canvas.nativeCanvas.drawText(
            label,
            x,
            padding + (boardSize - 1) * cellSize + 20.dp.toPx(),
            android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#8B4513")
                textSize = textSize
                textAlign = android.graphics.Paint.Align.CENTER
            }
        )
    }

    // Draw row labels (1-19)
    for (i in 0 until min(boardSize, 19)) {
        val y = padding + i * cellSize
        val label = (boardSize - i).toString()
        // Draw on left
        drawContext.canvas.nativeCanvas.drawText(
            label,
            padding - 12.dp.toPx(),
            y + textSize / 3,
            android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#8B4513")
                textSize = textSize
                textAlign = android.graphics.Paint.Align.CENTER
            }
        )
        // Draw on right
        drawContext.canvas.nativeCanvas.drawText(
            label,
            padding + (boardSize - 1) * cellSize + 12.dp.toPx(),
            y + textSize / 3,
            android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#8B4513")
                textSize = textSize
                textAlign = android.graphics.Paint.Align.CENTER
            }
        )
    }
}

private fun DrawScope.drawStone(
    placement: StonePlacement,
    padding: Float,
    cellSize: Float,
    isHighlighted: Boolean,
    isWrong: Boolean,
    isCorrect: Boolean,
    isLastMove: Boolean
) {
    val x = padding + placement.point.x * cellSize
    val y = padding + placement.point.y * cellSize
    val radius = cellSize * 0.45f

    // Stone shadow
    drawCircle(
        color = Color(0x40000000),
        radius = radius,
        center = Offset(x + 2.dp.toPx(), y + 2.dp.toPx())
    )

    // Main stone
    when (placement.color) {
        Stone.BLACK -> {
            drawCircle(
                color = BlackStone,
                radius = radius,
                center = Offset(x, y)
            )
            // Highlight
            if (isHighlighted || isWrong || isCorrect) {
                drawCircle(
                    color = when {
                        isWrong -> IncorrectRed
                        isCorrect -> CorrectGreen
                        else -> HintBlue
                    },
                    radius = radius,
                    center = Offset(x, y),
                    style = Stroke(width = 3.dp.toPx())
                )
            }
            // Last move marker
            if (isLastMove) {
                drawCircle(
                    color = Color.White,
                    radius = radius * 0.3f,
                    center = Offset(x, y)
                )
            }
        }
        Stone.WHITE -> {
            drawCircle(
                color = WhiteStone,
                radius = radius,
                center = Offset(x, y)
            )
            // Stone border
            drawCircle(
                color = WhiteStoneShadow,
                radius = radius,
                center = Offset(x, y),
                style = Stroke(width = 1.dp.toPx())
            )
            // Highlight
            if (isHighlighted || isWrong || isCorrect) {
                drawCircle(
                    color = when {
                        isWrong -> IncorrectRed
                        isCorrect -> CorrectGreen
                        else -> HintBlue
                    },
                    radius = radius,
                    center = Offset(x, y),
                    style = Stroke(width = 3.dp.toPx())
                )
            }
            // Last move marker
            if (isLastMove) {
                drawCircle(
                    color = BlackStone,
                    radius = radius * 0.3f,
                    center = Offset(x, y)
                )
            }
        }
        Stone.EMPTY -> { }
    }
}

private fun calculatePointFromOffset(
    offset: Offset,
    width: Float,
    height: Float,
    boardSize: Int
): Point? {
    val padding = 32.dp.toPx()
    val boardAreaSize = min(width, height) - padding * 2
    val cellSize = boardAreaSize / (boardSize - 1)

    val x = ((offset.x - padding + cellSize / 2) / cellSize).toInt()
    val y = ((offset.y - padding + cellSize / 2) / cellSize).toInt()

    val point = Point(x, y)
    if (!point.isValid(boardSize)) return null

    // Check if the touch is close enough to the intersection
    val exactX = padding + x * cellSize
    val exactY = padding + y * cellSize
    val distance = kotlin.math.sqrt(
        (offset.x - exactX) * (offset.x - exactX) +
        (offset.y - exactY) * (offset.y - exactY)
    )

    return if (distance < cellSize * 0.6f) point else null
}
