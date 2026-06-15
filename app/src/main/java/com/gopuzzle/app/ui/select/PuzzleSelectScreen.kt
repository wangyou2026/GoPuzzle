package com.gopuzzle.app.ui.select

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gopuzzle.app.data.repository.PuzzleRepository
import com.gopuzzle.app.domain.model.Difficulty
import com.gopuzzle.app.domain.model.Puzzle
import com.gopuzzle.app.domain.model.PuzzleCategory
import com.gopuzzle.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PuzzleSelectScreen(
    repository: PuzzleRepository,
    category: String,
    onPuzzleSelected: (String) -> Unit,
    onBack: () -> Unit
) {

    val puzzles = remember(category) {
        when (category) {
            "WRONG" -> repository.getWrongPuzzles()
            "FAVORITES" -> repository.getFavoritePuzzles()
            else -> {
                try {
                    val cat = PuzzleCategory.valueOf(category)
                    repository.getPuzzlesByCategory(cat)
                } catch (e: Exception) {
                    repository.getAllPuzzles()
                }
            }
        }
    }

    val title = when (category) {
        "WRONG" -> "错题本"
        "FAVORITES" -> "收藏夹"
        else -> {
            try {
                PuzzleCategory.valueOf(category).displayName
            } catch (e: Exception) {
                "全部题目"
            }
        }
    }

    // Group puzzles by difficulty
    val groupedPuzzles = puzzles.groupBy { it.difficulty }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
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
        if (puzzles.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Inbox,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "暂无题目",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "开始练习后，这里会显示您的进度",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                Difficulty.entries.forEach { difficulty ->
                    val difficultyPuzzles = groupedPuzzles[difficulty]
                    if (!difficultyPuzzles.isNullOrEmpty()) {
                        item {
                            DifficultyHeader(difficulty = difficulty, count = difficultyPuzzles.size)
                        }
                        items(difficultyPuzzles) { puzzle ->
                            PuzzleCard(
                                repository = repository,
                                puzzle = puzzle,
                                onClick = { onPuzzleSelected(puzzle.id) }
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DifficultyHeader(
    difficulty: Difficulty,
    count: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = difficulty.displayName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = MaterialTheme.shapes.small
        ) {
            Text(
                text = "$count 题",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun PuzzleCard(
    repository: PuzzleRepository,
    puzzle: Puzzle,
    onClick: () -> Unit
) {
    val progress = remember { repository.getProgress(puzzle.id) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Board size indicator
            Surface(
                color = WoodBrownLight.copy(alpha = 0.2f),
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = "${puzzle.boardSize}路",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    color = WoodBrown
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = puzzle.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                if (puzzle.description.isNotEmpty()) {
                    Text(
                        text = puzzle.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }

            // Progress indicator
            when {
                progress?.isSolved == true -> {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "已解决",
                        tint = CorrectGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }
                progress?.isWrong == true -> {
                    Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = "做错",
                        tint = IncorrectRed,
                        modifier = Modifier.size(24.dp)
                    )
                }
                progress?.isFavorite == true -> {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "收藏",
                        tint = IncorrectRed,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "进入",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
