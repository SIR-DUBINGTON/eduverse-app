package com.example.eduverse.ui.screens.problems

import androidx.lifecycle.ViewModel

/**
 * [ProblemInteractionViewModel] is a ViewModel responsible for managing the interaction
 * with a single [Problem] instance within the application's UI. It holds the currently
 * selected or active problem and provides methods to set and clear this problem.
 *
 * This ViewModel is typically scoped to a fragment or activity that displays
 * details or interacts with a specific problem.
 */
class ProblemInteractionViewModel : ViewModel() {
    var currentProblem: Problem? = null

    /**
     * Sets the currently selected problem.
     *
     * @param problem The [Problem] instance to be set as the current problem.
     */
    fun setProblem(problem: Problem) {
        currentProblem = problem
    }

    /**
     * Clears the currently selected problem.
     */
    fun clearProblem() {
        currentProblem = null
    }
}