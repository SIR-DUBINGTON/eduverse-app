package com.example.eduverse.ui.screens.classroom

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Composable function that displays a screen for a formula's theory.
 *
 * This function takes a title and a theory string as input and displays them in a
 * formatted layout using Material Design components. The title is displayed as a headline,
 * and the theory is displayed as body text.
 *
 * @param title The title of the formula theory to be displayed.
 * @param theory The textual explanation of the formula's theory.
 */
@Composable
fun FormulaTheoryScreen(title: String, theory: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = theory,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}


