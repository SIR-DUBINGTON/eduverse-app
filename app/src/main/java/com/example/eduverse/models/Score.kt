package com.example.eduverse.models

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Represents a test score recorded at a specific timestamp.
 *
 * This data class stores the number of correct answers and the total number of questions
 * at a given point in time, along with a timestamp indicating when the score was recorded.
 *
 * @property timestamp The timestamp when the score was recorded, formatted as an ISO 8601 date-time string (e.g., "2023-10-27T10:00:00").
 * @property correct The number of correct answers.
 * @property total The total number of questions.
 */
data class Score(
    val timestamp: String,
    val correct: Int,
    val total: Int
) {
    companion object {
        private val fmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        fun now(correct: Int, total: Int) =
            Score(LocalDateTime.now().format(fmt), correct, total)
    }
}