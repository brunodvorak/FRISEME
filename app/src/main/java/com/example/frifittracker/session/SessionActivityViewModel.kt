package com.example.frifittracker.session

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frifittracker.session.sessionDatabase.ExerciseEntity
import com.example.frifittracker.session.sessionDatabase.SessionDataDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel class for the SessionActivity.
 */
class SessionActivityViewModel : ViewModel() {

    /**
     * Loads exercises from the database for the specified date.
     *
     * @param context The application context.
     * @param date The date for which exercises should be loaded.
     * @param onExercisesLoaded Callback function to be called when exercises are loaded.
     */
    fun loadExercises(
        context: Context,
        date: String,
        onExercisesLoaded: (List<SessionItem>) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val database = SessionDataDatabase.getInstance(context)
            val exerciseEntities = database.sessionDataClassDao.getExercisesByDate(date)

            val exerciseList = exerciseEntities.map { entity ->
                SessionItem(
                    exeName = entity.getExeName(),
                    sets = entity.getnumOfSets(),
                    reps = entity.getnumOfReps(),
                    weight = entity.getWeight()
                )
            }

            withContext(Dispatchers.Main) {
                onExercisesLoaded(exerciseList)
            }
        }
    }

    /**
     * Saves the provided list of exercises to the database.
     *
     * @param context The application context.
     * @param exerciseList The list of exercises to be saved.
     * @param date The date associated with the exercises.
     * @param onSaveCompleted Callback function to be called when saving is completed.
     */
    fun saveExercises(
        context: Context,
        exerciseList: List<SessionItem>,
        date: String,
        onSaveCompleted: () -> Unit
    ) {
        val exerciseEntities = exerciseList.map { exercise ->
            ExerciseEntity(
                exeName = exercise.getExeName(),
                sets = exercise.getnumOfSets(),
                reps = exercise.getnumOfReps(),
                weight = exercise.getWeight(),
                date = date
            )
        }

        viewModelScope.launch(Dispatchers.IO) {
            val database = SessionDataDatabase.getInstance(context)

            database.sessionDataClassDao.deleteExercises(date)

            exerciseEntities.forEach { entity ->
                database.sessionDataClassDao.insertExercise(entity)
            }

            withContext(Dispatchers.Main) {
                onSaveCompleted()
            }
        }
    }

}
