package com.example.frifittracker.tracker

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frifittracker.tracker.trackerDatabase.TrackerEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.frifittracker.tracker.trackerDatabase.TrackerDataDatabase

/**
 * ViewModel class for the BodyTrackerActivity.
 * This class handles the business logic and data operations related to the BodyTrackerActivity.
 * It provides data to the activity and allows the activity to interact with the underlying data.
 */
class BodyTrackerActivityViewModel : ViewModel() {

    /**
     * Loads the tracker values from the database.
     * Retrieves the values from the database using the trackerDataClassDao.
     * Converts the retrieved TrackerEntity objects into TrackerItem objects.
     * Updates the adapter with the loaded values and notifies the adapter of the data change.
     */
    fun loadValues(context: Context, onValuesLoaded: (List<TrackerItem>) -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val trackerDataDatabase = TrackerDataDatabase.getInstance(context)
                if (trackerDataDatabase.trackerDataClassDao.getRowCount() != 0) {
                    val trackerEntities = trackerDataDatabase.trackerDataClassDao.getValues()

                    val trackerList = trackerEntities.map { trackerItem ->
                        TrackerItem(
                            bodyPart = trackerItem.getBodyPart(),
                            value = trackerItem.getValue(),
                            difference = trackerItem.getDifference()
                        )
                    }

                    withContext(Dispatchers.Main) {
                        onValuesLoaded(trackerList)
                    }
                }
            }
        }
    }

    /**
     * Saves the tracker values to the database.
     *
     * @param trackerList The list of TrackerItems containing the values to be saved.
     * @param context The Context used to access the database.
     */
    fun saveValues(trackerList: List<TrackerItem>, context: Context) {
        val trackerEntities = trackerList.map { trackerItem ->
            TrackerEntity(
                bodyPart = trackerItem.getBodyPart(),
                value = trackerItem.getValue(),
                difference = trackerItem.getDifference()
            )
        }

        viewModelScope.launch(Dispatchers.IO) {
            val trackerDataDatabase = TrackerDataDatabase.getInstance(context)

            // Delete all existing values before inserting new ones
            trackerDataDatabase.trackerDataClassDao.deleteValues()

            // Insert new values one by one
            trackerEntities.forEach { trackerEntity ->
                trackerDataDatabase.trackerDataClassDao.insertValues(trackerEntity)
            }

            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Miery uložené!", Toast.LENGTH_SHORT).show()
            }
        }
    }


}