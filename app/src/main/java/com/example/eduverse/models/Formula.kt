package com.example.eduverse.models

data class Formula(
    val name: String,   // Formula Name (e.g., "Newton’s Second Law")
    val equation: String, // Actual equation (e.g., "F = m * a")
    val variables: List<String>,
    val theory: String, // Could be theory ID or plain text
    val latex: String
)
