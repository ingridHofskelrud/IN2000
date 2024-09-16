package no.uio.ifi.in2000.team33.smaafly.data.room

import kotlinx.coroutines.flow.Flow
import no.uio.ifi.in2000.team33.smaafly.model.room.Location

/**
 * Repository for interacting with Location Database
 *
 * @param locationDAO DAO for interacting with database
 */
class LocationRepository(
    private val locationDAO: LocationDAO
) {

    /**
     * Get all locations from location table
     */
    fun getAllLocations(): Flow<List<Location>> {
        return locationDAO.getAll()
    }

    /**
     * Insert location into location table
     *
     * @param location Location to insert
     */
    fun insertLocation(location: Location) {
        locationDAO.insertLocation(location)
    }

    /**
     * Delete location in location table
     *
     * @param location Location to delete
     */
    fun deleteLocation(location: Location) {
        locationDAO.delete(location)
    }
    
}