package com.gopuzzle.app.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gopuzzle.app.ui.home.HomeScreen
import com.gopuzzle.app.ui.puzzle.PuzzleScreen
import com.gopuzzle.app.ui.select.PuzzleSelectScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Select : Screen("select/{category}") {
        fun createRoute(category: String) = "select/$category"
    }
    object Puzzle : Screen("puzzle/{puzzleId}") {
        fun createRoute(puzzleId: String) = "puzzle/$puzzleId"
    }
}

@Composable
fun GoPuzzleNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToSelect = { category ->
                    navController.navigate(Screen.Select.createRoute(category))
                },
                onNavigateToPuzzle = { puzzleId ->
                    navController.navigate(Screen.Puzzle.createRoute(puzzleId))
                }
            )
        }

        composable(
            route = Screen.Select.route,
            arguments = listOf(
                navArgument("category") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: ""
            PuzzleSelectScreen(
                category = category,
                onPuzzleSelected = { puzzleId ->
                    navController.navigate(Screen.Puzzle.createRoute(puzzleId))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Puzzle.route,
            arguments = listOf(
                navArgument("puzzleId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val puzzleId = backStackEntry.arguments?.getString("puzzleId") ?: ""
            PuzzleScreen(
                puzzleId = puzzleId,
                onBack = { navController.popBackStack() },
                onNextPuzzle = { nextPuzzleId ->
                    navController.navigate(Screen.Puzzle.createRoute(nextPuzzleId)) {
                        popUpTo(Screen.Puzzle.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
