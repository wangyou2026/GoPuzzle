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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.gopuzzle.app.domain.model.Board
import com.gopuzzle.app.domain.model.Point
import com.gopuzzle.app.domain.model.Stone
import com.gopuzzle.app.domain.model.StonePlacement
import com.gopuzzle.app.ui.theme.*
import kotlin.math.min
import kotlin.math.sqrt

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
                        val point = calculatePointFromOffset(
                            offset,
                            size.width.toFloat(),
                            size.height.toFloat(),
                            boardSize,
                            this@pointerInput.density
                        )
                        point?.let { onStoneClick(it) }
                    }
                }
            }
    ) {
        val padding = 32.dp.toPx()
        val boardAreaSize = size.width - padding * 2
        val cellSize = boardAreaSize / (boardSize - 1)

        drawRect(
            color = BoardBackground,
            topLeft = Offset(padding - 8.dp.toPx(), padding - 8.dp.toPx()),
            size = androidx.compose.ui.geometry.Size(boardAreaSize + 16.dp.toPx(), boardAreaSize + 16.dp.toPx())
        )

        drawGrid(boardSize, padding, cellSize)

        drawStarPoints(boardSize, padding, cellSize)

        if (showCoordinates) {
            drawCoordinates(boardSize, padding, cellSize)
        }

        stones.forEach { placement ->
            drawStone(
                placement = placement,
                padding = padding,
                cellSize = cellSize,
                boardSize = boardSize,
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

    for (i in 0 until boardSize) {
        val x = padding + i * cellSize
        drawLine(
            color = gridColor,
            start = Offset(x, padding),
            end = Offset(x, padding + (boardSize - 1) * cellSize),
            strokeWidth = 1.5f.dp.toPx(),
            cap = StrokeCap.Round
        )
    }

    for (i in 0 until boardSize) {
        val y = padding + i * cellSize
        drawLine(
            color = gridColor,
            start = Offset(padding, y),
            end = Offset(padding + (boardSize - 1) * cellSize, y),
            strokeWidth = 1.5f.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

private fun DrawScope.drawStarPoints(boardSize: Int, padding: Float, cellSize: Float) {
    val starPointRadius = when {
        boardSize == 9 -> 4f.dp.toPx()
        boardSize == 13 -> 3.5f.dp.toPx()
        boardSize == 19 -> 3f.dp.toPx()
        else -> 3f.dp.toPx()
    }

    val starPoints = when (boardSize) {
        9 -> listOf(Point(2, 2), Point(6, 2), Point(4, 4), Point(2, 6), Point(6, 6))
        13 -> listOf(Point(3, 3), Point(9, 3), Point(6, 6), Point(3, 9), Point(9, 9))
        19 -> listOf(
            Point(3, 3), Point(9, 3), Point(15, 3),
            Point(3, 9), Point(9, 9), Point(15, 9),
            Point(3, 15), Point(9, 15), Point(15, 15)
        )
        else -> emptyList()
    }

    starPoints.forEach { point ->
        val x = padding + point.x * cellSize
        val y = padding + (boardSize - 1 - point.y) * cellSize
        drawCircle(
            color = Color(0xFF8B4513),
            radius = starPointRadius,
            center = Offset(x, y)
        )
    }
}

private fun DrawScope.drawCoordinates(boardSize: Int, padding: Float, cellSize: Float) {
    val labelTextSize = 10f.dp.toPx()
    val canvas = drawContext.canvas

    val cols = "ABCDEFGHJKLMNOPQRST"
    for (i in 0 until min(boardSize, 19)) {
        val x = padding + i * cellSize
        val colLabel = cols.getOrNull(i)?.toString() ?: (i + 1).toString()

        val paint = android.graphics.Paint().apply {
            this.color = android.graphics.Color.parseColor("#8B4513")
            this.textSize = labelTextSize
            this.textAlign = android.graphics.Paint.Align.CENTER
        }

        canvas.nativeCanvas.drawText(colLabel, x, padding - 12f.dp.toPx(), paint)
        canvas.nativeCanvas.drawText(colLabel, x, padding + (boardSize - 1) * cellSize + 20f.dp.toPx(), paint)
    }

    for (i in 0 until min(boardSize, 19)) {
        val y = padding + i * cellSize
        val rowLabel = (boardSize - i).toString()

        val paint = android.graphics.Paint().apply {
            this.color = android.graphics.Color.parseColor("#8B4513")
            this.textSize = labelTextSize
            this.textAlign = android.graphics.Paint.Align.CENTER
        }

        canvas.nativeCanvas.drawText(rowLabel, padding - 12f.dp.toPx(), y + labelTextSize / 3f, paint)
        canvas.nativeCanvas.drawText(rowLabel, padding + (boardSize - 1) * cellSize + 12f.dp.toPx(), y + labelTextSize / 3f, paint)
    }
}

private fun DrawScope.drawStone(
    placement: StonePlacement,
    padding: Float,
    cellSize: Float,
    boardSize: Int,
    isHighlighted: Boolean,
    isWrong: Boolean,
    isCorrect: Boolean,
    isLastMove: Boolean
) {
    val x = padding + placement.point.x * cellSize
    val y = padding + (boardSize - 1 - placement.point.y) * cellSize
    val radius = cellSize * 0.45f
    val highlightWidth = 3f.dp.toPx()

    drawCircle(
        color = Color(0x40000000),
        radius = radius,
        center = Offset(x + 2f.dp.toPx(), y + 2f.dp.toPx())
    )

    when (placement.color) {
        Stone.BLACK -> {
            drawCircle(color = BlackStone, radius = radius, center = Offset(x, y))
            if (isHighlighted || isWrong || isCorrect) {
                val highlightColor = when {
                    isWrong -> IncorrectRed
                    isCorrect -> CorrectGreen
                    else -> HintBlue
                }
                drawCircle(color = highlightColor, radius = radius, center = Offset(x, y), style = Stroke(width = highlightWidth))
            }
            if (isLastMove) {
                drawCircle(color = Color.White, radius = radius * 0.3f, center = Offset(x, y))
            }
        }
        Stone.WHITE -> {
            drawCircle(color = WhiteStone, radius = radius, center = Offset(x, y))
            drawCircle(color = WhiteStoneShadow, radius = radius, center = Offset(x, y), style = Stroke(width = 1f.dp.toPx()))
            if (isHighlighted || isWrong || isCorrect) {
                val highlightColor = when {
                    isWrong -> IncorrectRed
                    isCorrect -> CorrectGreen
                    else -> HintBlue
                }
                drawCircle(color = highlightColor, radius = radius, center = Offset(x, y), style = Stroke(width = highlightWidth))
            }
            if (isLastMove) {
                drawCircle(color = BlackStone, radius = radius * 0.3f, center = Offset(x, y))
            }
        }
        Stone.EMPTY -> {}
    }
}

private fun calculatePointFromOffset(
    offset: Offset,
    width: Float,
    height: Float,
    boardSize: Int,
    density: Float
): Point? {
    val padding = 32f * density
    val boardAreaSize = min(width, height) - padding * 2
    val cellSize = boardAreaSize / (boardSize - 1)

    val x = ((offset.x - padding + cellSize / 2f) / cellSize).toInt()
    val rawY = ((offset.y - padding + cellSize / 2f) / cellSize).toInt()
    val y = boardSize - 1 - rawY

    val point = Point(x, y)
    if (!point.isValid(boardSize)) return null

    val exactX = padding + x * cellSize
    val exactY = padding + (boardSize - 1 - y) * cellSize
    val dx = offset.x - exactX
    val dy = offset.y - exactY
    val distance = sqrt(dx * dx + dy * dy)

    return if (distance < cellSize * 0.6f) point else null
}
