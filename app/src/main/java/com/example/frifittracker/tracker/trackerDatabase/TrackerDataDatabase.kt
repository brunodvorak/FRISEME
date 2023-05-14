import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.frifittracker.tracker.trackerDatabase.TrackerEntity

/**
 * Database class for the tracker data. This class provides a singleton instance of the database
 * and serves as the main access point for interacting with the tracker data.
 */
@Database(entities = [TrackerEntity::class], version = 1)
abstract class TrackerDataDatabase : RoomDatabase() {
    /**
     * Returns the DAO (Data Access Object) interface for the tracker data.
     */
    abstract val trackerDataClassDao: TrackerDataClassDao

    companion object {
        @Volatile
        private var INSTANCE: TrackerDataDatabase? = null

        /**
         * Returns the singleton instance of the TrackerDataDatabase.
         *
         * @param context The application context.
         * @return The TrackerDataDatabase instance.
         */
        fun getInstance(context: Context): TrackerDataDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        TrackerDataDatabase::class.java,
                        "tracker_list_database"
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