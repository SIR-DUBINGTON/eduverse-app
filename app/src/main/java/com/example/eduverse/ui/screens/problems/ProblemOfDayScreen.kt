// ui/screens/problems/ProblemOfDayScreen.kt
package com.example.eduverse.ui.screens.problems

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import java.time.LocalDate

@Composable
fun ProblemOfDayScreen(
    navController: NavController,
    practiceViewModel: PracticeViewModel = viewModel(),
    interactionViewModel: ProblemInteractionViewModel = viewModel()
) {
    val problems by remember { derivedStateOf { practiceViewModel.problems } }
    val loading by remember { derivedStateOf { practiceViewModel.loading } }
    val error by remember { derivedStateOf { practiceViewModel.error } }

    val today = LocalDate.now()
    val formatted = today.format(java.time.format.DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Today is $formatted",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Problem of the Day",
            style = MaterialTheme.typography.headlineMedium
        )


    when {
            loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            error != null -> Text("Error: $error", color = MaterialTheme.colorScheme.error)
            problems.isEmpty() -> Text("No problems available.")
            else -> {
                // pick an index based on today's epoch day
                val todayEpoch = LocalDate.now().toEpochDay()
                val index = (todayEpoch % problems.size).toInt().coerceIn(0, problems.lastIndex)
                val problem = problems[index]

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text(problem.title, style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        Text(problem.description)
                        Spacer(Modifier.height(8.dp))
                        Text("Difficulty: ${problem.difficulty}", style = MaterialTheme.typography.labelMedium)
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = {
                            interactionViewModel.setProblem(problem)
                            navController.navigate("sandbox")
                        }) {
                            Text("Solve Today’s Problem")
                        }
                    }
                }
            }
        }
    }
}
