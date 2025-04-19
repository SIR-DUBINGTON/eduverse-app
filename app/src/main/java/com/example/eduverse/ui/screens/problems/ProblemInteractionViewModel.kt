package com.example.eduverse.ui.screens.problems

import androidx.lifecycle.ViewModel
import com.example.eduverse.ui.screens.problems.Problem

class ProblemInteractionViewModel : ViewModel() {
    var currentProblem: Problem? = null

    fun setProblem(problem: Problem) {
        currentProblem = problem
    }

    fun clearProblem() {
        currentProblem = null
    }
}
