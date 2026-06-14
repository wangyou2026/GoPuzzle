package com.gopuzzle.app.domain.usecase

import com.gopuzzle.app.domain.model.*

class GameEngine(private val board: Board) {
    private val boardSize: Int get() = board.size
    private var lastCapturedPoint: Point? = null

    fun reset() {
        lastCapturedPoint = null
    }

    fun playMove(move: Move): MoveResult {
        if (move.isPass || move.isResign) {
            return MoveResult.Success(board.getAllStones(), emptyList())
        }

        val point = move.point

        // 检查是否在棋盘内
        if (!point.isValid(boardSize)) {
            return MoveResult.Invalid("超出棋盘范围")
        }

        // 检查是否已有棋子
        if (!board.isEmpty(point)) {
            return MoveResult.Invalid("该位置已有棋子")
        }

        // 放置棋子
        board.setStone(point, move.color)

        // 检查提子
        val capturedStones = mutableListOf<Point>()
        val opponentColor = move.color.opposite()

        point.neighbors(boardSize).forEach { neighbor ->
            if (board.getStone(neighbor) == opponentColor) {
                val group = findGroup(neighbor)
                if (group.isDead) {
                    capturedStones.addAll(group.stones)
                }
            }
        }

        // 移除被提的棋子
        capturedStones.forEach { board.setStone(it, Stone.EMPTY) }

        // 检查自杀（简单处理：检查自己是否有气）
        if (capturedStones.isEmpty) {
            val ownGroup = findGroup(point)
            if (ownGroup.isDead) {
                // 自杀，禁止
                board.setStone(point, Stone.EMPTY)
                return MoveResult.Invalid("禁止自杀")
            }
        }

        // 检查打劫（简化为：只能隔一手才能回提）
        val isKo = capturedStones.size == 1 && lastCapturedPoint == point
        if (isKo) {
            return MoveResult.Invalid("打劫禁止立即回提")
        }

        lastCapturedPoint = if (capturedStones.isNotEmpty()) capturedStones.first() else null

        return MoveResult.Success(board.getAllStones(), capturedStones)
    }

    private fun findGroup(startPoint: Point): Group {
        val color = board.getStone(startPoint)
        if (color == Stone.EMPTY) return Group(emptySet(), Stone.EMPTY, emptySet())

        val visited = mutableSetOf<Point>()
        val stones = mutableSetOf<Point>()
        val queue = ArrayDeque<Point>()
        queue.add(startPoint)

        while (queue.isNotEmpty()) {
            val point = queue.removeFirst()
            if (point in visited) continue
            visited.add(point)

            if (board.getStone(point) == color) {
                stones.add(point)
                point.neighbors(boardSize).forEach { neighbor ->
                    if (neighbor !in visited) {
                        queue.add(neighbor)
                    }
                }
            }
        }

        // 计算气
        val liberties = mutableSetOf<Point>()
        stones.forEach { stone ->
            stone.neighbors(boardSize).forEach { neighbor ->
                if (board.getStone(neighbor) == Stone.EMPTY) {
                    liberties.add(neighbor)
                }
            }
        }

        return Group(stones, color, liberties)
    }

    fun getGroupAt(point: Point): Group? {
        if (board.getStone(point) == Stone.EMPTY) return null
        return findGroup(point)
    }

    fun findAllDeadGroups(): List<Group> {
        val deadGroups = mutableListOf<Group>()
        val visited = mutableSetOf<Point>()

        for (x in 0 until boardSize) {
            for (y in 0 until boardSize) {
                val point = Point(x, y)
                if (point !in visited && board.getStone(point) != Stone.EMPTY) {
                    val group = findGroup(point)
                    visited.addAll(group.stones)
                    if (group.isDead) {
                        deadGroups.add(group)
                    }
                }
            }
        }
        return deadGroups
    }
}

sealed class MoveResult {
    data class Success(val boardStones: List<StonePlacement>, val captured: List<Point>) : MoveResult()
    data class Invalid(val reason: String) : MoveResult()
}
