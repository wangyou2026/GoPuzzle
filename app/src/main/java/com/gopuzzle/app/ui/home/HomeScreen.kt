package com.gopuzzle.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gopuzzle.app.data.repository.PuzzleRepository
import com.gopuzzle.app.domain.model.PuzzleCategory
import com.gopuzzle.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    repository: PuzzleRepository,
    onNavigateToSelect: (String) -> Unit,
    onNavigateToPuzzle: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "围棋死活训练",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "题目分类",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(PuzzleCategory.entries) { category ->
                CategoryCard(
                    category = category,
                    onClick = { onNavigateToSelect(category.name) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "快速开始",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            item {
                QuickStartCard(
                    title = "继续上次练习",
                    subtitle = "从错题本开始",
                    icon = Icons.Default.Refresh,
                    onClick = { onNavigateToSelect("WRONG") }
                )
            }

            item {
                QuickStartCard(
                    title = "收藏题目",
                    subtitle = "复习您收藏的题目",
                    icon = Icons.Default.Favorite,
                    onClick = { onNavigateToSelect("FAVORITES") }
                )
            }
        }
    }
}

@Composable
private fun CategoryCard(
    category: PuzzleCategory,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        when (category) {
                            PuzzleCategory.LIFE_AND_DEATH -> BambooGreenLight.copy(alpha = 0.2f)
                            PuzzleCategory.TESUJI -> WoodBrownLight.copy(alpha = 0.2f)
                            PuzzleCategory.YOSE -> HintBlue.copy(alpha = 0.2f)
                            else -> MaterialTheme.colorScheme.primaryContainer
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (category) {
                        PuzzleCategory.LIFE_AND_DEATH -> Icons.Default.Security
                        PuzzleCategory.TESUJI -> Icons.Default.Bolt
                        PuzzleCategory.YOSE -> Icons.Default.Layers
                        else -> Icons.Default.GridOn
                    },
                    contentDescription = null,
                    tint = when (category) {
                        PuzzleCategory.LIFE_AND_DEATH -> BambooGreen
                        PuzzleCategory.TESUJI -> WoodBrown
                        PuzzleCategory.YOSE -> HintBlue
                        else -> MaterialTheme.colorScheme.primary
                    },
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = getCategoryDescription(category),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "进入",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun QuickStartCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }

            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "开始",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

private fun getCategoryDescription(category: PuzzleCategory): String {
    return when (category) {
        PuzzleCategory.LIFE_AND_DEATH -> "练习死活棋型，提高计算力"
        PuzzleCategory.TESUJI -> "学习妙手技巧，掌握行棋要领"
        PuzzleCategory.YOSE -> "练习官子计算，积累目数"
        PuzzleCategory.OPENING -> "布局定式学习"
        PuzzleCategory.ENDGAME -> "收官技巧练习"
    }
}
