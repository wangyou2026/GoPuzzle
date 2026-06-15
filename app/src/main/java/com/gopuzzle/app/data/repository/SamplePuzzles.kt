package com.gopuzzle.app.data.repository

import com.gopuzzle.app.domain.model.*

object SamplePuzzles {

    fun getAll(): List<Puzzle> = buildList {
        addAll(getLifeAndDeathPuzzles())
        addAll(getTesujiPuzzles())
        addAll(getYosePuzzles())
    }

    fun getByCategory(category: PuzzleCategory): List<Puzzle> = getAll().filter { it.category == category }

    fun getByDifficulty(difficulty: Difficulty): List<Puzzle> = getAll().filter { it.difficulty == difficulty }

    fun getById(id: String): Puzzle? = getAll().find { it.id == id }

    private fun getLifeAndDeathPuzzles(): List<Puzzle> = listOf(
        createLifePuzzle(
            id = "ld_001",
            title = "入门-第一题",
            boardSize = 9,
            initialStones = listOf(
                StonePlacement(Point(0, 8), Stone.WHITE),
                StonePlacement(Point(1, 8), Stone.WHITE),
                StonePlacement(Point(0, 7), Stone.WHITE),
                StonePlacement(Point(1, 7), Stone.BLACK),
                StonePlacement(Point(0, 6), Stone.WHITE),
            ),
            moves = listOf(Move(Point(2, 7), Stone.BLACK)),
            correctSequence = listOf(Move(Point(2, 7), Stone.BLACK)),
            difficulty = Difficulty.BEGINNER,
            description = "黑棋在B8，在C8做眼活棋"
        ),
        createLifePuzzle(
            id = "ld_002",
            title = "入门-第二题",
            boardSize = 9,
            initialStones = listOf(
                StonePlacement(Point(1, 8), Stone.WHITE),
                StonePlacement(Point(2, 8), Stone.WHITE),
                StonePlacement(Point(0, 7), Stone.WHITE),
                StonePlacement(Point(1, 7), Stone.BLACK),
                StonePlacement(Point(2, 7), Stone.BLACK),
                StonePlacement(Point(0, 6), Stone.WHITE),
                StonePlacement(Point(1, 6), Stone.WHITE),
            ),
            moves = listOf(Move(Point(3, 7), Stone.BLACK)),
            correctSequence = listOf(Move(Point(3, 7), Stone.BLACK)),
            difficulty = Difficulty.BEGINNER,
            description = "黑棋在B8、C8，在D8做眼活棋"
        ),
        createLifePuzzle(
            id = "ld_003",
            title = "简单-直三",
            boardSize = 9,
            initialStones = listOf(
                StonePlacement(Point(4, 6), Stone.WHITE),
                StonePlacement(Point(4, 4), Stone.WHITE),
                StonePlacement(Point(3, 5), Stone.WHITE),
                StonePlacement(Point(5, 5), Stone.WHITE),
            ),
            moves = listOf(Move(Point(4, 5), Stone.BLACK)),
            correctSequence = listOf(Move(Point(4, 5), Stone.BLACK)),
            difficulty = Difficulty.EASY,
            description = "直三型，中心点眼做活"
        ),
        createLifePuzzle(
            id = "ld_004",
            title = "简单-板六",
            boardSize = 9,
            initialStones = listOf(
                StonePlacement(Point(3, 4), Stone.WHITE),
                StonePlacement(Point(4, 4), Stone.WHITE),
                StonePlacement(Point(5, 4), Stone.WHITE),
                StonePlacement(Point(3, 6), Stone.WHITE),
                StonePlacement(Point(4, 6), Stone.WHITE),
                StonePlacement(Point(5, 6), Stone.WHITE),
            ),
            moves = listOf(Move(Point(4, 5), Stone.BLACK)),
            correctSequence = listOf(Move(Point(4, 5), Stone.BLACK)),
            difficulty = Difficulty.EASY,
            description = "板六型，中心点杀"
        ),
        createLifePuzzle(
            id = "ld_005",
            title = "中级-刀把五",
            boardSize = 9,
            initialStones = listOf(
                StonePlacement(Point(4, 6), Stone.WHITE),
                StonePlacement(Point(3, 5), Stone.WHITE),
                StonePlacement(Point(5, 5), Stone.WHITE),
                StonePlacement(Point(3, 4), Stone.WHITE),
                StonePlacement(Point(5, 4), Stone.WHITE),
                StonePlacement(Point(4, 3), Stone.WHITE),
            ),
            moves = listOf(Move(Point(4, 5), Stone.BLACK)),
            correctSequence = listOf(Move(Point(4, 5), Stone.BLACK)),
            difficulty = Difficulty.INTERMEDIATE,
            description = "刀把五，中心点杀"
        ),
        createLifePuzzle(
            id = "ld_006",
            title = "中级-梅花五",
            boardSize = 9,
            initialStones = listOf(
                StonePlacement(Point(4, 6), Stone.WHITE),
                StonePlacement(Point(3, 5), Stone.WHITE),
                StonePlacement(Point(5, 5), Stone.WHITE),
                StonePlacement(Point(3, 4), Stone.WHITE),
                StonePlacement(Point(5, 4), Stone.WHITE),
                StonePlacement(Point(4, 4), Stone.WHITE),
                StonePlacement(Point(4, 3), Stone.WHITE),
            ),
            moves = listOf(Move(Point(4, 5), Stone.BLACK)),
            correctSequence = listOf(Move(Point(4, 5), Stone.BLACK)),
            difficulty = Difficulty.INTERMEDIATE,
            description = "梅花五，中心点杀"
        ),
        createLifePuzzle(
            id = "ld_007",
            title = "困难-活棋",
            boardSize = 13,
            initialStones = listOf(
                StonePlacement(Point(5, 10), Stone.BLACK),
                StonePlacement(Point(6, 10), Stone.BLACK),
                StonePlacement(Point(7, 10), Stone.BLACK),
                StonePlacement(Point(5, 9), Stone.BLACK),
                StonePlacement(Point(7, 9), Stone.BLACK),
                StonePlacement(Point(5, 8), Stone.BLACK),
                StonePlacement(Point(6, 8), Stone.BLACK),
                StonePlacement(Point(7, 8), Stone.BLACK),
                StonePlacement(Point(4, 10), Stone.WHITE),
                StonePlacement(Point(8, 10), Stone.WHITE),
                StonePlacement(Point(4, 9), Stone.WHITE),
                StonePlacement(Point(8, 9), Stone.WHITE),
                StonePlacement(Point(4, 8), Stone.WHITE),
                StonePlacement(Point(8, 8), Stone.WHITE),
                StonePlacement(Point(5, 7), Stone.WHITE),
                StonePlacement(Point(7, 7), Stone.WHITE),
            ),
            moves = listOf(Move(Point(6, 9), Stone.BLACK)),
            correctSequence = listOf(Move(Point(6, 9), Stone.BLACK)),
            difficulty = Difficulty.HARD,
            description = "复杂活棋，中间做眼"
        ),
        createLifePuzzle(
            id = "ld_008",
            title = "高级-金柜角",
            boardSize = 13,
            initialStones = listOf(
                StonePlacement(Point(5, 11), Stone.WHITE),
                StonePlacement(Point(6, 11), Stone.WHITE),
                StonePlacement(Point(7, 11), Stone.WHITE),
                StonePlacement(Point(5, 10), Stone.WHITE),
                StonePlacement(Point(7, 10), Stone.WHITE),
                StonePlacement(Point(5, 9), Stone.WHITE),
                StonePlacement(Point(6, 9), Stone.WHITE),
                StonePlacement(Point(7, 9), Stone.WHITE),
            ),
            moves = listOf(Move(Point(6, 10), Stone.BLACK)),
            correctSequence = listOf(Move(Point(6, 10), Stone.BLACK)),
            difficulty = Difficulty.ADVANCED,
            description = "金柜角点眼"
        )
    )

    private fun getTesujiPuzzles(): List<Puzzle> = listOf(
        createTesujiPuzzle(
            id = "ts_001",
            title = "手筋-打吃",
            boardSize = 9,
            initialStones = listOf(
                StonePlacement(Point(3, 6), Stone.BLACK),
                StonePlacement(Point(4, 6), Stone.BLACK),
                StonePlacement(Point(5, 6), Stone.WHITE),
                StonePlacement(Point(4, 5), Stone.WHITE),
                StonePlacement(Point(5, 5), Stone.WHITE),
            ),
            moves = listOf(Move(Point(5, 6), Stone.BLACK)),
            correctSequence = listOf(Move(Point(5, 6), Stone.BLACK)),
            difficulty = Difficulty.BEGINNER,
            description = "打吃白棋"
        ),
        createTesujiPuzzle(
            id = "ts_002",
            title = "手筋-枷吃",
            boardSize = 9,
            initialStones = listOf(
                StonePlacement(Point(4, 5), Stone.WHITE),
                StonePlacement(Point(4, 6), Stone.WHITE),
                StonePlacement(Point(3, 5), Stone.BLACK),
                StonePlacement(Point(5, 5), Stone.BLACK),
                StonePlacement(Point(5, 6), Stone.BLACK),
            ),
            moves = listOf(Move(Point(3, 6), Stone.BLACK)),
            correctSequence = listOf(Move(Point(3, 6), Stone.BLACK)),
            difficulty = Difficulty.EASY,
            description = "枷吃白棋"
        ),
        createTesujiPuzzle(
            id = "ts_003",
            title = "手筋-倒扑",
            boardSize = 9,
            initialStones = listOf(
                StonePlacement(Point(4, 6), Stone.WHITE),
                StonePlacement(Point(4, 5), Stone.BLACK),
                StonePlacement(Point(3, 5), Stone.BLACK),
                StonePlacement(Point(5, 5), Stone.BLACK),
                StonePlacement(Point(3, 4), Stone.WHITE),
                StonePlacement(Point(5, 4), Stone.WHITE),
            ),
            moves = listOf(Move(Point(4, 4), Stone.WHITE)),
            correctSequence = listOf(Move(Point(4, 4), Stone.WHITE)),
            difficulty = Difficulty.INTERMEDIATE,
            description = "倒扑吃黑棋"
        ),
        createTesujiPuzzle(
            id = "ts_004",
            title = "手筋-接不归",
            boardSize = 9,
            initialStones = listOf(
                StonePlacement(Point(3, 5), Stone.WHITE),
                StonePlacement(Point(4, 5), Stone.WHITE),
                StonePlacement(Point(5, 5), Stone.WHITE),
                StonePlacement(Point(3, 6), Stone.BLACK),
                StonePlacement(Point(4, 6), Stone.BLACK),
                StonePlacement(Point(5, 6), Stone.WHITE),
                StonePlacement(Point(6, 5), Stone.WHITE),
            ),
            moves = listOf(Move(Point(4, 4), Stone.BLACK)),
            correctSequence = listOf(Move(Point(4, 4), Stone.BLACK)),
            difficulty = Difficulty.HARD,
            description = "打吃形成接不归"
        )
    )

    private fun getYosePuzzles(): List<Puzzle> = listOf(
        createYosePuzzle(
            id = "ys_001",
            title = "官子-先手",
            boardSize = 9,
            initialStones = listOf(
                StonePlacement(Point(2, 7), Stone.WHITE),
                StonePlacement(Point(3, 7), Stone.WHITE),
                StonePlacement(Point(4, 7), Stone.WHITE),
                StonePlacement(Point(2, 6), Stone.WHITE),
                StonePlacement(Point(4, 6), Stone.WHITE),
                StonePlacement(Point(2, 5), Stone.WHITE),
                StonePlacement(Point(4, 5), Stone.WHITE),
            ),
            moves = listOf(Move(Point(3, 6), Stone.BLACK)),
            correctSequence = listOf(Move(Point(3, 6), Stone.BLACK)),
            difficulty = Difficulty.BEGINNER,
            description = "先手官子"
        ),
        createYosePuzzle(
            id = "ys_002",
            title = "官子-后手",
            boardSize = 9,
            initialStones = listOf(
                StonePlacement(Point(3, 7), Stone.WHITE),
                StonePlacement(Point(4, 7), Stone.WHITE),
                StonePlacement(Point(5, 7), Stone.WHITE),
                StonePlacement(Point(3, 6), Stone.WHITE),
                StonePlacement(Point(5, 6), Stone.WHITE),
                StonePlacement(Point(3, 5), Stone.WHITE),
                StonePlacement(Point(5, 5), Stone.WHITE),
            ),
            moves = listOf(Move(Point(4, 6), Stone.BLACK)),
            correctSequence = listOf(Move(Point(4, 6), Stone.BLACK)),
            difficulty = Difficulty.EASY,
            description = "后手官子"
        ),
        createYosePuzzle(
            id = "ys_003",
            title = "官子-二一路",
            boardSize = 13,
            initialStones = listOf(
                StonePlacement(Point(5, 11), Stone.WHITE),
                StonePlacement(Point(6, 11), Stone.WHITE),
                StonePlacement(Point(7, 11), Stone.WHITE),
                StonePlacement(Point(5, 10), Stone.WHITE),
                StonePlacement(Point(7, 10), Stone.WHITE),
                StonePlacement(Point(5, 9), Stone.WHITE),
                StonePlacement(Point(7, 9), Stone.WHITE),
            ),
            moves = listOf(Move(Point(6, 10), Stone.BLACK)),
            correctSequence = listOf(Move(Point(6, 10), Stone.BLACK)),
            difficulty = Difficulty.INTERMEDIATE,
            description = "二一路官子"
        ),
        createYosePuzzle(
            id = "ys_004",
            title = "官子-逆收",
            boardSize = 13,
            initialStones = listOf(
                StonePlacement(Point(4, 10), Stone.WHITE),
                StonePlacement(Point(5, 10), Stone.WHITE),
                StonePlacement(Point(6, 10), Stone.WHITE),
                StonePlacement(Point(4, 9), Stone.WHITE),
                StonePlacement(Point(6, 9), Stone.WHITE),
                StonePlacement(Point(4, 8), Stone.WHITE),
                StonePlacement(Point(6, 8), Stone.WHITE),
            ),
            moves = listOf(Move(Point(5, 9), Stone.BLACK)),
            correctSequence = listOf(Move(Point(5, 9), Stone.BLACK)),
            difficulty = Difficulty.HARD,
            description = "逆收官子"
        )
    )

    private fun createLifePuzzle(
        id: String,
        title: String,
        boardSize: Int,
        initialStones: List<StonePlacement>,
        moves: List<Move>,
        correctSequence: List<Move>,
        difficulty: Difficulty,
        description: String
    ): Puzzle {
        val solutionTree = buildSolutionTree(correctSequence, description)
        return Puzzle(
            id = id,
            title = title,
            boardSize = boardSize,
            initialStones = initialStones,
            solutionTree = solutionTree,
            difficulty = difficulty,
            category = PuzzleCategory.LIFE_AND_DEATH,
            description = description,
            nextPlayerColor = Stone.BLACK
        )
    }

    private fun createTesujiPuzzle(
        id: String,
        title: String,
        boardSize: Int,
        initialStones: List<StonePlacement>,
        moves: List<Move>,
        correctSequence: List<Move>,
        difficulty: Difficulty,
        description: String
    ): Puzzle {
        val solutionTree = buildSolutionTree(correctSequence, description)
        return Puzzle(
            id = id,
            title = title,
            boardSize = boardSize,
            initialStones = initialStones,
            solutionTree = solutionTree,
            difficulty = difficulty,
            category = PuzzleCategory.TESUJI,
            description = description,
            nextPlayerColor = Stone.BLACK
        )
    }

    private fun createYosePuzzle(
        id: String,
        title: String,
        boardSize: Int,
        initialStones: List<StonePlacement>,
        moves: List<Move>,
        correctSequence: List<Move>,
        difficulty: Difficulty,
        description: String
    ): Puzzle {
        val solutionTree = buildSolutionTree(correctSequence, description)
        return Puzzle(
            id = id,
            title = title,
            boardSize = boardSize,
            initialStones = initialStones,
            solutionTree = solutionTree,
            difficulty = difficulty,
            category = PuzzleCategory.YOSE,
            description = description,
            nextPlayerColor = Stone.BLACK
        )
    }

    private fun buildSolutionTree(correctSequence: List<Move>, description: String): MoveNode {
        if (correctSequence.isEmpty()) {
            return MoveNode(null, true, emptyList(), hint = description)
        }

        var child: MoveNode? = null
        for (i in correctSequence.indices.reversed()) {
            val move = correctSequence[i]
            val isLast = (i == correctSequence.size - 1)
            val children = if (child != null) listOf(child) else emptyList<MoveNode>()
            child = MoveNode(
                move = move,
                isCorrect = true,
                children = children,
                hint = if (isLast) description else null,
                comment = if (isLast) description else null
            )
        }

        return child ?: MoveNode(null, true, emptyList(), hint = description)
    }
}
