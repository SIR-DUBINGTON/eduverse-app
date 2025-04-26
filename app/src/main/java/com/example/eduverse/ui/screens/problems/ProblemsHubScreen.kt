package com.example.eduverse.ui.screens.problems

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.eduverse.R

/**
 * The ProblemsHubScreen composable displays the main screen for accessing different problem-solving
 * modes and related features.
 *
 * This screen provides navigation to the following sections:
 * - Practice Mode: Allows users to practice solving problems in a flexible environment.
 * - Problem of the Day: Presents a daily challenge problem.
 * - Test Mode: Simulates a timed test environment for problem-solving.
 * - Score History: Displays the user's past performance and scores.
 *
 * @param navController The NavController instance used for navigating between screens.
 */
@Composable
fun ProblemsHubScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            stringResource(R.string.problems_heading_text),
            style = MaterialTheme.typography.headlineMedium
        )

        Button(
            onClick = { navController.navigate("practice") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.practice_mode_text))
        }

        Button(
            onClick = { navController.navigate("problem_of_the_day") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.problem_of_the_day_text))
        }

        Button(
            onClick = { navController.navigate("test_mode") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.test_mode_text))
        }

        Button(onClick = { navController.navigate("score_history") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.score_history_text))
        }
    }
}