package com.example.eduverse.ui.screens.scores

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.eduverse.R


/**
 * Displays the score history of the user.
 *
 * This screen fetches and displays a list of past scores, including the timestamp,
 * number of correct answers, and total number of questions. If no scores are
 * found, it displays a message indicating that. It also provides a button
 * to navigate back to the "problems" screen.
 *
 * @param navController The NavController instance used for navigating between screens.
 * @param context The Context used for accessing resources and persistent data.
 *                Defaults to the current composition's LocalContext.
 *
 * @see ScoreRepository
 * @see NavController
 */
@Composable
fun ScoreHistoryScreen(
    navController: NavController,
    context: Context = LocalContext.current
) {
    val scores = remember { mutableStateOf(ScoreRepository.loadScores(context)) }

    Column(Modifier.padding(16.dp)) {
        Text(stringResource(R.string.score_history_heading_text), style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(12.dp))
        if (scores.value.isEmpty()) {
            Text(stringResource(R.string.no_scores_text))
        } else {
            scores.value.forEach { s ->
                Text("${s.timestamp}: ${s.correct}/${s.total}",
                    style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(4.dp))
            }
        }
        Spacer(Modifier.height(16.dp))
        Button(onClick = { navController.navigate("problems") }) {
            Text(stringResource(R.string.back_to_problems_text))
        }
    }
}