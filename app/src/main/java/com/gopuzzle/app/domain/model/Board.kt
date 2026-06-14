package com.gopuzzle.app.domain.model

class Board(val size: Int) {
    private val stones: Array<Array<Stone>> = Array(size) { Array(size) { Stone.EMPTY } }

    fun getStone(point: Point): Stone {
        return if (point.isValid(size)) stones[point.x][point.y] else Stone.EMPTY
    }

    fun setStone(point: Point, stone: Stone): Boolean {
        if (!point.isValid(size)) return false
        stones[point.x][point.y] = stone
        return true
    }

    fun copy(): Board {
        val newBoard = Board(size)
        for (x in 0 until size) {
            for (y in 0 until size) {
                newBoard.setStone(Point(x, y), stones[x][y])
            }
        }
        return newBoard
    }

    fun getAllStones(): List<StonePlacement> {
        val placements = mutableListOf<StonePlacement>()
        for (x in 0 until size) {
            for (y in 0 until size) {
                if (stones[x][y] != Stone.EMPTY) {
                    placements.add(StonePlacement(Point(x, y), stones[x][y]))
                }
            }
        }
        return placements
    }

    fun isEmpty(point: Point): Boolean = getStone(point) == Stone.EMPTY

    companion object {
        fun fromStones(size: Int, stones: List<StonePlacement>): Board {
            val board = Board(size)
            stones.forEach { board.setStone(it.point, it.color) }
            return board
        }
    }
}
