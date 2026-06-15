package com.gopuzzle.app.data.sgf

import com.gopuzzle.app.domain.model.*

class SgfParser {

    fun parse(sgfContent: String): List<Puzzle> {
        return try {
            val trees = parseMultipleTrees(sgfContent)
            trees.mapNotNull { buildPuzzle(it) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun parseMultipleTrees(sgf: String): List<SgfNode> {
        val trees = mutableListOf<SgfNode>()
        var i = 0

        while (i < sgf.length) {
            // Find next tree start
            while (i < sgf.length && sgf[i] != '(') i++
            if (i >= sgf.length) break

            // Find matching closing )
            val treeStart = i
            var depth = 0
            while (i < sgf.length) {
                if (sgf[i] == '(') depth++
                if (sgf[i] == ')') depth--
                if (depth == 0) break
                i++
            }

            if (i < sgf.length) {
                val treeContent = sgf.substring(treeStart, i + 1)
                parseTree(treeContent)?.let { trees.add(it) }
            }
            i++
        }

        return trees
    }

    private fun parseTree(sgf: String): SgfNode? {
        val root = SgfNode()
        var i = 0

        while (i < sgf.length) {
            when (sgf[i]) {
                '(' -> {
                    // Find matching )
                    var depth = 0
                    var j = i
                    while (j < sgf.length) {
                        if (sgf[j] == '(') depth++
                        if (sgf[j] == ')') depth--
                        if (depth == 0) break
                        j++
                    }
                    val childContent = sgf.substring(i, j + 1)
                    parseTree(childContent)?.let { root.children.add(it) }
                    i = j + 1
                }
                ';' -> {
                    val nodeResult = parseNode(sgf, i + 1)
                    if (nodeResult != null) {
                        val (node, newIndex) = nodeResult
                        root.properties.putAll(node.properties)
                        i = newIndex
                    } else {
                        i++
                    }
                }
                else -> i++
            }
        }

        return root
    }

    private fun parseNode(sgf: String, startIndex: Int): Pair<SgfNode, Int>? {
        val node = SgfNode()
        var i = startIndex

        while (i < sgf.length && sgf[i] != ';' && sgf[i] != ')' && sgf[i] != '(') {
            if (sgf[i].isLetter()) {
                val propType = StringBuilder()
                while (i < sgf.length && sgf[i].isLetter()) {
                    propType.append(sgf[i])
                    i++
                }

                if (i < sgf.length && sgf[i] == '[') i++

                val value = StringBuilder()
                while (i < sgf.length && sgf[i] != ']') {
                    if (sgf[i] == '\\') {
                        i++
                    }
                    value.append(sgf[i])
                    i++
                }
                i++

                node.properties[propType.toString()] = value.toString()

                while (i < sgf.length && sgf[i] == '[') {
                    i++
                    val extraValue = StringBuilder()
                    while (i < sgf.length && sgf[i] != ']') {
                        if (sgf[i] == '\\') i++
                        extraValue.append(sgf[i])
                        i++
                    }
                    i++
                    node.properties[propType.toString()] += "\n${extraValue}"
                }
            } else {
                i++
            }
        }

        return Pair(node, i)
    }

    private fun buildPuzzle(root: SgfNode): Puzzle? {
        val props = root.properties

        val sizeStr = props["SZ"] ?: "9"
        val boardSize = sizeStr.split("\n").first().toIntOrNull() ?: 9

        val initialStones = mutableListOf<StonePlacement>()
        val moves = mutableListOf<Move>()
        var currentPlayer = Stone.BLACK

        var node: SgfNode? = root
        while (node != null) {
            node.properties["B"]?.let { sgfPoint ->
                if (sgfPoint.isNotEmpty() && sgfPoint != "tt") {
                    try {
                        val point = Point.fromSgf(sgfPoint.trim())
                        if (boardSize <= 13 || (point.x < boardSize && point.y < boardSize)) {
                            initialStones.add(StonePlacement(point, Stone.BLACK))
                            moves.add(Move(point, Stone.BLACK))
                        }
                    } catch (e: Exception) { }
                }
                currentPlayer = Stone.WHITE
            }

            node.properties["W"]?.let { sgfPoint ->
                if (sgfPoint.isNotEmpty() && sgfPoint != "tt") {
                    try {
                        val point = Point.fromSgf(sgfPoint.trim())
                        if (boardSize <= 13 || (point.x < boardSize && point.y < boardSize)) {
                            initialStones.add(StonePlacement(point, Stone.WHITE))
                            moves.add(Move(point, Stone.WHITE))
                        }
                    } catch (e: Exception) { }
                }
                currentPlayer = Stone.BLACK
            }

            node.properties["AB"]?.split("\n")?.filter { it.isNotEmpty() }?.forEach { sgfPoint ->
                try {
                    val point = Point.fromSgf(sgfPoint.trim())
                    if (!initialStones.any { it.point == point }) {
                        initialStones.add(StonePlacement(point, Stone.BLACK))
                    }
                } catch (e: Exception) { }
            }

            node.properties["AW"]?.split("\n")?.filter { it.isNotEmpty() }?.forEach { sgfPoint ->
                try {
                    val point = Point.fromSgf(sgfPoint.trim())
                    if (!initialStones.any { it.point == point }) {
                        initialStones.add(StonePlacement(point, Stone.WHITE))
                    }
                } catch (e: Exception) { }
            }

            node = node.children.firstOrNull()
        }

        val comment = props["C"] ?: ""
        val title = props["GN"] ?: props["ON"] ?: extractTitle(comment)
        val id = "sgf_${title.hashCode()}"

        val difficulty = when {
            comment.contains("入门") || comment.contains("初级") -> Difficulty.BEGINNER
            comment.contains("简单") || comment.contains("容易") -> Difficulty.EASY
            comment.contains("中级") || comment.contains("普通") -> Difficulty.INTERMEDIATE
            comment.contains("困难") || comment.contains("难") -> Difficulty.HARD
            comment.contains("高级") || comment.contains("天王") -> Difficulty.ADVANCED
            else -> Difficulty.INTERMEDIATE
        }

        val category = when {
            comment.contains("死活") || comment.contains("死") || comment.contains("活") -> PuzzleCategory.LIFE_AND_DEATH
            comment.contains("手筋") || comment.contains("筋") -> PuzzleCategory.TESUJI
            comment.contains("官子") || comment.contains("收官") -> PuzzleCategory.YOSE
            else -> PuzzleCategory.LIFE_AND_DEATH
        }

        val nextPlayer = when (props["PL"]?.firstOrNull()?.uppercaseChar()) {
            'B' -> Stone.BLACK
            'W' -> Stone.WHITE
            else -> determineNextPlayer(comment, moves)
        }

        val solutionMoves = extractSolutionMoves(root, nextPlayer)
        val solutionTree = buildSolutionTree(solutionMoves, comment)

        return Puzzle(
            id = id,
            title = title,
            boardSize = boardSize,
            initialStones = initialStones.filter { it.point.x < boardSize && it.point.y < boardSize },
            solutionTree = solutionTree,
            difficulty = difficulty,
            category = category,
            description = comment,
            nextPlayerColor = nextPlayer
        )
    }

    private fun extractTitle(comment: String): String {
        if (comment.contains("-")) {
            return comment.substringBefore("-").trim()
        }
        return "围棋练习题"
    }

    private fun determineNextPlayer(comment: String, moves: List<Move>): Stone {
        if (comment.contains("黑先")) return Stone.BLACK
        if (comment.contains("白先")) return Stone.WHITE
        return if (moves.isEmpty() || moves.last().color == Stone.WHITE) Stone.BLACK else Stone.WHITE
    }

    private fun extractSolutionMoves(root: SgfNode, startingPlayer: Stone): List<Move> {
        val moves = mutableListOf<Move>()
        var player = startingPlayer
        var node: SgfNode? = root

        while (node != null) {
            val moveStr = when (player) {
                Stone.BLACK -> node.properties["B"]
                Stone.WHITE -> node.properties["W"]
                Stone.EMPTY -> null
            }

            if (moveStr != null && moveStr.isNotEmpty() && moveStr != "tt") {
                try {
                    val point = Point.fromSgf(moveStr.trim())
                    moves.add(Move(point, player))
                } catch (e: Exception) { }
            }

            player = player.opposite()
            node = node.children.firstOrNull {
                !it.properties.containsKey("CR") && !it.properties.containsKey("MA")
            } ?: node.children.firstOrNull()
        }

        return moves
    }

    private fun buildSolutionTree(moves: List<Move>, description: String): MoveNode {
        if (moves.isEmpty()) {
            return MoveNode(null, true, emptyList(), hint = description)
        }

        var child: MoveNode? = null
        for (i in moves.indices.reversed()) {
            val move = moves[i]
            val isLast = (i == moves.size - 1)
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

data class SgfNode(
    val properties: MutableMap<String, String> = mutableMapOf(),
    val children: MutableList<SgfNode> = mutableListOf()
)
