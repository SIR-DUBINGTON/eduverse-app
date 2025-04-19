package com.example.eduverse.ui.screens.problems

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ProblemsHubScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Problems",
            style = MaterialTheme.typography.headlineMedium
        )

        Button(
            onClick = { navController.navigate("practice") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Practice Problems")
        }

        Button(
            onClick = { navController.navigate("problem_of_the_day") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Problem of the Day")
        }

        Button(
            onClick = { navController.navigate("test_mode") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Test Mode")
        }
    }
}
