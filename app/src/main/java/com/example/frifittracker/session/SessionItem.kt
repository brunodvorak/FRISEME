package com.example.frifittracker.session

/**
 * Represents an exercise item in the session.
 *
 * @property exeName The name of the exercise.
 * @property sets The number of sets for the exercise.
 * @property reps The number of repetitions for each set.
 * @property weight The weight used for the exercise.
 */
class SessionItem(private val exeName: String, private val sets: Int, private val reps: Int, private val weight: Int) {

    /**
     * Calculates and returns the total weight lifted for the exercise.
     */
    fun getFullWeight(): Int {
        return weight * reps * sets
    }

    /**
     * Returns the name of the exercise.
     */
    fun getExeName(): String {
        return exeName
    }

    /**
     * Returns the number of sets for the exercise.
     */
    fun getnumOfSets(): Int {
        return sets
    }

    /**
     * Returns the number of repetitions for each set of the exercise.
     */
    fun getnumOfReps(): Int {
        return reps
    }

    /**
     * Returns the weight used for the exercise.
     */
    fun getWeight(): Int {
        return weight
    }
}