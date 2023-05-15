package com.example.frifittracker

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frifittracker.session.SessionActivity
import com.example.frifittracker.session.SessionItem
import com.example.frifittracker.session.sessionDatabase.SessionDataDatabase
import com.example.frifittracker.tracker.BodyTrackerActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * The ViewModel class for the MainActivity.
 */
class MainActivityViewModel : ViewModel() {
    /**
     * Loads the exercises from the database for the specified date asynchronously.
     *
     * @param context The application context.
     * @param date The date for which to load the exercises.
     * @param onExercisesLoaded The callback function to be invoked when exercises are loaded.
     *                          It receives the list of loaded exercises as a parameter.
     */
    fun loadExercises(context: Context, date: String, onExercisesLoaded: (List<SessionItem>) -> Unit) {
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
     * Start the SessionActivity to switch to the session screen.
     *
     * @param change Flag indicating whether training is being changed or created.
     */
    fun sessionActivitySwitch(change: Boolean, context: Context, date: String) {
        val intent = Intent(context, SessionActivity::class.java)
        intent.putExtra("change", change)
        intent.putExtra("date", date)
        context.startActivity(intent)
    }

    /**
     * Start the BodyTrackerActivity to switch to the tracker screen.
     */
    fun trackerActivitySwitch(context: Context) {
        val intent = Intent(context, BodyTrackerActivity::class.java)
        context.startActivity(intent)
    }


}