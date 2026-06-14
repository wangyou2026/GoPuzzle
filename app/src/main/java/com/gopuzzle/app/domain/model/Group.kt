package com.gopuzzle.app.domain.model

data class Group(
    val stones: Set<Point>,
    val color: Stone,
    val liberties: Set<Point>
) {
    val isDead: Boolean get() = liberties.isEmpty()
}
