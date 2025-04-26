package com.example.eduverse.ui.screens.problems

import android.util.Log
import android.view.View
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.eduverse.R
import com.example.eduverse.models.Score
import com.example.eduverse.ui.screens.scores.ScoreRepository
import kotlin.math.abs

/**
 * `TestModeScreen` is a composable function that displays a screen for taking a practice test.
 * It presents a series of problems to the user, allows them to input their answers for five questions
 * and displays overall test score at the end out of five while saving the score to the user's history.
 */
@Composable
fun TestModeScreen(
    navController: NavController,
    practiceViewModel: PracticeViewModel = viewModel()
) {
    val context = LocalContext.current

    val problems = practiceViewModel.problems
    val loading = practiceViewModel.loading
    val error   = practiceViewModel.errorDetail

    var currentIndex by rememberSaveable { mutableIntStateOf(0) }
    var answerInput by rememberSaveable { mutableStateOf("") }
    val results = remember { mutableStateMapOf<Int, Boolean>() }
    var showSummary by rememberSaveable { mutableStateOf(false) }

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
            },
            onScoreHistory = {
                navController.navigate("score_history")
            }

        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(stringResource(R.string.test_mode_heading_text), style = MaterialTheme.typography.headlineMedium)

        when {
            loading -> Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            error != null -> Text(
                "${stringResource(R.string.result_error)} $error",
                color = MaterialTheme.colorScheme.error
            )
            problems.isEmpty() -> Text(stringResource(R.string.no_problems_text))
            else -> {
                val problem = problems[currentIndex]
                val expectedValue = problem.expectedValue.lowercase().trim()

                Text(
                    "${stringResource(R.string.problem_of_text)} ${currentIndex + 1} ${stringResource(R.string.of_text)} ${problems.size}",
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(Modifier.height(4.dp))
                Text(problem.title, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Text(problem.description, style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = answerInput,
                    onValueChange = { answerInput = it },
                    label = { Text(stringResource(R.string.your_answer_text)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
                Spacer(Modifier.height(8.dp))

                KaTeXPreview(generateKaTeXHtml(answerInput))
                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = {
                        val textMatch = answerInput.lowercase().contains(expectedValue)
                        fun extractNum(s: String) =
                            Regex("""-?\d+(\.\d+)?""")
                                .find(s)
                                ?.value
                                ?.toDoubleOrNull()
                        val got = extractNum(answerInput)
                        val want = extractNum(expectedValue)
                        val numericMatch =
                            got != null && want != null && abs(got - want) < 1e-6

                        results[problem.id] = (textMatch || numericMatch)

                        if (currentIndex == problems.lastIndex) {
                            val correctCount = results.values.count { it }
                            ScoreRepository.saveScore(
                                context,
                                Score.now(correctCount, problems.size)
                            )
                            showSummary = true
                        } else {
                            currentIndex += 1
                            answerInput = ""
                        }
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        if (currentIndex == problems.lastIndex)
                            stringResource(R.string.finish_text) else stringResource(R.string.next_text)
                    )
                }
            }
        }
    }
}


/**
 * Displays a summary of a completed test.
 *
 * This composable shows the user's score, the total number of questions,
 * and provides buttons for restarting the test, going back to the problem list,
 * and viewing the score history.
 *
 * @param results A map where keys represent question numbers and values are booleans
 *                indicating whether the answer was correct (true) or incorrect (false).
 * @param total The total number of questions in the test.
 * @param onRestart A callback function invoked when the "Restart Test" button is clicked.
 * @param onBackToProblems A callback function invoked when the "Back to Problems" button is clicked.
 * @param onScoreHistory A callback function invoked when the "View Score History" button is clicked.
 */
@Composable
fun TestSummary(
    results: Map<Int, Boolean>,
    total: Int,
    onRestart: () -> Unit,
    onBackToProblems: () -> Unit,
    onScoreHistory: () -> Unit,
) {
    val correct = results.values.count { it }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(stringResource(R.string.test_complete_text), style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(12.dp))
        Text( "${stringResource(R.string.score_text)} $correct / $total", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onRestart,
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.restart_test_text))
            }
            Button(
                onClick = onBackToProblems,
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.back_to_problems_text))
            }
            Button(
                onClick = onScoreHistory,
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.view_score_history_text))
            }
        }
    }
}


/**
 * A Composable function that renders HTML content containing KaTeX expressions in a WebView.
 *
 * This function takes an HTML string as input and displays it in a WebView, leveraging
 * KaTeX for rendering mathematical formulas. It handles enabling JavaScript, allowing mixed
 * content, and logging console messages from the WebView for debugging purposes.
 *
 * @param htmlContent The HTML content string to be rendered. This string should include the
 *                    necessary KaTeX library and any KaTeX expressions within appropriate tags
 *                    (e.g., `<span class="katex">...</span>`).
 *
 * Example usage:
 * ```kotlin
 *  val katexHtml = """
 *      <!DOCTYPE html>
 *      <html>
 *      <head>
 *          <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/katex@0.16.9/dist/katex.min.css" integrity="sha384-n8MVd4RsNIU0tAv4ct0n8Z+wi/b2V+oQ+7gNqY8+c1XyW/o4N7fXvYpQj6t/h4a" crossorigin="anonymous">
 *          <script defer src="https://cdn.jsdelivr.net/npm/katex@0.16.9/dist/katex.min.js" integrity="sha384-XjK6Qj9CJgVf1r/Fh9y2fXwN7Y/p2/Z2i/iXvN6K3K8xJ6fJgXwYvK/K9JgV/f" crossorigin="anonymous"></script>
 */
@Composable
fun KaTeXPreview(htmlContent: String) {
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        factory = { ctx ->
            WebView(ctx).apply {
                settings.javaScriptEnabled = true
                settings.mixedContentMode =
                    WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                setLayerType(View.LAYER_TYPE_HARDWARE, null)
                webChromeClient = object : WebChromeClient() {
                    override fun onConsoleMessage(cm: ConsoleMessage?) = true.also {
                        Log.d("KaTeX", cm?.message() ?: "")
                    }
                }
                loadDataWithBaseURL(
                    null,
                    htmlContent,
                    "text/html",
                    "utf-8",
                    null
                )
            }
        },
        update = {
            it.loadDataWithBaseURL(
                null,
                htmlContent,
                "text/html",
                "utf-8",
                null
            )
        }
    )
}

