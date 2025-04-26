package com.example.eduverse.ui.screens.problems

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Represents a programming problem with its associated details.
 *
 * This data class encapsulates information about a single problem,
 * including its unique identifier, title, description, difficulty level,
 * the expected answer format, and the expected value type.
 *
 * @property id The unique identifier for the problem.
 * @property title The title or name of the problem.
 * @property description A detailed description of the problem statement.
 * @property difficulty The difficulty level of the problem (e.g., "Easy," "Medium," "Hard").
 * @property expectedAnswer A description of the expected answer format (e.g., "Integer," "String," "List").
 * @property expectedValue The data type or value type expected for the output (e.g. "Int", "String", "List<Int>").
 */
data class Problem(
    val id: Int,
    val title: String,
    val description: String,
    val difficulty: String,
    val expectedAnswer: String,
    val expectedValue: String
)

/**
 * Interface defining the API endpoints for retrieving problem data.
 */
interface ProblemApi {
    @GET("~2207059/cmp309/problems.json")
    suspend fun getProblems(): List<Problem>
}


/**
 * `PracticeViewModel` is a ViewModel responsible for managing the state and
 * data related to practice problems. It fetches problems from a remote API,
 * handles loading and error states, and exposes the data to the UI.
 *
 * It uses Retrofit for network communication and coroutines for asynchronous
 * operations.
 *
 * @property problems A list of [Problem] objects fetched from the API.
 *                   It's a mutable state that updates when new problems are loaded.
 * @property loading A boolean indicating whether the problems are currently being loaded.
 *                   True during data fetching, false otherwise.
 * @property errorDetail A string containing error details if an error occurred during data fetching.
 *                      Null if no error has occurred.
 */
class PracticeViewModel : ViewModel() {
    var problems by mutableStateOf<List<Problem>>(emptyList())
    var loading by mutableStateOf(true)
    var errorDetail by mutableStateOf<String?>(null)

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://mayar.abertay.ac.uk/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(ProblemApi::class.java)

    init {
        loadProblems()
    }

    /**
     * Loads problems from the API.
     *
     * This function fetches a list of problems from the remote API using the `api.getProblems()` method.
     * It handles potential exceptions during the API call and updates the `loading` and `errorDetail` properties accordingly.
     *
     * **Process:**
     * 1. Sets the `loading` flag to `true` to indicate that data fetching has started.
     * 2. Clears any previous error messages by setting `errorDetail` to `null`.
     * 3. Launches a coroutine in the `viewModelScope` to perform the network operation asynchronously.
     * 4. **Try Block:**
     *    - Attempts to retrieve the list of problems by calling `api.getProblems()`.
     *    - If successful, the retrieved list is assigned to the `problems` property.
     * 5. **Catch Block:**
     *    - If an exception occurs during the API call:
     *      - The `errorDetail` property is set to the localized message of the exception.
     *      - An error log is recorded using `Log.e` to provide debugging information.
     * 6. **Finally Block:**
     *    - Regardless of success or failure, the `loading` flag is set to `false`, indicating that the data fetching operation has completed.
     *
     * **Side Effects:**
     * - Modifies the `loading` property.
     * - Modifies the `errorDetail` property.
     * - Modifies the `problems` property if the API call is successful.
     * - Logs error messages to the Android Logcat in case of failures.
     *
     * **Error Handling:**
     * - If any `Exception` occurs during the API call, the `errorDetail` will contain the exception's localized message.
     * - The specific type of exception is also included in the log message.
     *
     * **Threading:**
     */
    private fun loadProblems() {
        loading = true
        errorDetail = null
        viewModelScope.launch {
            try {
                problems = api.getProblems()
            } catch (e: Exception) {
                errorDetail = e.localizedMessage
                Log.e("PracticeViewModel", "Error fetching problems", e)
            } finally {
                loading = false
            }
        }
    }
}