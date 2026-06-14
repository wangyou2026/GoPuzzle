package com.gopuzzle.app.domain.model

data class Move(
    val point: Point,
    val color: Stone,
    val isPass: Boolean = false,
    val isResign: Boolean = false
) {
    companion object {
        fun fromSgf(sgfMove: String, color: Stone): Move {
            return if (sgfMove.isEmpty() || sgfMove == "" || sgfMove == "tt") {
                Move(Point(0, 0), color, isPass = true)
            } else {
                Move(Point.fromSgf(sgfMove), color)
            }
        }
    }
}
