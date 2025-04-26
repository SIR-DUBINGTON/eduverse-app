package com.example.eduverse.ui.screens.problems

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.widget.RemoteViews
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.eduverse.R
import com.example.eduverse.ui.widgets.ProblemOfDayWidgetProvider
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.core.content.edit

/**
 * Composable function that displays the "Problem of the Day" screen.
 *
 * This screen fetches and displays a daily problem, along with its title, description,
 * difficulty, and a button to navigate to the sandbox for solving. It also updates
 * an app widget with the current problem's title.
 *
 * @param navController The navigation controller used to navigate to the sandbox screen.
 * @param practiceViewModel The view model responsible for fetching and managing the list of practice problems.
 * @param interactionViewModel The view model responsible for managing the interaction with a selected problem.
 *
 * The function handles the following states:
 * - **Loading:** Displays a circular progress indicator while fetching problems.
 * - **Error:** Displays an error message if there's an issue fetching problems.
 */
@Composable
fun ProblemOfDayScreen(
    navController: NavController,
    practiceViewModel: PracticeViewModel = viewModel(),
    interactionViewModel: ProblemInteractionViewModel = viewModel()
) {
    val context = LocalContext.current
    val problems by remember { derivedStateOf { practiceViewModel.problems } }
    val loading by remember { derivedStateOf { practiceViewModel.loading } }
    val error by remember { derivedStateOf { practiceViewModel.errorDetail } }

    val today = LocalDate.now()
    val formatted = today.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            stringResource(R.string.today_is_text) + " $formatted",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            stringResource(R.string.problem_of_the_day_heading_text),
            style = MaterialTheme.typography.headlineMedium
        )

        when {
            loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            error != null -> Text(stringResource(R.string.PoD_error_text)+" $error", color = MaterialTheme.colorScheme.error)
            problems.isEmpty() -> Text(stringResource(R.string.no_problems_text))
            else -> {
                val todayEpoch = LocalDate.now().toEpochDay()
                val index = (todayEpoch % problems.size).toInt().coerceIn(0, problems.lastIndex)
                val problem = problems[index]

                LaunchedEffect(problem.id) {
                    val prefs = context.getSharedPreferences("pod_prefs", Context.MODE_PRIVATE)
                    prefs.edit {
                        putString("pod_title", problem.title)
                    }

                    val mgr = AppWidgetManager.getInstance(context)
                    val comp = ComponentName(context, ProblemOfDayWidgetProvider::class.java)
                    val views = RemoteViews(context.packageName, R.layout.widget_problem_of_day).apply {
                        setTextViewText(R.id.widget_problem_title, problem.title)

                    }
                    mgr.updateAppWidget(mgr.getAppWidgetIds(comp), views)
                }

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text(problem.title, style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        Text(problem.description)
                        Spacer(Modifier.height(8.dp))
                        Text(stringResource(R.string.difficulty_text)+" ${problem.difficulty}", style = MaterialTheme.typography.labelMedium)
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = {
                            interactionViewModel.setProblem(problem)
                            navController.navigate("sandbox")
                        }) {
                            Text(stringResource(R.string.solve_today_problem_text))
                        }
                    }
                }
            }
        }
    }
}