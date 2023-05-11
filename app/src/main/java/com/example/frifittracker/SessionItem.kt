package com.example.frifittracker

class SessionItem(private val exeName: String, private val sets: Int, private val reps: Int, private val weight: Int) {

    fun getFullWeight(): Int {
        return weight*reps*sets
    }



    fun getExeName(): String {
        return exeName
    }

    fun getnumOfSets(): Int {
        return sets
    }

    fun getnumOfReps(): Int {
        return reps
    }

    fun getWeight(): Int {
        return weight
    }
}