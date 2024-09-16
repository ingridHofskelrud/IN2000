package no.uio.ifi.in2000.team33.smaafly.model.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Favorite location data class for room database
 *
 * @param name Name of location
 * @param lat Latitude of location
 * @param lon Longtitude of location
 * @param height Height of location in meters
 * @param icao ICAO code for location if applicable
 */
@Entity
data class Location(
    @PrimaryKey val name: String,
    @ColumnInfo(name = "lat") val lat: Double,
    @ColumnInfo(name = "lon") val lon: Double,
    @ColumnInfo(name = "height") val height: Int,
    @ColumnInfo(name = "icao") val icao: String?,
)
