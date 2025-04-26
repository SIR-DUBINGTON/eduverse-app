package com.example.eduverse.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.eduverse.R
import java.time.LocalTime

/**
 * Represents a feature in the application, typically displayed as an item in a navigation menu or a list of available functionalities.
 *
 * @property title The user-friendly name of the feature, to be displayed in the UI.
 * @property icon The visual representation of the feature, displayed alongside the title. This is expected to be a vector image.
 * @property route The navigation route associated with this feature. When the feature is selected, the application should navigate to this route.
 */
private data class Feature(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val route: String
)

/**
 * The main screen of the application, displaying a personalized greeting,
 * a "Problem of the Day" card, and a grid of feature shortcuts.
 *
 * @param navController The navigation controller used to navigate to other screens.
 *
 * This Composable function:
 * - Determines the appropriate greeting based on the current time of day.
 */

@Composable
fun HomeScreen(navController: NavController) {
    val hour = LocalTime.now().hour
    val greeting = when (hour) {
        in 5..11  -> stringResource(R.string.good_morning)
        in 12..17 -> stringResource(R.string.good_afternoon)
        in 18..21 -> stringResource(R.string.good_evening)
        in 22..23 -> stringResource(R.string.good_night)
        in 0..4   -> stringResource(R.string.good_twilight_hours)
        else      -> stringResource(R.string.hello)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = greeting,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(Modifier.height(8.dp))
        Text(stringResource(R.string.welcome_text_subheading))
        Spacer(Modifier.height(16.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navController.navigate("problem_of_the_day") }
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.problem_of_the_day_text),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.tap_to_solve_today),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        Spacer(Modifier.height(16.dp))

        val features = listOf(
            Feature(stringResource(R.string.equation_solver_text), Icons.Default.Calculate,      "equation_solver"),
            Feature(stringResource(R.string.nav_classroom_text),   Icons.AutoMirrored.Filled.MenuBook, "nav_classroom"),
            Feature(stringResource(R.string.problems_text),        Icons.Default.Lightbulb,      "problems"),
            Feature(stringResource(R.string.practice_text),        Icons.Default.PlayArrow,      "practice"),
            Feature(stringResource(R.string.sandbox_text),         Icons.Default.EditNote,       "sandbox"),
            Feature(stringResource(R.string.test_mode_text),       Icons.AutoMirrored.Filled.Assignment, "test_mode"),
            Feature(stringResource(R.string.score_history_text),   Icons.AutoMirrored.Filled.Notes,      "score_history"),
            Feature(stringResource(R.string.settings_heading_text),Icons.Default.Settings,        "settings")
        )

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 140.dp),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(features) { feature ->
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    tonalElevation = 4.dp,
                    modifier = Modifier
                        .clickable { navController.navigate(feature.route) }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = feature.icon,
                            contentDescription = feature.title,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = feature.title,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}