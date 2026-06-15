package com.gopuzzle.app.ui.puzzle

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gopuzzle.app.domain.model.Point
import com.gopuzzle.app.domain.model.Stone
import com.gopuzzle.app.ui.components.GoBoard
import com.gopuzzle.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PuzzleScreen(
    viewModel: PuzzleViewModel,
    puzzleId: String,
    onBack: () -> Unit,
    onNextPuzzle: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(puzzleId) {
        viewModel.loadPuzzle(puzzleId)
    }

    // Show feedback snackbar
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.feedbackMessage) {
        uiState.feedbackMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearFeedback()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = uiState.puzzle?.title ?: "加载中...",
                            style = MaterialTheme.typography.titleMedium
                        )
                        uiState.puzzle?.let { puzzle ->
                            Text(
                                text = "${puzzle.boardSize}路 · ${puzzle.difficulty.displayName} · ${puzzle.category.displayName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleFavorite() }) {
                        Icon(
                            imageVector = if (uiState.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "收藏",
                            tint = if (uiState.isFavorite) IncorrectRed else MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = uiState.error ?: "未知错误",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            else -> {
                PuzzleContent(
                    uiState = uiState,
                    onStoneClick = viewModel::onStoneClick,
                    onShowHint = viewModel::showHint,
                    onHideHint = viewModel::hideHint,
                    onShowAnswer = viewModel::showAnswer,
                    onHideAnswer = viewModel::hideAnswer,
                    onReset = viewModel::reset,
                    onUndo = viewModel::undo,
                    onNextPuzzle = {
                        uiState.puzzle?.let { puzzle ->
                            val next = com.gopuzzle.app.data.repository.PuzzleRepository()
                                .getNextPuzzle(puzzle.id, puzzle.category, null)
                            next?.let { onNextPuzzle(it.id) }
                        }
                    },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun PuzzleContent(
    uiState: PuzzleUiState,
    onStoneClick: (Point) -> Unit,
    onShowHint: () -> Unit,
    onHideHint: () -> Unit,
    onShowAnswer: () -> Unit,
    onHideAnswer: () -> Unit,
    onReset: () -> Unit,
    onUndo: () -> Unit,
    onNextPuzzle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Current player indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!uiState.isComplete) {
                Surface(
                    color = if (uiState.currentPlayer == Stone.BLACK)
                        BlackStone else WhiteStone,
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.size(20.dp)
                ) {}
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (uiState.currentPlayer == Stone.BLACK) "黑方落子" else "白方落子",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            } else {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + scaleIn()
                ) {
                    Surface(
                        color = if (uiState.isCorrect) CorrectGreen else IncorrectRed,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (uiState.isCorrect) "正确！" else "错误",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = WhiteStone,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Description
        uiState.puzzle?.description?.let { desc ->
            if (desc.isNotEmpty()) {
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Hint indicator
        AnimatedVisibility(
            visible = uiState.showHint && uiState.hintMove != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Surface(
                color = HintBlue.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = HintBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "提示：参考下一步位置",
                        style = MaterialTheme.typography.bodyMedium,
                        color = HintBlue
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Game Board
        GoBoard(
            boardSize = uiState.puzzle?.boardSize ?: 9,
            stones = uiState.stones,
            currentPlayer = uiState.currentPlayer,
            showCoordinates = true,
            highlightedPoints = when {
                uiState.showHint && uiState.hintMove != null -> setOf(uiState.hintMove!!.point)
                uiState.showAnswer -> uiState.answerMoves.map { it.point }.toSet()
                else -> emptySet()
            },
            wrongPoint = uiState.wrongMove?.point,
            correctPoint = if (uiState.isComplete && uiState.isCorrect) {
                uiState.lastMove
            } else null,
            lastMovePoint = uiState.lastMove,
            onStoneClick = onStoneClick,
            enabled = !uiState.isComplete
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Action buttons
        ActionButtons(
            uiState = uiState,
            onShowHint = onShowHint,
            onHideHint = onHideHint,
            onShowAnswer = onShowAnswer,
            onHideAnswer = onHideAnswer,
            onReset = onReset,
            onUndo = onUndo,
            onNextPuzzle = onNextPuzzle
        )

        Spacer(modifier = Modifier.weight(1f))

        // Progress indicator
        if (uiState.moveHistory.isNotEmpty()) {
            Text(
                text = "已走 ${uiState.moveHistory.size} 手",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ActionButtons(
    uiState: PuzzleUiState,
    onShowHint: () -> Unit,
    onHideHint: () -> Unit,
    onShowAnswer: () -> Unit,
    onHideAnswer: () -> Unit,
    onReset: () -> Unit,
    onUndo: () -> Unit,
    onNextPuzzle: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (uiState.isComplete) {
            // Puzzle complete actions
            Button(
                onClick = onNextPuzzle,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CorrectGreen
                )
            ) {
                Icon(Icons.Default.SkipNext, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("下一题")
            }

            OutlinedButton(
                onClick = onReset,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("重做")
            }
        } else {
            // In progress actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Hint button
                OutlinedButton(
                    onClick = if (uiState.showHint) onHideHint else onShowHint,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = if (uiState.showHint) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (uiState.showHint) "隐藏" else "提示")
                }

                // Answer button
                OutlinedButton(
                    onClick = if (uiState.showAnswer) onHideAnswer else onShowAnswer,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = if (uiState.showAnswer) Icons.Default.VisibilityOff else Icons.Default.Lightbulb,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (uiState.showAnswer) "隐藏" else "答案")
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Undo button
                OutlinedButton(
                    onClick = onUndo,
                    enabled = uiState.moveHistory.isNotEmpty(),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Undo, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("悔棋")
                }

                // Reset button
                OutlinedButton(
                    onClick = onReset,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("重置")
                }
            }
        }
    }
}
