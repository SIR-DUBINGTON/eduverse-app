package com.example.eduverse.ui.screens

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.eduverse.data.FormulaProvider
import com.example.eduverse.engine.EquationEngine
import com.example.eduverse.models.Formula

@Composable
fun EquationSolverScreen(navController: NavController) {
    val selectedFormula = remember { mutableStateOf<Formula?>(null) }
    val inputs = remember { mutableStateMapOf<String, String>() }
    val latexContent = remember { mutableStateOf("") }
    val resultText = remember { mutableStateOf("") }

    var webView: WebView? = null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Select a formula:", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))

        FormulaProvider.formulas.forEach { formula ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(formula.name, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(formula.equation, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = {
                            selectedFormula.value = formula
                            inputs.clear()
                            formula.variables.forEach { inputs[it] = "" }
                            latexContent.value = formula.latex
                            resultText.value = ""
                        }) {
                            Text("Use Formula")
                        }
                        Button(onClick = {
                            navController.navigate("classroom?formulaTitle=${formula.name}&formulaTheory=${formula.theory}")
                        }) {
                            Text("View Theory")
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        selectedFormula.value?.let { formula ->
            Text("Enter values for: ${formula.name}", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            val variableMap = formula.variables.associateWith { varName ->
                // Optional mapping for LaTeX display
                when (varName) {
                    "m1" -> "m_1"
                    "m2" -> "m_2"
                    else -> varName
                }
            }

            formula.variables.forEach { variable ->
                OutlinedTextField(
                    value = inputs[variable] ?: "",
                    onValueChange = {
                        inputs[variable] = it
                        val preview = buildLatexPreview(formula.latex, inputs, variableMap)
                        latexContent.value = preview
                        webView?.loadDataWithBaseURL(null, generateKaTeXHtml(preview), "text/html", "utf-8", null)
                    },
                    label = { Text(variable) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                val knowns = inputs.mapNotNull { (k, v) -> v.toDoubleOrNull()?.let { k to it } }.toMap()
                val unknown = inputs.filterValues { it.isBlank() }.keys.firstOrNull()

                if (unknown != null && knowns.size == formula.variables.size - 1) {
                    try {
                        val resultValue = EquationEngine.solve(formula.equation, unknown, knowns)
                        val fullPreview = buildLatexPreviewWithResult(formula.latex, inputs, "$unknown = $resultValue", variableMap)
                        latexContent.value = fullPreview
                        webView?.loadDataWithBaseURL(null, generateKaTeXHtml(fullPreview), "text/html", "utf-8", null)
                        resultText.value = "$unknown = $resultValue"
                    } catch (e: Exception) {
                        resultText.value = "Error: ${e.message}"
                    }
                } else {
                    resultText.value = "Leave exactly one field empty to solve for it."
                }
            }) {
                Text("Calculate Result")
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(resultText.value, style = MaterialTheme.typography.bodyLarge)

            Text("Live Formula Preview:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
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
                                Log.d("LatexWebView", consoleMessage?.message() ?: "null")
                                return true
                            }
                        }
                        webView = this
                        loadDataWithBaseURL(null, generateKaTeXHtml(latexContent.value), "text/html", "utf-8", null)
                    }
                },
                update = {
                    webView = it
                }
            )
        }
    }
}

fun buildLatexPreview(latex: String, inputs: Map<String, String>, variableMap: Map<String, String>): String {
    var output = latex
    variableMap.forEach { (inputKey, latexVar) ->
        val value = inputs[inputKey]
        if (!value.isNullOrBlank()) {
            output = output.replace(Regex("(?<![a-zA-Z0-9_])$latexVar(?![a-zA-Z0-9_])"), value)
        }
    }
    return output
}

fun buildLatexPreviewWithResult(latex: String, inputs: Map<String, String>, result: String, variableMap: Map<String, String>): String {
    val replaced = buildLatexPreview(latex, inputs, variableMap)
    return "$replaced \\\\ \\ $result"
}

fun generateKaTeXHtml(latexContent: String): String {
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
            <p>$$$latexContent$$</p>
        </body>
        </html>
    """.trimIndent()
}
