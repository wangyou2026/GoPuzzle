package com.gopuzzle.app.ui.puzzle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gopuzzle.app.data.repository.PuzzleRepository
import com.gopuzzle.app.domain.model.*
import com.gopuzzle.app.domain.usecase.GameEngine
import com.gopuzzle.app.domain.usecase.MoveResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PuzzleUiState(
    val puzzle: Puzzle? = null,
    val currentBoard: Board? = null,
    val stones: List<StonePlacement> = emptyList(),
    val moveHistory: List<Move> = emptyList(),
    val currentPlayer: Stone = Stone.BLACK,
    val isComplete: Boolean = false,
    val isCorrect: Boolean = false,
    val showHint: Boolean = false,
    val hintMove: Move? = null,
    val showAnswer: Boolean = false,
    val answerMoves: List<Move> = emptyList(),
    val wrongMove: Move? = null,
    val lastMove: Point? = null,
    val isFavorite: Boolean = false,
    val feedbackMessage: String? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

class PuzzleViewModel : ViewModel() {
    private val repository = PuzzleRepository()

    private val _uiState = MutableStateFlow(PuzzleUiState())
    val uiState: StateFlow<PuzzleUiState> = _uiState.asStateFlow()

    private var gameEngine: GameEngine? = null
    private var solutionNode: MoveNode? = null
    private var currentNode: MoveNode? = null

    fun loadPuzzle(puzzleId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val puzzle = repository.getPuzzleById(puzzleId)
                if (puzzle == null) {
                    _uiState.update { it.copy(isLoading = false, error = "题目不存在") }
                    return@launch
                }

                // Initialize board with initial stones
                val board = Board.fromStones(puzzle.boardSize, puzzle.initialStones)
                gameEngine = GameEngine(board)

                // Get solution tree root
                solutionNode = puzzle.solutionTree
                currentNode = solutionNode

                // Get progress
                val progress = repository.getProgress(puzzleId)

                _uiState.update {
                    it.copy(
                        puzzle = puzzle,
                        currentBoard = board,
                        stones = puzzle.initialStones,
                        moveHistory = emptyList(),
                        currentPlayer = puzzle.nextPlayerColor,
                        isComplete = false,
                        isCorrect = false,
                        showHint = false,
                        hintMove = null,
                        showAnswer = false,
                        answerMoves = emptyList(),
                        wrongMove = null,
                        lastMove = null,
                        isFavorite = progress?.isFavorite ?: false,
                        feedbackMessage = null,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun onStoneClick(point: Point) {
        val state = _uiState.value
        if (state.isComplete || state.puzzle == null || gameEngine == null) return

        val move = Move(point, state.currentPlayer)

        // Try to play the move
        val result = gameEngine!!.playMove(move)

        when (result) {
            is MoveResult.Success -> {
                // Update stones
                val newStones = state.currentBoard!!.getAllStones()

                // Check if this move matches the solution
                val expectedNode = findExpectedMove(currentNode, state.currentPlayer)

                if (expectedNode != null && expectedNode.move == move) {
                    // Correct move
                    currentNode = expectedNode

                    // Check if puzzle is complete
                    val isPuzzleComplete = expectedNode.isCorrect &&
                        (expectedNode.children.isEmpty() ||
                         expectedNode.children.all { !it.isCorrect })

                    if (isPuzzleComplete) {
                        repository.markAsSolved(state.puzzle.id)
                        _uiState.update {
                            it.copy(
                                stones = newStones,
                                moveHistory = it.moveHistory + move,
                                currentPlayer = it.currentPlayer.opposite(),
                                isComplete = true,
                                isCorrect = true,
                                lastMove = point,
                                feedbackMessage = "正确！",
                                wrongMove = null
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                stones = newStones,
                                moveHistory = it.moveHistory + move,
                                currentPlayer = it.currentPlayer.opposite(),
                                lastMove = point,
                                feedbackMessage = null,
                                wrongMove = null
                            )
                        }
                    }
                } else {
                    // Wrong move
                    repository.markAsWrong(state.puzzle.id)
                    _uiState.update {
                        it.copy(
                            isComplete = true,
                            isCorrect = false,
                            wrongMove = move,
                            feedbackMessage = "错误，请重试"
                        )
                    }
                }
            }
            is MoveResult.Invalid -> {
                // Invalid move (suicide, ko, occupied, etc.)
                _uiState.update {
                    it.copy(feedbackMessage = result.reason)
                }
            }
        }
    }

    private fun findExpectedMove(node: MoveNode?, player: Stone): MoveNode? {
        if (node == null) return null

        // Check children for the expected move of this player
        for (child in node.children) {
            if (child.move?.color == player) {
                return child
            }
        }
        return null
    }

    fun showHint() {
        val state = _uiState.value
        if (state.isComplete) return

        val expectedNode = findExpectedMove(currentNode, state.currentPlayer)
        _uiState.update {
            it.copy(
                showHint = true,
                hintMove = expectedNode?.move
            )
        }
    }

    fun hideHint() {
        _uiState.update {
            it.copy(showHint = false, hintMove = null)
        }
    }

    fun showAnswer() {
        val state = _uiState.value
        if (state.isComplete) return

        // Collect all moves in the solution path
        val solutionMoves = mutableListOf<Move>()
        var node = solutionNode
        while (node != null) {
            node.move?.let { solutionMoves.add(it) }
            node = node.children.firstOrNull { it.isCorrect }
        }

        _uiState.update {
            it.copy(showAnswer = true, answerMoves = solutionMoves)
        }
    }

    fun hideAnswer() {
        _uiState.update {
            it.copy(showAnswer = false, answerMoves = emptyList())
        }
    }

    fun reset() {
        val puzzle = _uiState.value.puzzle ?: return

        val board = Board.fromStones(puzzle.boardSize, puzzle.initialStones)
        gameEngine = GameEngine(board)
        currentNode = solutionNode

        _uiState.update {
            it.copy(
                currentBoard = board,
                stones = puzzle.initialStones,
                moveHistory = emptyList(),
                currentPlayer = puzzle.nextPlayerColor,
                isComplete = false,
                isCorrect = false,
                showHint = false,
                hintMove = null,
                showAnswer = false,
                answerMoves = emptyList(),
                wrongMove = null,
                lastMove = null,
                feedbackMessage = null
            )
        }
    }

    fun undo() {
        val state = _uiState.value
        if (state.moveHistory.isEmpty()) return

        // Reset and replay all moves except the last one
        val puzzle = state.puzzle ?: return
        val board = Board.fromStones(puzzle.boardSize, puzzle.initialStones)
        gameEngine = GameEngine(board)
        currentNode = solutionNode

        val movesToReplay = state.moveHistory.dropLast(1)
        var stones = puzzle.initialStones

        for (move in movesToReplay) {
            gameEngine!!.playMove(move)
            stones = board.getAllStones()

            // Advance to correct child
            currentNode = currentNode?.children?.find { it.move == move } ?: currentNode
        }

        _uiState.update {
            it.copy(
                currentBoard = board,
                stones = stones,
                moveHistory = movesToReplay,
                currentPlayer = if (movesToReplay.size % 2 == 0) puzzle.nextPlayerColor else puzzle.nextPlayerColor.opposite(),
                isComplete = false,
                isCorrect = false,
                showHint = false,
                hintMove = null,
                wrongMove = null,
                lastMove = movesToReplay.lastOrNull()?.point,
                feedbackMessage = null
            )
        }
    }

    fun toggleFavorite() {
        val puzzle = _uiState.value.puzzle ?: return
        val newFavorite = repository.toggleFavorite(puzzle.id)
        _uiState.update { it.copy(isFavorite = newFavorite) }
    }

    fun clearFeedback() {
        _uiState.update { it.copy(feedbackMessage = null) }
    }
}
