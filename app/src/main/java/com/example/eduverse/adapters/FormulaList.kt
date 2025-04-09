package com.example.eduverse.adapters
import com.example.eduverse.models.Formula
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FormulaList(
    formulas: List<Formula>,
    onFormulaClick: (Formula) -> Unit,
    onTheoryClick: (Formula) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(formulas) { formula ->
            FormulaCard(
                formula = formula,
                onClick = { onFormulaClick(formula) },
                onTheoryClick = { onTheoryClick(formula) }
            )
        }
    }
}

@Composable
fun FormulaCard(
    formula: Formula,
    onClick: () -> Unit,
    onTheoryClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() } // apply to just the column, not overlapping the button
                .padding(16.dp)
        ) {
            Text(text = formula.name, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = formula.equation, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onTheoryClick) {
                Text("View Theory")
            }
        }
    }
}

