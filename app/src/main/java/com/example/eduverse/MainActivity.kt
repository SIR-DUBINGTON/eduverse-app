package com.example.eduverse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.eduverse.ui.screens.EquationSolverScreen
import com.example.eduverse.ui.screens.HomeScreen
import com.example.eduverse.ui.screens.classroom.ClassroomScreen
import com.example.eduverse.ui.theme.EduVerseTheme
import com.example.eduverse.ui.components.EduVerseBottomBar
import com.example.eduverse.ui.screens.classroom.FormulaTheoryScreen
import com.example.eduverse.ui.screens.problems.PracticeScreen
import com.example.eduverse.ui.screens.problems.ProblemInteractionViewModel
import com.example.eduverse.ui.screens.problems.ProblemOfDayScreen
import com.example.eduverse.ui.screens.problems.ProblemsHubScreen
import com.example.eduverse.ui.screens.problems.SandboxNotepadScreen
import com.example.eduverse.ui.screens.problems.TestModeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EduVerseTheme {
                EduVerseApp()
            }
        }
    }
}

@Composable
fun EduVerseApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val interactionViewModel: ProblemInteractionViewModel = viewModel()

    Scaffold(
        bottomBar = {
            EduVerseBottomBar(currentRoute = currentRoute) { route ->
                navController.navigate(route) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            }
        }
    ) { innerPadding ->
        EduVerseNavHost(
            navController = navController,
            interactionViewModel = interactionViewModel,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun EduVerseNavHost(
    navController: NavHostController,
    interactionViewModel: ProblemInteractionViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier
    ) {
        composable("home") { HomeScreen(navController) }
        composable("equation_solver") { EquationSolverScreen(navController) }
        composable("nav_classroom") { ClassroomScreen(navController) }
        composable("classroom?formulaTitle={formulaTitle}&formulaTheory={formulaTheory}") { backStackEntry ->
            val formulaTitle = backStackEntry.arguments?.getString("formulaTitle") ?: "Formula"
            val formulaTheory = backStackEntry.arguments?.getString("formulaTheory") ?: "No theory available"
            FormulaTheoryScreen(title = formulaTitle, theory = formulaTheory)
        }
        composable("problems") { ProblemsHubScreen(navController) }
        composable("practice") { PracticeScreen(navController, interactionViewModel = interactionViewModel) }
        composable("sandbox") { SandboxNotepadScreen(interactionViewModel = interactionViewModel) }
        composable("problem_of_the_day") { ProblemOfDayScreen(navController, interactionViewModel = interactionViewModel) }
        composable("test_mode") { TestModeScreen(navController, interactionViewModel = interactionViewModel) }
    }
}
