package com.gopuzzle.app.data.repository

import android.content.Context
import com.gopuzzle.app.data.sgf.SgfParser
import com.gopuzzle.app.domain.model.*
import java.io.BufferedReader
import java.io.InputStreamReader

class PuzzleRepository(private val context: Context? = null) {
    private val puzzles by lazy { loadAllPuzzles() }
    private val progressMap = mutableMapOf<String, PuzzleProgress>()

    private fun loadAllPuzzles(): List<Puzzle> {
        val allPuzzles = mutableListOf<Puzzle>()

        allPuzzles.addAll(SamplePuzzles.getAll())

        loadSgfPuzzles().let { allPuzzles.addAll(it) }

        return allPuzzles
    }

    private fun loadSgfPuzzles(): List<Puzzle> {
        if (context == null) return emptyList()

        return try {
            val parser = SgfParser()
            val puzzles = mutableListOf<Puzzle>()

            val assetManager = context.assets
            val files = assetManager.list("puzzles") ?: emptyArray()

            files.filter { it.endsWith(".sgf") }.forEach { filename ->
                try {
                    val inputStream = assetManager.open("puzzles/$filename")
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val content = reader.readText()
                    reader.close()

                    val parsedPuzzles = parser.parse(content)
                    puzzles.addAll(parsedPuzzles)
                } catch (e: Exception) {
                    // Ignore single file errors
                }
            }

            puzzles
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getAllPuzzles(): List<Puzzle> = puzzles

    fun getPuzzlesByCategory(category: PuzzleCategory): List<Puzzle> =
        puzzles.filter { it.category == category }

    fun getPuzzlesByDifficulty(difficulty: Difficulty): List<Puzzle> =
        puzzles.filter { it.difficulty == difficulty }

    fun getPuzzlesByCategoryAndDifficulty(
        category: PuzzleCategory?,
        difficulty: Difficulty?
    ): List<Puzzle> {
        var result = puzzles
        category?.let { result = result.filter { p -> p.category == it } }
        difficulty?.let { result = result.filter { p -> p.difficulty == it } }
        return result
    }

    fun getPuzzleById(id: String): Puzzle? = puzzles.find { it.id == id }

    fun getNextPuzzle(currentId: String, category: PuzzleCategory?, difficulty: Difficulty?): Puzzle? {
        val filteredPuzzles = getPuzzlesByCategoryAndDifficulty(category, difficulty)
        val currentIndex = filteredPuzzles.indexOfFirst { it.id == currentId }
        return if (currentIndex >= 0 && currentIndex < filteredPuzzles.size - 1) {
            filteredPuzzles[currentIndex + 1]
        } else {
            filteredPuzzles.firstOrNull()
        }
    }

    fun getPreviousPuzzle(currentId: String, category: PuzzleCategory?, difficulty: Difficulty?): Puzzle? {
        val filteredPuzzles = getPuzzlesByCategoryAndDifficulty(category, difficulty)
        val currentIndex = filteredPuzzles.indexOfFirst { it.id == currentId }
        return if (currentIndex > 0) {
            filteredPuzzles[currentIndex - 1]
        } else {
            null
        }
    }

    fun getProgress(puzzleId: String): PuzzleProgress? = progressMap[puzzleId]

    fun saveProgress(progress: PuzzleProgress) {
        progressMap[progress.puzzleId] = progress
    }

    fun markAsSolved(puzzleId: String) {
        val current = progressMap[puzzleId]
        progressMap[puzzleId] = PuzzleProgress(
            puzzleId = puzzleId,
            isSolved = true,
            isWrong = false,
            isFavorite = current?.isFavorite ?: false,
            attemptCount = (current?.attemptCount ?: 0) + 1,
            lastAttemptTime = System.currentTimeMillis()
        )
    }

    fun markAsWrong(puzzleId: String) {
        val current = progressMap[puzzleId]
        progressMap[puzzleId] = PuzzleProgress(
            puzzleId = puzzleId,
            isSolved = false,
            isWrong = true,
            isFavorite = current?.isFavorite ?: false,
            attemptCount = (current?.attemptCount ?: 0) + 1,
            lastAttemptTime = System.currentTimeMillis()
        )
    }

    fun toggleFavorite(puzzleId: String): Boolean {
        val current = progressMap[puzzleId]
        val newFavorite = !(current?.isFavorite ?: false)
        progressMap[puzzleId] = PuzzleProgress(
            puzzleId = puzzleId,
            isSolved = current?.isSolved ?: false,
            isWrong = current?.isWrong ?: false,
            isFavorite = newFavorite,
            attemptCount = current?.attemptCount ?: 0,
            lastAttemptTime = current?.lastAttemptTime ?: System.currentTimeMillis()
        )
        return newFavorite
    }

    fun getFavoritePuzzles(): List<Puzzle> {
        return puzzles.filter { progressMap[it.id]?.isFavorite == true }
    }

    fun getWrongPuzzles(): List<Puzzle> {
        return puzzles.filter { progressMap[it.id]?.isWrong == true }
    }

    fun getSolvedPuzzles(): List<Puzzle> {
        return puzzles.filter { progressMap[it.id]?.isSolved == true }
    }

    fun getStatistics(): PuzzleStatistics {
        val total = puzzles.size
        val solved = progressMap.values.count { it.isSolved }
        val wrong = progressMap.values.count { it.isWrong }
        val totalAttempts = progressMap.values.sumOf { it.attemptCount }

        val categoryStats = PuzzleCategory.entries.associateWith { category ->
            val categoryPuzzles = puzzles.filter { it.category == category }
            val solvedCount = categoryPuzzles.count { progressMap[it.id]?.isSolved == true }
            CategoryStat(category.displayName, categoryPuzzles.size, solvedCount)
        }

        return PuzzleStatistics(total, solved, wrong, totalAttempts, categoryStats)
    }
}

data class PuzzleStatistics(
    val totalPuzzles: Int,
    val solvedCount: Int,
    val wrongCount: Int,
    val totalAttempts: Int,
    val categoryStats: Map<PuzzleCategory, CategoryStat>
) {
    val solvedRate: Float get() = if (totalPuzzles > 0) solvedCount.toFloat() / totalPuzzles else 0f
}

data class CategoryStat(
    val categoryName: String,
    val total: Int,
    val solved: Int
) {
    val solvedRate: Float get() = if (total > 0) solved.toFloat() / total else 0f
}
