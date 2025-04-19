package com.example.eduverse.ui.screens.problems

import android.os.Build
import android.util.Log
import android.view.View
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import java.time.LocalDate
import kotlin.math.abs

@Composable
fun TestModeScreen(
    navController: NavController,
    practiceViewModel: PracticeViewModel = viewModel(),
    interactionViewModel: ProblemInteractionViewModel = viewModel()
) {
    val problems = practiceViewModel.problems
    val loading = practiceViewModel.loading
    val error   = practiceViewModel.error

    // test state
    var currentIndex by rememberSaveable { mutableStateOf(0) }
    var answerInput by rememberSaveable { mutableStateOf("") }
    val results = remember { mutableStateMapOf<Int, Boolean>() }
    var showSummary by remember { mutableStateOf(false) }

    if (showSummary) {
        TestSummary(
            results = results,
            total = problems.size,
            onRestart = {
                results.clear()
                currentIndex = 0
                answerInput = ""
                showSummary = false
            },
            onBackToProblems = {
                navController.navigate("problems") {
                    popUpTo("problems") { inclusive = true }
                }
            }
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Test Mode", style = MaterialTheme.typography.headlineMedium)

        when {
            loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            error != null -> Text("Error: $error", color = MaterialTheme.colorScheme.error)
            problems.isEmpty() -> Text("No problems available.")
            else -> {
                val problem = problems[currentIndex]
                val expectedValue = problem.expectedValue.lowercase().trim()

                Text("Problem ${currentIndex + 1} of ${problems.size}",
                    style = MaterialTheme.typography.titleSmall)
                Spacer(Modifier.height(4.dp))
                Text(problem.title, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Text(problem.description, style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = answerInput,
                    onValueChange = { answerInput = it },
                    label = { Text("Your Answer") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
                Spacer(Modifier.height(8.dp))

                // Live preview
                KaTeXPreview(generateKaTeXHtml(answerInput))
                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = {
                        // 1) text match
                        val textMatch = answerInput.lowercase().contains(expectedValue)
                        // 2) numeric match
                        fun extractNum(s: String) =
                            Regex("""-?\d+(\.\d+)?""").find(s)?.value?.toDoubleOrNull()
                        val got = extractNum(answerInput)
                        val want = extractNum(expectedValue)
                        val numericMatch = got != null && want != null && abs(got - want) < 1e-6

                        results[problem.id] = (textMatch || numericMatch)
                        if (currentIndex == problems.lastIndex) showSummary = true
                        else {
                            currentIndex += 1
                            answerInput = ""
                        }
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(if (currentIndex == problems.lastIndex) "Finish Test" else "Next")
                }
            }
        }
    }
}

@Composable
fun TestSummary(
    results: Map<Int, Boolean>,
    total: Int,
    onRestart: () -> Unit,
    onBackToProblems: () -> Unit
) {
    val correct = results.values.count { it }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Test Complete!", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(12.dp))
        Text("Score: $correct / $total", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(24.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(onClick = onRestart) {
                Text("Retake Test")
            }
            Button(onClick = onBackToProblems) {
                Text("Back to Problems")
            }
        }
    }
}


@Composable
fun KaTeXPreview(htmlContent: String) {
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        factory = { ctx ->
            WebView(ctx).apply {
                settings.javaScriptEnabled = true
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    setLayerType(View.LAYER_TYPE_HARDWARE, null)
                } else {
                    setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                }
                webChromeClient = object : WebChromeClient() {
                    override fun onConsoleMessage(cm: ConsoleMessage?) = true.also {
                        Log.d("KaTeX", cm?.message() ?: "")
                    }
                }
                loadDataWithBaseURL(null, htmlContent, "text/html", "utf-8", null)
            }
        },
        update = { it.loadDataWithBaseURL(null, htmlContent, "text/html", "utf-8", null) }
    )
}
