package com.example.frifittracker.tracker.trackerDatabase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents an entity in the tracker_table of the tracker database.
 *
 * @property id The unique identifier of the entity (auto-generated).
 * @property bodyPart The body part name.
 * @property value The value of the body part in cm/kg.
 * @property difference The difference in value compared to the previous value.
 */
@Entity(tableName = "tracker_table")
data class TrackerEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "bodyPart")
    val bodyPart: String,

    @ColumnInfo(name = "value")
    val value: Double,

    @ColumnInfo(name = "difference")
    val difference: Double
)
