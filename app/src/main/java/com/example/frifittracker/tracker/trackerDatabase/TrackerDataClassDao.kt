import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.frifittracker.tracker.TrackerItem
import com.example.frifittracker.tracker.trackerDatabase.TrackerEntity

/**
 * Data Access Object (DAO) interface for accessing and manipulating data in the tracker_table of the tracker database.
 */
@Dao
interface TrackerDataClassDao {
    /**
     * Inserts a [TrackerEntity] object into the tracker_table.
     *
     * @param trackerEntity The [TrackerEntity] to be inserted.
     */
    @Insert
    fun insertValues(trackerEntity: TrackerEntity)

    /**
     * Deletes all values from the tracker_table.
     */
    @Query("DELETE FROM tracker_table")
    fun deleteValues()

    /**
     * Retrieves all values from the tracker_table.
     *
     * @return A list of [TrackerItem] objects representing the values in the tracker_table.
     */
    @Query("SELECT * FROM tracker_table")
    fun getValues(): List<TrackerItem>

    /**
     * Returns the number of rows in the tracker_table.
     *
     * @return The number of rows in the tracker_table.
     */
    @Query("SELECT COUNT(*) FROM tracker_table")
    fun getRowCount(): Int
}