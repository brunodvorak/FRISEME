package com.example.frifittracker.session.sessionDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Room database class for the Sessions.
 */
@Database(entities = [ExerciseEntity::class], version = 2)
abstract class SessionDataDatabase : RoomDatabase() {

    /**
     * Returns the DAO (Data Access Object) for the session data.
     */
    abstract val sessionDataClassDao: SessionDataClassDao

    companion object {
        @Volatile
        private var INSTANCE: SessionDataDatabase? = null

        /**
         * Returns the singleton instance of the SessionDataDatabase.
         *
         * @param context The application context.
         * @return The instance of the SessionDataDatabase.
         */
        fun getInstance(context: Context): SessionDataDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        SessionDataDatabase::class.java,
                        "exercise_list_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
