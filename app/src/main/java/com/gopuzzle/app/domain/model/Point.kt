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

    companion object {
        fun fromSgf(sgf: String): Point {
            val col = sgf[0].code - 'a'.code
            val row = sgf[1].code - 'a'.code
            return Point(col, row)
        }
    }

    fun toSgf(): String {
        val col = ('a'.code + x).toChar()
        val row = ('a'.code + y).toChar()
        return "$col$row"
    }
}
