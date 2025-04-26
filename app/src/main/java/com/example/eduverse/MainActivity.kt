package com.example.eduverse

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.eduverse.ui.components.EduVerseBottomBar
import com.example.eduverse.ui.screens.*
import com.example.eduverse.ui.screens.classroom.ClassroomScreen
import com.example.eduverse.ui.screens.classroom.FormulaTheoryScreen
import com.example.eduverse.ui.screens.problems.*
import com.example.eduverse.ui.screens.scores.ScoreHistoryScreen
import com.example.eduverse.ui.theme.EduVerseTheme
import com.example.eduverse.util.NetworkObserver
import com.example.eduverse.util.ConnectionStatus
import androidx.navigation.navDeepLink
import com.example.eduverse.ui.screens.settings.SettingsScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.navigation.compose.rememberNavController


/**
 * The main activity of the EduVerse application.
 *
 * This activity serves as the entry point for the application and handles
 * the initialization of the UI, shared preferences, and the problem interaction
 * view model. It also manages changes to application-wide settings like font
 * scale and dark mode.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val isFirstCreation = savedInstanceState == null

        setContent {
            val prefs: SharedPreferences = getSharedPreferences("eduverse_prefs", Context.MODE_PRIVATE)
            val interactionViewModel: ProblemInteractionViewModel = viewModel()

            val fontScaleState = remember {
                mutableFloatStateOf(prefs.getFloat("pref_font_scale", 1f))
            }
            val darkModeState = remember {
                mutableStateOf(prefs.getBoolean("pref_dark_mode", false))
            }

            DisposableEffect(prefs) {
                val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
                    when (key) {
                        "pref_font_scale" -> fontScaleState.floatValue = prefs.getFloat(key, 1f)
                        "pref_dark_mode" -> darkModeState.value = prefs.getBoolean(key, false)
                    }
                }
                prefs.registerOnSharedPreferenceChangeListener(listener)
                onDispose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
            }

            EduVerseTheme(
                darkTheme = darkModeState.value,
                fontScale = fontScaleState.floatValue
            ) {
                EduVerseApp(
                    interactionViewModel = interactionViewModel,
                    isFirstCreation = isFirstCreation
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
}

/**
 * The main composable function for the EduVerse application.
 *
 * This function sets up the navigation, UI structure, and network connectivity monitoring
 * for the EduVerse app. It utilizes a Scaffold for the basic layout, including a top bar
 * to display network status and a bottom bar for navigation.
 *
 * @param interactionViewModel The ViewModel responsible for handling problem interaction data.
 * @param isFirstCreation A boolean indicating whether this is the first time the composable is being created.
 *                        Used to handle deep links on initial launch.
 *
 * Flow:
 * - Initialize Navigation:
 *    - `rememberNavController()`: Creates and remembers a NavHostController for managing app navigation.
 *    - `currentBackStackEntryAsState()`: Provides the current back stack entry for tracking the current route.
 * - Check Network Status:
 *    - `NetworkObserver`: Observes the network connection status.
 */
@Composable
fun EduVerseApp(
    interactionViewModel: ProblemInteractionViewModel,
    isFirstCreation: Boolean
) {
    val activity = LocalActivity.current
    val navController = rememberNavController()

    val connectionStatus by NetworkObserver(LocalContext.current)
        .status
        .collectAsState(initial = ConnectionStatus.Available)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            if (connectionStatus == ConnectionStatus.Unavailable) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFB00020))
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No Internet Connection", color = Color.White)
                }
            }
        },
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

    LaunchedEffect(isFirstCreation) {
        if (isFirstCreation) {
            if (activity != null) {
                activity.intent?.let { navController.handleDeepLink(it) }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            if (activity != null) {
                activity.intent = Intent()
            }
        }
    }
}


/**
 * [EduVerseNavHost] is the main navigation host for the EduVerse application.
 *
 * This composable defines the navigation graph for the application, specifying the available
 * screens and how to navigate between them. It uses a [NavHostController] to manage the
 * navigation state and provides access to different screens via composable functions.
 *
 * @param navController The [NavHostController] responsible for managing the navigation
 *                      within the application.
 * @param interactionViewModel The [ProblemInteractionViewModel] used for managing problem
 *                             interaction state, shared across some screens.
 * @param modifier An optional [Modifier] to apply to the navigation host.
 *
 * Navigation Routes:
 *  - "home": The home screen of the application.
 *  - "equation_solver": The equation solver screen.
 *  - "nav_classroom": The classroom screen.
 *  - "classroom?formulaTitle={formulaTitle}&formulaTheory={formulaTheory}": The formula theory screen,
 *    accepting the formula title and theory as arguments.
 *    - `formulaTitle`: The title of the formula (defaults to "Formula").
 *    - `formulaTheory`: The theory behind the formula (defaults to "No theory available").
 */
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
        composable("home") {
            HomeScreen(navController)
        }
        composable("equation_solver") {
            EquationSolverScreen(navController)
        }
        composable("nav_classroom") {
            ClassroomScreen(navController)
        }
        composable("classroom?formulaTitle={formulaTitle}&formulaTheory={formulaTheory}") { backStackEntry ->
            val title = backStackEntry.arguments?.getString("formulaTitle") ?: "Formula"
            val theory = backStackEntry.arguments?.getString("formulaTheory") ?: "No theory available"
            FormulaTheoryScreen(title = title, theory = theory)
        }
        composable("problems") {
            ProblemsHubScreen(navController)
        }
        composable("practice") {
            PracticeScreen(
                navController = navController,
                practiceViewModel = viewModel(),
                interactionViewModel = interactionViewModel
            )
        }
        composable("sandbox") {
            SandboxNotepadScreen(navController = navController, interactionViewModel = interactionViewModel)
        }
        composable("test_mode") {
            TestModeScreen(navController)
        }
        composable("score_history") {
            ScoreHistoryScreen(navController)
        }
        composable(
            route = "problem_of_the_day",
            deepLinks = listOf(
                navDeepLink { uriPattern = "eduverse://problem_of_the_day" }
            )
        ) {
            ProblemOfDayScreen(navController = navController, interactionViewModel = interactionViewModel)
        }
        composable("settings") {
            SettingsScreen()
        }
    }
}