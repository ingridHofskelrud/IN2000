package no.uio.ifi.in2000.team33.smaafly.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import no.uio.ifi.in2000.team33.smaafly.model.room.Location

/**
 * DAO for interacting with room database
 */
@Dao
interface LocationDAO {

    /**
     * Get all favorite locations from database
     */
    @Query("SELECT * FROM location")
    fun getAll(): Flow<List<Location>>

    /**
     * Insert location into location table
     *
     * @param location Location to insert
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertLocation(location: Location)

    /**
     * Delete location in location table
     *
     * @param location Location to delete
     */
    @Delete
    fun delete(location: Location)
}