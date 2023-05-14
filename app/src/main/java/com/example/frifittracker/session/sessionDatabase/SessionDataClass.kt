package com.example.frifittracker.session.sessionDatabase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents an ExerciseEntity in the session database.
 *
 * @property id The unique identifier of the exercise.
 * @property date The date whe the exercise was done.
 * @property exeName The name of the exercise.
 * @property sets The number of sets performed in the exercise.
 * @property reps The number of repetitions performed in each set of the exercise.
 * @property weight The weight used in the exercise.
 */

@Entity(tableName = "exercise_table")
data class ExerciseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "date")
    val date: String,

    @ColumnInfo(name = "exeName")
    val exeName: String,

    @ColumnInfo(name = "sets")
    val sets: Int,

    @ColumnInfo(name = "reps")
    val reps: Int,

    @ColumnInfo(name = "weight")
    val weight: Int
)
