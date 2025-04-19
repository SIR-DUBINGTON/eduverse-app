package com.example.eduverse.ui.screens.classroom

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.eduverse.data.FormulaProvider
import com.example.eduverse.models.Formula

@Composable
fun ClassroomScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    val allFormulas = FormulaProvider.formulas
    val filtered = allFormulas.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
                it.equation.contains(searchQuery, ignoreCase = true)
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Explore Formula Theory", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search formulas") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filtered) { formula ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)) {
                        Text(text = formula.name, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = formula.equation, style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = {
                            navController.navigate("classroom?formulaTitle=${formula.name}&formulaTheory=${formula.theory}")
                        }) {
                            Text("Read Theory")
                        }
                    }
                }
            }
        }
    }
}
