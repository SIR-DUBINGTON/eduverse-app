package com.example.eduverse.ui.screens.problems

import android.os.Build
import android.view.View
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun SandboxNotepadScreen(
    interactionViewModel: ProblemInteractionViewModel = viewModel()
) {
    var notesInput by remember { mutableStateOf("") }
    var answerInput by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }
    var webView: WebView? = null

    val currentProblem = interactionViewModel.currentProblem
    val expectedAnswer = currentProblem?.expectedAnswer ?: ""
    val expected = currentProblem?.expectedValue?.lowercase() ?: ""
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Problem Sandbox", style = MaterialTheme.typography.headlineSmall)

        currentProblem?.let {
            Text(text = it.title, style = MaterialTheme.typography.titleMedium)
            Text(text = it.description, style = MaterialTheme.typography.bodyLarge)
        }

        // Notes section (not evaluated)
        OutlinedTextField(
            value = notesInput,
            onValueChange = { notesInput = it },
            label = { Text("Scratchpad / Notes (not evaluated)") },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            singleLine = false,
            maxLines = 10
        )

        // Answer section (evaluated)
        OutlinedTextField(
            value = answerInput,
            onValueChange = {
                answerInput = it
                webView?.loadDataWithBaseURL(null, generateKaTeXHtml(it), "text/html", "utf-8", null)
            },
            label = { Text("Final Working & Answer (evaluated)") },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            singleLine = false,
            maxLines = 10
        )

        Text("Live Preview:", style = MaterialTheme.typography.titleMedium)

        // KaTeX WebView preview for answer
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(Color.White),
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        setLayerType(View.LAYER_TYPE_HARDWARE, null)
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        setLayerType(View.LAYER_TYPE_HARDWARE, null)
                    } else {
                        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                    }
                    webChromeClient = object : WebChromeClient() {
                        override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                            Log.d("KaTeXSandbox", consoleMessage?.message() ?: "null")
                            return true
                        }
                    }
                    webView = this
                    loadDataWithBaseURL(null, generateKaTeXHtml(answerInput), "text/html", "utf-8", null)
                }
            },
            update = { webView = it }
        )

        // Submit and evaluate
        Button(
            onClick = {
                // 1) Plain‐text expected value, loaded from the Problem model
                val expectedText = currentProblem?.expectedValue?.lowercase()?.trim() ?: ""
                // 2) Try a simple substring match (allows users to type units, e.g. "24 J")
                val textMatch = answerInput.lowercase().contains(expectedText)

                // 3) Fallback: extract first numeric token and compare numerically
                fun extractNumber(s: String) =
                    Regex("""-?\d+(\.\d+)?""")
                        .find(s)?.value
                        ?.toDoubleOrNull()
                val gotNum = extractNumber(answerInput)
                val wantNum = extractNumber(expectedText)

                val numericMatch = (gotNum != null && wantNum != null && kotlin.math.abs(gotNum - wantNum) < 1e-6)

                if (textMatch || numericMatch) {
                    isCorrect = true
                    dialogMessage = "Correct!"
                } else {
                    isCorrect = false
                    dialogMessage = "Incorrect. Expected: $expectedText"
                }
                showDialog = true
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Submit Answer")
        }


        // Result dialog
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("OK")
                    }
                },
                title = { Text(if (isCorrect) "Result" else "Try Again") },
                text = { Text(dialogMessage) }
            )
        }
    }
}

fun generateKaTeXHtml(input: String): String {
    val safeLatex = input.replace("$", "\\$")
    return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset='utf-8'>
            <link rel='stylesheet' href='file:///android_asset/katex/katex.min.css'>
            <script src='file:///android_asset/katex/katex.min.js'></script>
            <script src='file:///android_asset/katex/auto-render.min.js'></script>
            <script>
                window.onload = function() {
                    renderMathInElement(document.body, {
                        delimiters: [ {left: "$$", right: "$$", display: true} ]
                    });
                };
            </script>
            <style>
                body {
                    font-size: 22px;
                    padding: 20px;
                    background-color: white;
                    margin: 0;
                }
            </style>
        </head>
        <body>
            <p>$$${safeLatex}$$</p>
        </body>
        </html>
    """.trimIndent()
}
