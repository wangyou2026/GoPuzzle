package com.gopuzzle.app.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gopuzzle.app.GoPuzzleApplication
import com.gopuzzle.app.ui.home.HomeScreen
import com.gopuzzle.app.ui.puzzle.PuzzleScreen
import com.gopuzzle.app.ui.puzzle.PuzzleViewModel
import com.gopuzzle.app.ui.select.PuzzleSelectScreen
import android.content.Context
import androidx.compose.ui.platform.LocalContext

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
    val context = LocalContext.current.applicationContext as GoPuzzleApplication

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                repository = context.puzzleRepository,
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
                repository = context.puzzleRepository,
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
                viewModel = PuzzleViewModel(context.puzzleRepository),
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
