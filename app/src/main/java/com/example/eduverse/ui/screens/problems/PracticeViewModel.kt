package com.example.eduverse.ui.screens.problems

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

data class Problem(
    val id: Int,
    val title: String,
    val description: String,
    val difficulty: String,
    val expectedAnswer: String,
    val expectedValue: String
)

interface ProblemApi {
    @GET("~2207059/cmp309/problems.json")
    suspend fun getProblems(): List<Problem>
}

class PracticeViewModel : ViewModel() {
    var problems by androidx.compose.runtime.mutableStateOf<List<Problem>>(emptyList())
    var loading by androidx.compose.runtime.mutableStateOf(true)
    var error by androidx.compose.runtime.mutableStateOf<String?>(null)

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://mayar.abertay.ac.uk/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(ProblemApi::class.java)

    init {
        loadProblems()
    }

    fun loadProblems() {
        loading = true
        error = null
        viewModelScope.launch {
            try {
                problems = api.getProblems()
            } catch (e: Exception) {
                error = "Failed to load problems: ${e.localizedMessage}"
                Log.e("PracticeViewModel", "Error fetching problems", e)
            } finally {
                loading = false
            }
        }
    }
}
