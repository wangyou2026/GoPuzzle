package com.gopuzzle.app.domain.model

data class Puzzle(
    val id: String,
    val title: String,
    val boardSize: Int,
    val initialStones: List<StonePlacement>,
    val solutionTree: MoveNode,
    val difficulty: Difficulty,
    val category: PuzzleCategory,
    val description: String = "",
    val nextPlayerColor: Stone = Stone.BLACK
)

data class StonePlacement(
    val point: Point,
    val color: Stone
)

data class MoveNode(
    val move: Move?,
    val isCorrect: Boolean,
    val children: List<MoveNode>,
    val hint: String? = null,
    val comment: String? = null
) {
    fun findChild(move: Move): MoveNode? {
        if (move == this.move) return this
        return children.find { it.move == move }
    }

    fun hasCorrectChild(): Boolean = isCorrect || children.any { it.hasCorrectChild() }
}

enum class PuzzleCategory(val displayName: String) {
    LIFE_AND_DEATH("死活题"),
    TESUJI("手筋题"),
    YOSE("官子题"),
    OPENING("布局题"),
    ENDGAME("收官题")
}

enum class Difficulty(val level: Int, val displayName: String) {
    BEGINNER(1, "入门"),
    EASY(2, "简单"),
    INTERMEDIATE(3, "中级"),
    HARD(4, "困难"),
    ADVANCED(5, "高级")
}

data class PuzzleProgress(
    val puzzleId: String,
    val isSolved: Boolean,
    val isWrong: Boolean,
    val isFavorite: Boolean,
    val attemptCount: Int,
    val lastAttemptTime: Long
)

data class PuzzleState(
    val puzzle: Puzzle,
    val currentBoard: Board,
    val currentNode: MoveNode,
    val moveHistory: List<Move>,
    val currentPlayerColor: Stone,
    val isComplete: Boolean,
    val isCorrect: Boolean,
    val showHint: Boolean,
    val showAnswer: Boolean,
    val wrongMove: Move? = null
)
