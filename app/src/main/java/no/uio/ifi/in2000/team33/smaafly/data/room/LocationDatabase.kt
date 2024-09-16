package no.uio.ifi.in2000.team33.smaafly.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import no.uio.ifi.in2000.team33.smaafly.model.room.Location

/**
 * Database for favorite locations
 */
@Database(entities = [Location::class], version = 1)
abstract class LocationDatabase : RoomDatabase() {

    /**
     * Get location DAO
     */
    abstract fun locationDao(): LocationDAO
}