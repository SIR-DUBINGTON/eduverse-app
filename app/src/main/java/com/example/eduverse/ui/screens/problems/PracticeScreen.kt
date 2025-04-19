package com.example.eduverse.ui.screens.problems

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun PracticeScreen(
    navController: NavController,
    viewModel: PracticeViewModel = viewModel(),
    interactionViewModel: ProblemInteractionViewModel = viewModel()
) {
    val problems = viewModel.problems
    val loading = viewModel.loading
    val error = viewModel.error

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Practice Problems", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        when {
            loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            error != null -> {
                Text("Error: $error", color = MaterialTheme.colorScheme.error)
            }
            else -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(problems) { problem ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(problem.title, style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(problem.description)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Difficulty: ${problem.difficulty}",
                                    style = MaterialTheme.typography.labelMedium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(onClick = {
                                    interactionViewModel.setProblem(problem)
                                    navController.navigate("sandbox")
                                }) {
                                    Text("Solve Problem")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
