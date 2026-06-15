package com.gopuzzle.app.domain.model

enum class Stone {
    EMPTY,
    BLACK,
    WHITE;

    fun opposite(): Stone = when (this) {
        EMPTY -> EMPTY
        BLACK -> WHITE
        WHITE -> BLACK
    }

    companion object {
        fun fromChar(c: Char): Stone = when (c.uppercaseChar()) {
            'B' -> BLACK
            'W' -> WHITE
            else -> EMPTY
        }
    }
}

data class Point(
    val x: Int,
    val y: Int
) {
    init {
        require(x >= 0 && y >= 0) { "坐标必须非负: ($x, $y)" }
    }

    fun isValid(boardSize: Int): Boolean = x in 0 until boardSize && y in 0 until boardSize

    fun neighbors(boardSize: Int): List<Point> = listOfNotNull(
        Point(x - 1, y).takeIf { it.isValid(boardSize) },
        Point(x + 1, y).takeIf { it.isValid(boardSize) },
        Point(x, y - 1).takeIf { it.isValid(boardSize) },
        Point(x, y + 1).takeIf { it.isValid(boardSize) }
    )

    fun toBoardCoordinate(boardSize: Int): String {
        val cols = "ABCDEFGHJKLMNOPQRST"
        val col = cols.getOrNull(x) ?: '?'
        val row = boardSize - y
        return "$col$row"
    }

    companion object {
        fun fromSgf(sgf: String): Point {
            val col = sgf[0].code - 'a'.code
            val row = sgf[1].code - 'a'.code
            return Point(col, row)
        }

        fun fromBoardCoordinate(coordinate: String, boardSize: Int): Point {
            val cols = "ABCDEFGHJKLMNOPQRST"
            val col = coordinate[0].uppercaseChar()
            val row = coordinate.substring(1).toInt()
            val x = cols.indexOf(col)
            val y = boardSize - row
            return Point(x, y)
        }
    }

    fun toSgf(): String {
        val col = ('a'.code + x).toChar()
        val row = ('a'.code + y).toChar()
        return "$col$row"
    }
}
