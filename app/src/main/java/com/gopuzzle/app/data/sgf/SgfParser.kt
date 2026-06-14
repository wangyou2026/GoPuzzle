package com.gopuzzle.app.data.sgf

import com.gopuzzle.app.domain.model.*

class SgfParser {

    fun parse(sgfContent: String): Puzzle? {
        return try {
            val root = parseTree(sgfContent)
            root?.let { buildPuzzle(it) }
        } catch (e: Exception) {
            null
        }
    }

    private fun parseTree(sgf: String): SgfNode? {
        val root = SgfNode()
        var i = 0

        // Skip to first (
        while (i < sgf.length && sgf[i] != '(') i++
        if (i >= sgf.length) return null
        i++

        // Parse root node
        while (i < sgf.length && sgf[i] != ')') {
            if (sgf[i] == ';') {
                i++
                val node = parseNode(sgf, i)
                if (node != null) {
                    i = node.second
                    root.properties.putAll(node.first)
                    root.children.addAll(node.first.children)
                }
            } else {
                i++
            }
        }

        return root
    }

    private fun parseNode(sgf: String, startIndex: Int): Pair<SgfNode, Int>? {
        val node = SgfNode()
        var i = startIndex

        while (i < sgf.length && sgf[i] != ';' && sgf[i] != ')') {
            // Parse property
            if (sgf[i].isLetter()) {
                val propType = StringBuilder()
                while (i < sgf.length && sgf[i].isLetter()) {
                    propType.append(sgf[i])
                    i++
                }

                // Skip [
                if (i < sgf.length && sgf[i] == '[') i++

                // Parse value
                val value = StringBuilder()
                while (i < sgf.length && sgf[i] != ']') {
                    if (sgf[i] == '\\') {
                        i++
                    }
                    value.append(sgf[i])
                    i++
                }
                i++ // Skip ]

                node.properties[propType.toString()] = value.toString()

                // Handle multiple values (for AB, AW)
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

        // Get board size
        val sizeStr = props["SZ"] ?: "19"
        val boardSize = sizeStr.split("\n").first().toIntOrNull() ?: 19

        // Get initial stones (setup)
        val initialStones = mutableListOf<StonePlacement>()

        // Black stones (AB)
        props["AB"]?.split("\n")?.filter { it.isNotEmpty() }?.forEach { sgfPoint ->
            try {
                initialStones.add(StonePlacement(Point.fromSgf(sgfPoint.trim()), Stone.BLACK))
            } catch (e: Exception) { }
        }

        // White stones (AW)
        props["AW"]?.split("\n")?.filter { it.isNotEmpty() }?.forEach { sgfPoint ->
            try {
                initialStones.add(StonePlacement(Point.fromSgf(sgfPoint.trim()), Stone.WHITE))
            } catch (e: Exception) { }
        }

        // Get difficulty/comment from comment (C)
        val comment = props["C"] ?: ""

        // Parse difficulty from comment if present
        val difficulty = when {
            comment.contains("入门") || comment.contains("初级") -> Difficulty.BEGINNER
            comment.contains("简单") -> Difficulty.EASY
            comment.contains("中级") -> Difficulty.INTERMEDIATE
            comment.contains("困难") || comment.contains("高级") -> Difficulty.HARD
            comment.contains("天王") || comment.contains("超难") -> Difficulty.ADVANCED
            else -> Difficulty.INTERMEDIATE
        }

        // Parse category
        val category = when {
            comment.contains("死活") || comment.contains("死") || comment.contains("活") -> PuzzleCategory.LIFE_AND_DEATH
            comment.contains("手筋") || comment.contains("筋") -> PuzzleCategory.TESUJI
            comment.contains("官子") || comment.contains("收官") -> PuzzleCategory.YOSE
            else -> PuzzleCategory.LIFE_AND_DEATH
        }

        // Get title
        val title = props["GN"] ?: props["ON"] ?: "围棋练习题"

        // Determine next player
        val nextPlayer = when (props["PL"]?.firstOrNull()?.uppercaseChar()) {
            'B' -> Stone.BLACK
            'W' -> Stone.WHITE
            else -> Stone.BLACK
        }

        // Build solution tree (simplified - just the first variation)
        val solutionTree = buildSolutionTree(root, nextPlayer)

        return Puzzle(
            id = "sgf_${title.hashCode()}",
            title = title,
            boardSize = boardSize,
            initialStones = initialStones,
            solutionTree = solutionTree,
            difficulty = difficulty,
            category = category,
            description = comment,
            nextPlayerColor = nextPlayer
        )
    }

    private fun buildSolutionTree(node: SgfNode, currentPlayer: Stone): MoveNode {
        val moveStr = when (currentPlayer) {
            Stone.BLACK -> node.properties["B"]
            Stone.WHITE -> node.properties["W"]
            Stone.EMPTY -> null
        }

        val move = if (moveStr != null && moveStr.isNotEmpty() && moveStr != "tt") {
            Move.fromSgf(moveStr.trim(), currentPlayer)
        } else {
            null
        }

        // Get comment for hint
        val comment = node.properties["C"]
        val hint = extractHint(comment)

        // Check if this move is marked as correct/incorrect
        val isCorrect = !node.properties.containsKey("CR") // CR = Circle = marked as wrong

        // Build children from variations
        val children = node.children.map { child ->
            buildSolutionTree(child, currentPlayer.opposite())
        }

        return MoveNode(
            move = move,
            isCorrect = isCorrect,
            children = children,
            hint = hint,
            comment = comment
        )
    }

    private fun extractHint(comment: String?): String? {
        if (comment == null) return null
        return comment.split("\n").firstOrNull { it.contains("提示") }?.trim()
    }
}

data class SgfNode(
    val properties: MutableMap<String, String> = mutableMapOf(),
    val children: MutableList<SgfNode> = mutableListOf()
)
