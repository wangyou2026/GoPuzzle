package com.gopuzzle.app.domain.usecase

import com.gopuzzle.app.domain.model.*

class GameEngine(private val board: Board) {
    private val boardSize: Int get() = board.size
    
    private var koPoint: Point? = null
    private var previousBoardHash: Int = 0

    fun reset() {
        koPoint = null
        previousBoardHash = 0
    }

    fun playMove(move: Move): MoveResult {
        if (move.isPass) {
            previousBoardHash = board.hashCode()
            koPoint = null
            return MoveResult.Success(board.getAllStones(), emptyList())
        }

        if (move.isResign) {
            return MoveResult.Success(board.getAllStones(), emptyList())
        }

        val point = move.point

        if (!point.isValid(boardSize)) {
            return MoveResult.Invalid("超出棋盘范围")
        }

        if (!board.isEmpty(point)) {
            return MoveResult.Invalid("该位置已有棋子")
        }

        if (koPoint == point) {
            return MoveResult.Invalid("打劫禁止")
        }

        val originalBoard = board.copy()
        board.setStone(point, move.color)

        val capturedStones = mutableListOf<Point>()
        val opponentColor = move.color.opposite()

        point.neighbors(boardSize).forEach { neighbor ->
            if (board.getStone(neighbor) == opponentColor) {
                val group = findGroup(neighbor)
                if (group.liberties.isEmpty()) {
                    capturedStones.addAll(group.stones)
                }
            }
        }

        capturedStones.forEach { board.setStone(it, Stone.EMPTY) }

        val ownGroup = findGroup(point)
        if (ownGroup.liberties.isEmpty()) {
            board.setStone(point, Stone.EMPTY)
            capturedStones.forEach { board.setStone(it, opponentColor) }
            return MoveResult.Invalid("禁止自杀")
        }

        koPoint = if (capturedStones.size == 1) {
            capturedStones[0]
        } else {
            null
        }

        val newBoardHash = board.hashCode()
        if (newBoardHash == previousBoardHash) {
            board.setStone(point, Stone.EMPTY)
            capturedStones.forEach { board.setStone(it, opponentColor) }
            return MoveResult.Invalid("禁止同形重复")
        }
        previousBoardHash = newBoardHash

        return MoveResult.Success(board.getAllStones(), capturedStones)
    }

    private fun findGroup(startPoint: Point): Group {
        val color = board.getStone(startPoint)
        if (color == Stone.EMPTY) {
            return Group(emptySet(), Stone.EMPTY, emptySet())
        }

        val visited = mutableSetOf<Point>()
        val stones = mutableSetOf<Point>()
        val liberties = mutableSetOf<Point>()
        val queue = ArrayDeque<Point>()
        queue.add(startPoint)

        while (queue.isNotEmpty()) {
            val point = queue.removeFirst()
            if (point in visited) continue
            visited.add(point)

            val stone = board.getStone(point)
            if (stone == color) {
                stones.add(point)
                point.neighbors(boardSize).forEach { neighbor ->
                    if (neighbor !in visited) {
                        queue.add(neighbor)
                    }
                }
            } else if (stone == Stone.EMPTY) {
                liberties.add(point)
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
                    if (group.liberties.isEmpty()) {
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
