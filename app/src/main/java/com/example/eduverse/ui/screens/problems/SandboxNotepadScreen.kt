package com.example.eduverse.ui.screens.problems

import android.content.Context
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.util.Log
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.eduverse.R
import kotlin.math.abs
import androidx.core.content.edit

/**
 * Displays the sandbox notepad screen for a given problem.
 *
 * This screen allows users to:
 * - View the problem title and description.
 * - Enter scratch notes for the problem.
 * - Enter a final working answer for the problem.
 * - Submit the answer and receive feedback.
 * - View a live LaTex preview of the final working answer.
 */
@Composable
fun SandboxNotepadScreen(
    navController: NavController,
    interactionViewModel: ProblemInteractionViewModel
) {
    val context = LocalContext.current
    val problem = interactionViewModel.currentProblem

    if (problem == null) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.no_problems_text),
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.height(16.dp))
            Button(onClick = { navController.popBackStack() }) {
                Text(stringResource(R.string.back_to_problems_text))
            }
        }
        return
    }

    val notesKey = "notes_${problem.id}"
    val answerKey = "answer_${problem.id}"
    val prefs = context.getSharedPreferences("eduverse_prefs", Context.MODE_PRIVATE)
    val correctText    = stringResource(R.string.result_correct)
    val incorrectTpl   = stringResource(R.string.sandbox_result_incorrect)

    var notesInput by rememberSaveable(notesKey) {
        mutableStateOf(prefs.getString(notesKey, "") ?: "")
    }
    var answerInput by rememberSaveable(answerKey) {
        mutableStateOf(prefs.getString(answerKey, "") ?: "")
    }

    var showDialog by rememberSaveable { mutableStateOf(false) }
    var isCorrect by rememberSaveable { mutableStateOf(false) }
    var dialogMessage by rememberSaveable { mutableStateOf("") }

    var webView: WebView? = null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(stringResource(R.string.sandbox_heading_text), style = MaterialTheme.typography.headlineSmall)
        Text(problem.title, style = MaterialTheme.typography.titleMedium)
        Text(problem.description, style = MaterialTheme.typography.bodyLarge)

        OutlinedTextField(
            value = notesInput,
            onValueChange = {
                notesInput = it
                prefs.edit { putString(notesKey, it) }
            },
            label = { Text(stringResource(R.string.scratchpad_text)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            singleLine = false,
            maxLines = 10
        )

        OutlinedTextField(
            value = answerInput,
            onValueChange = {
                answerInput = it
                prefs.edit { putString(answerKey, it) }
                webView?.loadDataWithBaseURL(
                    null,
                    generateKaTeXHtml(it),
                    "text/html",
                    "utf-8",
                    null
                )
            },
            label = { Text(stringResource(R.string.final_working_text)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            singleLine = false,
            maxLines = 10
        )

        Text(stringResource(R.string.live_preview_text), style = MaterialTheme.typography.titleMedium)

        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(Color.White),
            factory = { ctx ->
                WebView(ctx).apply {
                    settings.javaScriptEnabled = true
                    settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    setLayerType(View.LAYER_TYPE_HARDWARE, null)
                    webChromeClient = object : WebChromeClient() {
                        override fun onConsoleMessage(msg: ConsoleMessage?) =
                            super.onConsoleMessage(msg).also {
                                Log.d("KaTeXSandbox", msg?.message() ?: "")
                            }
                    }
                    webView = this
                    loadDataWithBaseURL(null, generateKaTeXHtml(answerInput), "text/html", "utf-8", null)
                }
            },
            update = { it.loadDataWithBaseURL(null, generateKaTeXHtml(answerInput), "text/html", "utf-8", null) }
        )

        DisposableEffect(webView) {
            onDispose { webView?.destroy() }
        }

        Button(
            onClick = {
                val expectedValue = problem.expectedValue.lowercase().trim()
                val textMatch = answerInput.lowercase().contains(expectedValue)
                fun extractNum(s: String) =
                    Regex("""-?\d+(\.\d+)?""").find(s)?.value?.toDoubleOrNull()
                val got = extractNum(answerInput)
                val want = extractNum(expectedValue)
                val numericMatch = got != null && want != null && abs(got - want) < 1e-6

                if (textMatch || numericMatch) {
                    isCorrect = true
                    dialogMessage = correctText
                } else {
                    isCorrect = false
                    dialogMessage = String.format(incorrectTpl, expectedValue)
                }
                showDialog = true
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(stringResource(R.string.submit_answer_text))
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text(stringResource(R.string.OK))
                    }
                },
                title = { Text(if (isCorrect) stringResource(R.string.result_correct) else stringResource(R.string.try_again_text)) },
                text = { Text(dialogMessage) }
            )
        }
    }
}

/* Katex HTML generator */

fun generateKaTeXHtml(input: String): String {
    val safe = input.replace("$", "\\$")
    return """
        <!DOCTYPE html><html><head>
          <meta charset='utf-8'>
          <link rel='stylesheet' href='file:///android_asset/katex/katex.min.css'>
          <script src='file:///android_asset/katex/katex.min.js'></script>
          <script src='file:///android_asset/katex/auto-render.min.js'></script>
          <script>window.onload=()=>renderMathInElement(document.body,{
            delimiters:[{left:'$$',right:'$$',display:true}]
          });</script>
          <style>body{font-size:18px;padding:8px;margin:0;}</style>
        </head><body><p>$$$safe$$</p></body></html>
    """.trimIndent()
}
