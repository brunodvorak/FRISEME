package com.example.frifittracker.session.sessionDatabase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.frifittracker.session.SessionItem

/**
 * Data Access Object (DAO) for the session database.
 */
@Dao
interface SessionDataClassDao {
    /**
     * Inserts an exercise entity into the database.
     *
     * @param exerciseEntity The exercise entity to be inserted.
     */
    @Insert
    fun insertExercise(exerciseEntity: ExerciseEntity)

    /**
     * Retrieves a list of exercises for a given date.
     *
     * @param date The date for which to retrieve the session items.
     * @return A list of session items for the specified date.
     */
    @Query("SELECT * FROM exercise_table WHERE date = :date")
    fun getExercisesByDate(date: String): List<SessionItem>

    /**
     * Deletes all exercises for a given date.
     *
     * @param date The date for which to delete the exercises.
     */
    @Query("DELETE FROM exercise_table WHERE date = :date")
    fun deleteExercises(date: String)
}