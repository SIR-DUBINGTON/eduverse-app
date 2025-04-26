package com.example.eduverse.ui.screens.scores

import android.content.Context
import com.example.eduverse.models.Score
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

/**
 * `ScoreRepository` is a singleton object responsible for managing the persistence of `Score` objects.
 * It handles loading and saving scores to a JSON file stored in the application's internal storage.
 */
object ScoreRepository {
    private const val FILE_NAME = "test_scores.json"
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Score>>() {}.type


    /**
     * Loads a list of scores from a JSON file stored in the application's internal storage.
     *
     * This function attempts to read the file specified by [FILE_NAME] located within
     * the application's files directory. If the file exists, it's content (assumed to be
     * a JSON array of Score objects) is deserialized using Gson. If the file does not exist,
     * or if an error occurs during file reading or JSON parsing, an empty list is returned.
     *
     * @param context The application context, used to access the internal files directory.
     * @return A list of [Score] objects loaded from the file, or an empty list if the file
     *         doesn't exist or if an error occurred during loading.
     *
     * @throws Exception if any exception happens during file reading or json parsing. (Handled internally, returning emptyList)
     */
    fun loadScores(context: Context): List<Score> {
        val file = File(context.filesDir, FILE_NAME)
        if (!file.exists()) return emptyList()
        return try {
            gson.fromJson(file.readText(), typeToken)
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Saves a new score to the persistent storage.
     *
     * This function takes a `Context` and a `Score` object as input. It retrieves the existing scores
     * from the storage (if any), adds the new score to the list, and then writes the updated list
     * back to the storage in JSON format.
     *
     * The data is stored in a file named `FILE_NAME` within the application's internal files directory.
     *
     * @param context The application context, used to access the internal files directory.
     * @param score The `Score` object to be saved.
     *
     * @see loadScores
     * @see Score
     */
    fun saveScore(context: Context, score: Score) {
        val file = File(context.filesDir, FILE_NAME)
        val current = loadScores(context).toMutableList()
        current.add(score)
        file.writeText(gson.toJson(current))
    }
}
