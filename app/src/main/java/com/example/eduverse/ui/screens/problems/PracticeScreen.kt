package com.example.eduverse.ui.screens.problems

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.eduverse.R

/**
 * Displays the Practice screen, which allows the user to browse and select problems to solve.
 *
 * This screen fetches a list of problems from the [PracticeViewModel] and displays them in a
 * scrollable list. Each problem card shows the problem's title and description, along with a
 * button to navigate to the sandbox screen for solving the problem.
 *
 * @param navController The navigation controller for navigating to other screens.
 * @param practiceViewModel The view model providing the list of problems and loading state.
 * @param interactionViewModel The view model for managing the selected problem and interactions.
 */
@Composable
fun PracticeScreen(
    navController: NavController,
    practiceViewModel: PracticeViewModel,
    interactionViewModel: ProblemInteractionViewModel
) {
    LaunchedEffect(Unit) {
        interactionViewModel.clearProblem()
    }

    val loading       = practiceViewModel.loading
    val problems      = practiceViewModel.problems
    val errorDetail   = practiceViewModel.errorDetail

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = stringResource(R.string.practice_heading_text),
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(Modifier.height(16.dp))

        when {
            loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            errorDetail != null -> {
                Text(
                    text = stringResource(R.string.failed_to_load_problems_text, errorDetail),
                    color = MaterialTheme.colorScheme.error
                )
            }
            else -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(problems) { problem ->
                        Card(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp)) {
                                Text(problem.title, style = MaterialTheme.typography.titleMedium)
                                Spacer(Modifier.height(4.dp))
                                Text(problem.description)
                                Spacer(Modifier.height(8.dp))
                                Button(onClick = {
                                    interactionViewModel.setProblem(problem)
                                    navController.navigate("sandbox")
                                }) {
                                    Text(stringResource(R.string.solve_problem_text))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}