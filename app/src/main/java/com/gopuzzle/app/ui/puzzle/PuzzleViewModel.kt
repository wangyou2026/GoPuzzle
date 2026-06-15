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

class PuzzleViewModel(private val repository: PuzzleRepository = PuzzleRepository()) : ViewModel() {

    private val _uiState = MutableStateFlow(PuzzleUiState())
    val uiState: StateFlow<PuzzleUiState> = _uiState.asStateFlow()

    private var gameEngine: GameEngine? = null
    private var solutionPath: List<Move> = emptyList()
    private var currentMoveIndex: Int = 0

    fun loadPuzzle(puzzleId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val puzzle = repository.getPuzzleById(puzzleId)
                if (puzzle == null) {
                    _uiState.update { it.copy(isLoading = false, error = "题目不存在") }
                    return@launch
                }

                val board = Board.fromStones(puzzle.boardSize, puzzle.initialStones)
                gameEngine = GameEngine(board)

                solutionPath = extractSolutionPath(puzzle.solutionTree)
                currentMoveIndex = 0

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

    private fun extractSolutionPath(node: MoveNode?): List<Move> {
        val path = mutableListOf<Move>()
        var current = node
        while (current != null && current.move != null) {
            path.add(current.move!!)
            current = current.children.firstOrNull()
        }
        return path
    }

    fun onStoneClick(point: Point) {
        val state = _uiState.value
        if (state.isComplete || state.puzzle == null || gameEngine == null) return

        val move = Move(point, state.currentPlayer)

        val result = gameEngine!!.playMove(move)

        when (result) {
            is MoveResult.Success -> {
                val newStones = state.currentBoard!!.getAllStones()

                if (isCorrectMove(move)) {
                    currentMoveIndex++

                    if (currentMoveIndex >= solutionPath.size) {
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
                _uiState.update {
                    it.copy(feedbackMessage = result.reason)
                }
            }
        }
    }

    private fun isCorrectMove(move: Move): Boolean {
        if (currentMoveIndex >= solutionPath.size) return false
        
        val expectedMove = solutionPath[currentMoveIndex]
        return move.point == expectedMove.point && move.color == expectedMove.color
    }

    fun showHint() {
        val state = _uiState.value
        if (state.isComplete) return

        if (currentMoveIndex < solutionPath.size) {
            _uiState.update {
                it.copy(
                    showHint = true,
                    hintMove = solutionPath[currentMoveIndex]
                )
            }
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

        _uiState.update {
            it.copy(showAnswer = true, answerMoves = solutionPath)
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
        currentMoveIndex = 0

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

        val puzzle = state.puzzle ?: return
        val board = Board.fromStones(puzzle.boardSize, puzzle.initialStones)
        gameEngine = GameEngine(board)

        val movesToReplay = state.moveHistory.dropLast(1)
        var stones = puzzle.initialStones

        for (move in movesToReplay) {
            gameEngine!!.playMove(move)
            stones = board.getAllStones()
        }

        currentMoveIndex = movesToReplay.size

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
