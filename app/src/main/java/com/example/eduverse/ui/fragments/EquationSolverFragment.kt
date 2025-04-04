package com.example.eduverse.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eduverse.R
import com.example.eduverse.adapters.FormulaAdapter
import com.example.eduverse.data.FormulaProvider
import com.example.eduverse.engine.EquationEngine
import android.text.TextWatcher
import com.example.eduverse.models.Formula
import android.webkit.WebView

class EquationSolverFragment : Fragment() {

    private lateinit var formulaRecyclerView: RecyclerView
    private lateinit var inputFieldsContainer: LinearLayout
    private lateinit var calculateButton: Button
    private lateinit var resultText: TextView

    private var selectedFormula: Formula? = null
    private val variableInputs = mutableMapOf<String, EditText>()
    private val G = 6.67430e-11 // gravitational constant
    private lateinit var mathWebView: WebView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_equation_solver, container, false)

        mathWebView = view.findViewById(R.id.mathWebView)
        mathWebView.settings.javaScriptEnabled = true
        formulaRecyclerView = view.findViewById(R.id.formulaRecyclerView)
        inputFieldsContainer = view.findViewById(R.id.inputFieldsContainer)
        calculateButton = view.findViewById(R.id.calculateButton)
        resultText = view.findViewById(R.id.resultText)

        setupFormulaRecyclerView()
        calculateButton.setOnClickListener { solveEquation() }

        return view
    }

    private fun setupFormulaRecyclerView() {
        formulaRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val adapter = FormulaAdapter(FormulaProvider.formulas) { selected ->
            selectedFormula = selected
            generateInputFields(selected.equation)
            updateFormulaPreview() // 👈 update the WebView instead of a TextView

        // ✅ Generate and load the KaTeX HTML into the WebView
            val katexHtml = generateKaTeXHtml(selected.equation)
            mathWebView.loadDataWithBaseURL(
                "file:///android_asset/katex/",
                katexHtml,
                "text/html",
                "utf-8",
                null
            )
        }

        formulaRecyclerView.adapter = adapter
    }


    private fun generateInputFields(equation: String) {
        inputFieldsContainer.removeAllViews()
        variableInputs.clear()

        val variables = extractVariables(equation)

        for (variable in variables) {
            val inputField = EditText(requireContext()).apply {
                hint = "Enter $variable"
                inputType = InputType.TYPE_CLASS_NUMBER or
                        InputType.TYPE_NUMBER_FLAG_DECIMAL or
                        InputType.TYPE_NUMBER_FLAG_SIGNED
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(8, 8, 8, 8)
                }
            }
            // Attach a text listener to update the preview
            inputField.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) { updateFormulaPreview() }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {} })
            variableInputs[variable] = inputField
            inputFieldsContainer.addView(inputField)
        }
    }

    private fun updateFormulaPreview(result: String? = null) {
        if (selectedFormula == null) return

        val originalEquation = selectedFormula!!.equation
        var previewEquation = originalEquation

        // Replace only known variables with their values
        variableInputs.forEach { (variable, input) ->
            val value = input.text.toString()
            if (value.isNotEmpty()) {
                previewEquation = previewEquation.replace(Regex("\\b$variable\\b"), value)
            }
        }

        // Optional: append the result if provided
        val finalEquation = if (result != null) {
            "$previewEquation\\\\ \\text{Result: } $result"
        } else {
            previewEquation
        }

        val html = generateKaTeXHtml(finalEquation)
        mathWebView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null)
    }


    private fun extractVariables(equation: String): List<String> {
        val reservedWords = listOf("G", "sqrt", "Sqrt")
        return equation
            .split(" ", "=", "+", "-", "*", "/", "(", ")", "^")
            .filter { it.matches(Regex("[a-zA-Z][a-zA-Z0-9]*")) }
            .filterNot { reservedWords.contains(it) }
            .distinct()
    }

    private fun solveEquation() {
        if (selectedFormula == null) return

        val equation = selectedFormula!!.equation
        val variables = extractVariables(equation)

        var unknownVariable: String? = null
        val knownValues = mutableMapOf<String, Double>()

        for (variable in variables) {
            val inputField = variableInputs[variable]
            val value = inputField?.text.toString().toDoubleOrNull()

            if (value == null) {
                if (unknownVariable == null) {
                    unknownVariable = variable
                } else {
                    resultText.text = "Error: Only one variable should be left empty."
                    return
                }
            } else {
                knownValues[variable] = value
            }
        }

        if (unknownVariable == null) {
            resultText.text = "Error: Leave one variable empty to compute it."
            return
        }

        try {
            val result = EquationEngine.solve(equation, unknownVariable, knownValues)
            resultText.text = "$unknownVariable = $result"

            // Update formula preview to show result too!
            updateFormulaPreview("$unknownVariable = $result")
        } catch (e: Exception) {
            resultText.text = "Error: ${e.message}"
            updateFormulaPreview() // fallback to normal preview
        }

    }

    private fun generateKaTeXHtml(mathExpression: String): String {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="utf-8">
                <link rel="stylesheet" href="file:///android_asset/katex/katex.min.css">
                <script defer src="file:///android_asset/katex/katex.min.js"></script>
                <script defer src="file:///android_asset/katex/auto-render.min.js"
                        onload="renderMathInElement(document.body);"></script>
                <style>
                    body {
                        font-size: 18px;
                        padding: 16px;
                        margin: 0;
                    }
                </style>
            </head>
            <body>
                <p>$$$mathExpression$$</p>
            </body>
            </html>
        """.trimIndent()
    }

}

