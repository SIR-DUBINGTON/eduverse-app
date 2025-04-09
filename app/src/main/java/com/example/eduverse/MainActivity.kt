package com.example.eduverse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EduVerseApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            EduVerseBottomBar(currentRoute = currentRoute) { route ->
                navController.navigate(route) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            }
        }
    ) { innerPadding ->
        EduVerseNavHost(navController = navController, modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun EduVerseNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier
    ) {
        composable("home") { HomeScreen(navController) }
        composable("equation_solver") { EquationSolverScreen(navController) }
        composable("classroom?formulaTitle={formulaTitle}&formulaTheory={formulaTheory}") {
            ClassroomScreen(it)
        }
    }
}
