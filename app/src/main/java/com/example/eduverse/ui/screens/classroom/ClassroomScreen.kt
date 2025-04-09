package com.example.eduverse.ui.screens.classroom

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry

@Composable
fun ClassroomScreen(backStackEntry: NavBackStackEntry) {
    val theoryTitle = backStackEntry.arguments?.getString("formulaTitle") ?: "Formula"
    val theoryContent = backStackEntry.arguments?.getString("formulaTheory") ?: "No theory available for this formula"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = theoryTitle,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Text(
            text = theoryContent,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
