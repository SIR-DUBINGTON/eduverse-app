package com.example.eduverse.ui.screens

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.eduverse.R
import com.example.eduverse.data.FormulaProvider
import com.example.eduverse.engine.EquationEngine
import com.example.eduverse.models.Formula

/**
 * Composable function that represents the Equation Solver screen.
 *
 * This screen allows the user to select a formula, input known values,
 * and calculate the result for an unknown variable. It also provides
 * a live preview of the formula in LaTeX format.
 *
 * @param navController The NavController used for navigation between screens.
 *
 * Functionality:
 *   - Displays a list of available formulas.
 *   - Allows the user to select a formula.
 *   - Allows the user to view the theory of the selected formula.
 *   - Provides input fields for each variable in the selected formula.
 */
@Composable
fun EquationSolverScreen(navController: NavController) {
    val selectedFormula = remember { mutableStateOf<Formula?>(null) }
    val inputs = remember { mutableStateMapOf<String, String>() }
    val latexContent = remember { mutableStateOf("") }
    val resultText = remember { mutableStateOf("") }
    val leaveEmpty = stringResource(R.string.leave_one_field_empty_text)
    val errorPrefix = stringResource(R.string.result_error)

    var webView: WebView? = null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(stringResource(R.string.select_formula_text), style = MaterialTheme.typography.headlineSmall)
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
                            Text(stringResource(R.string.use_formula_text))
                        }
                        Button(onClick = {
                            navController.navigate("classroom?formulaTitle=${formula.name}&formulaTheory=${formula.theory}")
                        }) {
                            Text(stringResource(R.string.view_theory_text))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        selectedFormula.value?.let { formula ->
            Text(text = "${stringResource(R.string.enter_values_text)} ${formula.name}", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            val variableMap = formula.variables.associateWith { varName ->
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
                        resultText.value = errorPrefix;"${e.message}"
                    }
                } else {
                    resultText.value = leaveEmpty
                }
            }) {
                Text(stringResource(R.string.calculate_result_text))
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(resultText.value, style = MaterialTheme.typography.bodyLarge)

            Text(stringResource(R.string.live_formula_preview_text), style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(Color.White),
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        setLayerType(View.LAYER_TYPE_HARDWARE, null)
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


/**
 * Builds a LaTeX preview string by substituting variables in a LaTeX template with their corresponding values.
 *
 * This function takes a LaTeX string template, a map of input values, and a map defining variable-to-input-key
 * relationships. It then iterates through the variable map, replacing each variable in the template with its
 * corresponding value from the input map, if that value exists and is not blank.
 *
 * The replacement process uses a regular expression to ensure that only whole variables are replaced,
 * avoiding partial replacements within longer words or identifiers.
 *
 * @param latex The LaTeX template string containing variables to be replaced.
 * @param inputs A map where keys represent input identifiers and values are the corresponding string values.
 * @param variableMap A map where keys are input identifiers and values are the corresponding LaTeX variable names.
 *                    These LaTeX variable names are what will be replaced within the `latex` string.
 * @return The modified LaTeX string with variables replaced by their values, or the original LaTeX string if no
 *         replacements were made.
 *
 * @throws IllegalArgumentException if any of the input parameters are invalid.
 *
 * Example:
 * ```
 * val latexTemplate = "The value of x is $x and the value of y is $y."
 * val inputs = mapOf("input_x" to "5", "input_y" to "10")
 * val variableMap = mapOf("input_x" to "\$x", "input_y" to "\$y")
 * val preview = buildLatexPreview(latexTemplate, inputs, variableMap)
 * println(preview) // Output: The value of x is 5 and the value of y is 10.
 *
 * val latexTemplate2 = "The values are $a $b and not $abc."
 * val inputs2 = mapOf("input_a" to "1", "input_b" to "2")
 * val variableMap2 = mapOf("input_a" to "\$a", "input_b" to "\$b")
 * val preview2 = buildLatexPreview(latexTemplate2, inputs2, variableMap2)
 * println(preview2) // Output: The values are 1 2 and not $abc.
 *
 */
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

/**
 * Builds a LaTeX preview string, incorporating the result of a calculation or evaluation.
 *
 * This function takes a LaTeX string, a map of input values, a result string, and a map of variable substitutions.
 * It first builds a LaTeX preview by replacing placeholders in the LaTeX string with their corresponding values.
 * Then, it appends the provided result string to the end of the preview, separated by a double backslash and a space (" \\\\ \\ ").
 * This formatting is standard for displaying a result on a new line in LaTeX.
 *
 * @param latex The base LaTeX string containing potential placeholders for input values.
 * @param inputs A map where keys are placeholder names and values are the strings to replace them with in the LaTeX.
 *               These are used in `buildLatexPreview`.
 * @param result The string representing the result to be displayed after the LaTeX preview.
 * @param variableMap A map where keys are placeholder names for variables in the latex string and values are the strings to replace them with.
 *                    These are used in `buildLatexPreview`.
 * @return A string containing the modified LaTeX preview, followed by a new line and the result string, formatted for LaTeX.
 *
 * @see buildLatexPreview
 *
 * Example:
 * ```kotlin
 * val latex = "The value of \\$x + \\$y is"
 * val inputs = mapOf("x" to "2", "y" to "3")
 * val variableMap = mapOf("x" to "x", "y" to "y")
 * val result = "5"
 * val preview = buildLatexPreviewWithResult(latex, inputs, result, variableMap)
 * // preview will be: "The value of 2 + 3 is \\\\ \\ 5"
 * ```
 */
fun buildLatexPreviewWithResult(latex: String, inputs: Map<String, String>, result: String, variableMap: Map<String, String>): String {
    val replaced = buildLatexPreview(latex, inputs, variableMap)
    return "$replaced \\\\ \\ $result"
}

/**
 * Generates an HTML string that renders LaTeX content using KaTeX.
 *
 * This function takes a LaTeX string as input and returns a complete HTML document.
 * The HTML document includes the necessary KaTeX CSS and JavaScript files,
 * along with a script to automatically render the LaTeX content within the document's body.
 * The LaTeX content is embedded within a paragraph element enclosed by double dollar signs ($$),
 * which are the delimiters recognized by the auto-render script for display mode.
 *
 * The generated HTML is designed to be used within environments like WebViews, where it can
 * be loaded directly to display the rendered mathematical expressions.
 *
 * The function also includes minimal CSS to control the display of the rendered formula.
 *
 * @param latexContent The LaTeX string to be rendered.
 * @return A complete HTML string that, when loaded, will display the rendered LaTeX.
 *
 * Example:
 * ```kotlin
 * val latex = "\\int_0^\\infty e^{-x^2} dx = \\frac{\\sqrt{\\pi}}{2}"
 * val html = generateKaTeXHtml(latex)
 * // The 'html' variable now contains the complete HTML string to render the LaTeX.
 * ```
 */
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
