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

    // 死活题
    private fun getLifeAndDeathPuzzles(): List<Puzzle> = listOf(
        // 入门级死活题 - 9路
        createLifePuzzle(
            id = "ld_001",
            title = "入门-第一题",
            boardSize = 9,
            initialStones = listOf(
                // 白棋围住的角落
                StonePlacement(Point(0, 0), Stone.WHITE),
                StonePlacement(Point(1, 0), Stone.WHITE),
                StonePlacement(Point(2, 0), Stone.WHITE),
                StonePlacement(Point(0, 1), Stone.WHITE),
                StonePlacement(Point(1, 1), Stone.BLACK), // 黑棋在里面
                StonePlacement(Point(0, 2), Stone.WHITE),
            ),
            moves = listOf(
                Move(Point(1, 1), Stone.BLACK), // 第一手提
            ),
            correctSequence = listOf(
                Move(Point(1, 1), Stone.BLACK),
            ),
            difficulty = Difficulty.BEGINNER,
            description = "入门-白棋角部，做活"
        ),
        createLifePuzzle(
            id = "ld_002",
            title = "入门-第二题",
            boardSize = 9,
            initialStones = listOf(
                StonePlacement(Point(1, 0), Stone.WHITE),
                StonePlacement(Point(2, 0), Stone.WHITE),
                StonePlacement(Point(0, 1), Stone.WHITE),
                StonePlacement(Point(1, 1), Stone.BLACK),
                StonePlacement(Point(2, 1), Stone.BLACK),
                StonePlacement(Point(0, 2), Stone.WHITE),
                StonePlacement(Point(1, 2), Stone.WHITE),
            ),
            moves = listOf(
                Move(Point(1, 1), Stone.BLACK),
            ),
            correctSequence = listOf(
                Move(Point(1, 1), Stone.BLACK),
            ),
            difficulty = Difficulty.BEGINNER,
            description = "入门-黑棋做活"
        ),
        // 简单级死活题
        createLifePuzzle(
            id = "ld_003",
            title = "简单死活-直三",
            boardSize = 9,
            initialStones = listOf(
                // 直三形状
                StonePlacement(Point(4, 3), Stone.WHITE),
                StonePlacement(Point(4, 4), Stone.BLACK),
                StonePlacement(Point(4, 5), Stone.WHITE),
                StonePlacement(Point(3, 4), Stone.WHITE),
                StonePlacement(Point(5, 4), Stone.WHITE),
            ),
            moves = listOf(
                Move(Point(4, 4), Stone.BLACK),
            ),
            correctSequence = listOf(
                Move(Point(4, 4), Stone.BLACK), // 点在中间，做活
            ),
            difficulty = Difficulty.EASY,
            description = "简单-直三型，做活要点"
        ),
        createLifePuzzle(
            id = "ld_004",
            title = "简单死活-扳",
            boardSize = 9,
            initialStones = listOf(
                StonePlacement(Point(3, 3), Stone.WHITE),
                StonePlacement(Point(4, 3), Stone.WHITE),
                StonePlacement(Point(5, 3), Stone.WHITE),
                StonePlacement(Point(3, 4), Stone.BLACK),
                StonePlacement(Point(4, 4), Stone.BLACK),
                StonePlacement(Point(5, 4), Stone.BLACK),
                StonePlacement(Point(3, 5), Stone.WHITE),
                StonePlacement(Point(4, 5), Stone.WHITE),
                StonePlacement(Point(5, 5), Stone.WHITE),
            ),
            moves = listOf(
                Move(Point(4, 4), Stone.BLACK),
            ),
            correctSequence = listOf(
                Move(Point(4, 4), Stone.BLACK),
            ),
            difficulty = Difficulty.EASY,
            description = "简单-扳过做活"
        ),
        // 中级死活题
        createLifePuzzle(
            id = "ld_005",
            title = "中级-刀把五",
            boardSize = 9,
            initialStones = listOf(
                StonePlacement(Point(4, 2), Stone.WHITE),
                StonePlacement(Point(3, 3), Stone.WHITE),
                StonePlacement(Point(4, 3), Stone.WHITE),
                StonePlacement(Point(5, 3), Stone.WHITE),
                StonePlacement(Point(4, 4), Stone.BLACK),
                StonePlacement(Point(3, 4), Stone.WHITE),
                StonePlacement(Point(5, 4), Stone.WHITE),
                StonePlacement(Point(4, 5), Stone.WHITE),
            ),
            moves = listOf(
                Move(Point(4, 4), Stone.BLACK),
            ),
            correctSequence = listOf(
                Move(Point(4, 4), Stone.BLACK),
            ),
            difficulty = Difficulty.INTERMEDIATE,
            description = "中级-刀把五，杀死白棋"
        ),
        createLifePuzzle(
            id = "ld_006",
            title = "中级-梅花五",
            boardSize = 9,
            initialStones = listOf(
                StonePlacement(Point(4, 2), Stone.WHITE),
                StonePlacement(Point(3, 3), Stone.WHITE),
                StonePlacement(Point(5, 3), Stone.WHITE),
                StonePlacement(Point(4, 3), Stone.BLACK),
                StonePlacement(Point(3, 4), Stone.WHITE),
                StonePlacement(Point(5, 4), Stone.WHITE),
                StonePlacement(Point(4, 4), Stone.WHITE),
                StonePlacement(Point(4, 5), Stone.WHITE),
            ),
            moves = listOf(
                Move(Point(4, 3), Stone.BLACK),
            ),
            correctSequence = listOf(
                Move(Point(4, 3), Stone.BLACK),
            ),
            difficulty = Difficulty.INTERMEDIATE,
            description = "中级-梅花五，聚杀要点"
        ),
        // 困难级死活题
        createLifePuzzle(
            id = "ld_007",
            title = "困难-大头鬼",
            boardSize = 13,
            initialStones = listOf(
                // 模拟大头鬼棋型
                StonePlacement(Point(5, 4), Stone.WHITE),
                StonePlacement(Point(6, 4), Stone.WHITE),
                StonePlacement(Point(7, 4), Stone.WHITE),
                StonePlacement(Point(5, 5), Stone.BLACK),
                StonePlacement(Point(6, 5), Stone.WHITE),
                StonePlacement(Point(7, 5), Stone.WHITE),
                StonePlacement(Point(5, 6), Stone.BLACK),
                StonePlacement(Point(6, 6), Stone.WHITE),
                StonePlacement(Point(7, 6), Stone.WHITE),
                StonePlacement(Point(5, 7), Stone.BLACK),
                StonePlacement(Point(6, 7), Stone.BLACK),
                StonePlacement(Point(4, 5), Stone.WHITE),
                StonePlacement(Point(4, 6), Stone.WHITE),
                StonePlacement(Point(4, 7), Stone.WHITE),
            ),
            moves = listOf(
                Move(Point(5, 5), Stone.BLACK),
            ),
            correctSequence = listOf(
                Move(Point(5, 5), Stone.BLACK),
            ),
            difficulty = Difficulty.HARD,
            description = "困难-大头鬼杀法"
        ),
        // 高级死活题 - 13路
        createLifePuzzle(
            id = "ld_008",
            title = "高级-金柜角",
            boardSize = 13,
            initialStones = listOf(
                // 金柜角外围
                StonePlacement(Point(5, 3), Stone.WHITE),
                StonePlacement(Point(6, 3), Stone.WHITE),
                StonePlacement(Point(7, 3), Stone.WHITE),
                StonePlacement(Point(5, 4), Stone.WHITE),
                StonePlacement(Point(7, 4), Stone.WHITE),
                StonePlacement(Point(5, 5), Stone.WHITE),
                StonePlacement(Point(6, 5), Stone.WHITE),
                StonePlacement(Point(7, 5), Stone.WHITE),
                StonePlacement(Point(6, 4), Stone.BLACK),
            ),
            moves = listOf(
                Move(Point(6, 4), Stone.BLACK),
            ),
            correctSequence = listOf(
                Move(Point(6, 4), Stone.BLACK),
            ),
            difficulty = Difficulty.ADVANCED,
            description = "高级-金柜角变化"
        )
    )

    // 手筋题
    private fun getTesujiPuzzles(): List<Puzzle> = listOf(
        createTesujiPuzzle(
            id = "ts_001",
            title = "手筋-征子",
            boardSize = 9,
            initialStones = listOf(
                StonePlacement(Point(4, 3), Stone.BLACK),
                StonePlacement(Point(4, 4), Stone.WHITE),
                StonePlacement(Point(4, 5), Stone.WHITE),
                StonePlacement(Point(3, 4), Stone.WHITE),
                StonePlacement(Point(5, 4), Stone.WHITE),
                StonePlacement(Point(3, 6), Stone.WHITE),
                StonePlacement(Point(5, 6), Stone.WHITE),
            ),
            moves = listOf(
                Move(Point(4, 6), Stone.BLACK),
            ),
            correctSequence = listOf(
                Move(Point(4, 6), Stone.BLACK),
            ),
            difficulty = Difficulty.BEGINNER,
            description = "入门-征子手筋"
        ),
        createTesujiPuzzle(
            id = "ts_002",
            title = "手筋-枷吃",
            boardSize = 9,
            initialStones = listOf(
                StonePlacement(Point(4, 3), Stone.BLACK),
                StonePlacement(Point(4, 4), Stone.WHITE),
                StonePlacement(Point(4, 5), Stone.WHITE),
                StonePlacement(Point(3, 4), Stone.WHITE),
                StonePlacement(Point(5, 3), Stone.BLACK),
                StonePlacement(Point(5, 4), Stone.BLACK),
                StonePlacement(Point(5, 5), Stone.WHITE),
            ),
            moves = listOf(
                Move(Point(5, 6), Stone.BLACK),
            ),
            correctSequence = listOf(
                Move(Point(5, 6), Stone.BLACK),
            ),
            difficulty = Difficulty.EASY,
            description = "简单-枷吃手筋"
        ),
        createTesujiPuzzle(
            id = "ts_003",
            title = "手筋-倒扑",
            boardSize = 9,
            initialStones = listOf(
                StonePlacement(Point(3, 3), Stone.WHITE),
                StonePlacement(Point(4, 3), Stone.BLACK),
                StonePlacement(Point(5, 3), Stone.WHITE),
                StonePlacement(Point(3, 4), Stone.BLACK),
                StonePlacement(Point(4, 4), Stone.WHITE),
                StonePlacement(Point(5, 4), Stone.WHITE),
                StonePlacement(Point(3, 5), Stone.BLACK),
                StonePlacement(Point(4, 5), Stone.BLACK),
                StonePlacement(Point(5, 5), Stone.WHITE),
            ),
            moves = listOf(
                Move(Point(4, 4), Stone.WHITE),
            ),
            correctSequence = listOf(
                Move(Point(4, 4), Stone.WHITE),
            ),
            difficulty = Difficulty.INTERMEDIATE,
            description = "中级-倒扑吃子"
        ),
        createTesujiPuzzle(
            id = "ts_004",
            title = "手筋-接不归",
            boardSize = 9,
            initialStones = listOf(
                StonePlacement(Point(2, 4), Stone.BLACK),
                StonePlacement(Point(3, 4), Stone.BLACK),
                StonePlacement(Point(4, 4), Stone.WHITE),
                StonePlacement(Point(3, 3), Stone.WHITE),
                StonePlacement(Point(3, 5), Stone.WHITE),
                StonePlacement(Point(5, 4), Stone.WHITE),
            ),
            moves = listOf(
                Move(Point(4, 4), Stone.BLACK),
            ),
            correctSequence = listOf(
                Move(Point(4, 4), Stone.BLACK),
            ),
            difficulty = Difficulty.HARD,
            description = "困难-接不归手筋"
        )
    )

    // 官子题
    private fun getYosePuzzles(): List<Puzzle> = listOf(
        createYosePuzzle(
            id = "ys_001",
            title = "官子-先手官子",
            boardSize = 9,
            initialStones = listOf(
                StonePlacement(Point(2, 2), Stone.WHITE),
                StonePlacement(Point(3, 2), Stone.WHITE),
                StonePlacement(Point(4, 2), Stone.WHITE),
                StonePlacement(Point(2, 3), Stone.WHITE),
                StonePlacement(Point(3, 3), Stone.BLACK),
                StonePlacement(Point(4, 3), Stone.WHITE),
                StonePlacement(Point(2, 4), Stone.WHITE),
                StonePlacement(Point(3, 4), Stone.WHITE),
                StonePlacement(Point(4, 4), Stone.WHITE),
            ),
            moves = listOf(
                Move(Point(3, 3), Stone.WHITE),
            ),
            correctSequence = listOf(
                Move(Point(3, 3), Stone.WHITE),
            ),
            difficulty = Difficulty.BEGINNER,
            description = "入门-先手官子"
        ),
        createYosePuzzle(
            id = "ys_002",
            title = "官子-后手官子",
            boardSize = 9,
            initialStones = listOf(
                StonePlacement(Point(2, 2), Stone.WHITE),
                StonePlacement(Point(3, 2), Stone.WHITE),
                StonePlacement(Point(4, 2), Stone.WHITE),
                StonePlacement(Point(2, 3), Stone.WHITE),
                StonePlacement(Point(3, 3), Stone.EMPTY),
                StonePlacement(Point(4, 3), Stone.WHITE),
                StonePlacement(Point(2, 4), Stone.WHITE),
                StonePlacement(Point(3, 4), Stone.WHITE),
                StonePlacement(Point(4, 4), Stone.WHITE),
            ),
            moves = listOf(
                Move(Point(3, 3), Stone.BLACK),
            ),
            correctSequence = listOf(
                Move(Point(3, 3), Stone.BLACK),
            ),
            difficulty = Difficulty.EASY,
            description = "简单-后手官子"
        ),
        createYosePuzzle(
            id = "ys_003",
            title = "官子-二一路",
            boardSize = 13,
            initialStones = listOf(
                StonePlacement(Point(5, 4), Stone.WHITE),
                StonePlacement(Point(6, 4), Stone.WHITE),
                StonePlacement(Point(5, 5), Stone.WHITE),
                StonePlacement(Point(7, 4), Stone.WHITE),
                StonePlacement(Point(6, 5), Stone.WHITE),
                StonePlacement(Point(5, 6), Stone.WHITE),
                StonePlacement(Point(6, 6), Stone.WHITE),
            ),
            moves = listOf(
                Move(Point(6, 5), Stone.BLACK),
            ),
            correctSequence = listOf(
                Move(Point(6, 5), Stone.BLACK),
            ),
            difficulty = Difficulty.INTERMEDIATE,
            description = "中级-二一路官子"
        ),
        createYosePuzzle(
            id = "ys_004",
            title = "官子-逆收官",
            boardSize = 13,
            initialStones = listOf(
                StonePlacement(Point(4, 4), Stone.WHITE),
                StonePlacement(Point(5, 4), Stone.WHITE),
                StonePlacement(Point(6, 4), Stone.WHITE),
                StonePlacement(Point(4, 5), Stone.WHITE),
                StonePlacement(Point(6, 5), Stone.WHITE),
                StonePlacement(Point(4, 6), Stone.WHITE),
                StonePlacement(Point(5, 6), Stone.WHITE),
                StonePlacement(Point(6, 6), Stone.WHITE),
            ),
            moves = listOf(
                Move(Point(5, 5), Stone.BLACK),
            ),
            correctSequence = listOf(
                Move(Point(5, 5), Stone.BLACK),
            ),
            difficulty = Difficulty.HARD,
            description = "困难-逆收官"
        )
    )

    // Helper functions to create puzzles
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
            return MoveNode(null, true, emptyList<MoveNode>(), hint = description)
        }

        // Build chain from end to start
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

        return child ?: MoveNode(null, true, emptyList<MoveNode>(), hint = description)
    }
}
