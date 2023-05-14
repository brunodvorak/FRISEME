package com.example.frifittracker.tracker

import kotlin.math.roundToInt

/**
 * Represents a tracker item for a specific body part.
 * @property bodyPart The name of the body part.
 * @property value The value associated with the body part.
 * @property difference The difference between the current and previous value.
 */
class TrackerItem(
    private val bodyPart: String,
    private var value: Double,
    private var difference: Double = 0.0
) {

    /**
     * Returns the name of the body part.
     */
    fun getBodyPart(): String {
        return bodyPart
    }

    /**
     * Returns the value associated with the body part.
     */
    fun getValue(): Double {
        return value
    }

    /**
     * Sets the value of the body part.
     * Calculates and updates the difference between the new value and the previous value.
     * @param newValue The new value to be set.
     */
    fun setValue(newValue: Double) {
        val passedValue = (newValue * 100.0).roundToInt() / 100.0
        if (value != 0.0) difference = passedValue - value
        value = passedValue
    }

    /**
     * Returns the difference between the current and previous value.
     */
    fun getDifference(): Double {
        return (difference * 100.0).roundToInt() / 100.0
    }
}