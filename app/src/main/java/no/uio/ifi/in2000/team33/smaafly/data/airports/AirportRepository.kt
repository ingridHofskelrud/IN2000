package no.uio.ifi.in2000.team33.smaafly.data.airports

import android.annotation.SuppressLint
import android.content.Context
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import no.uio.ifi.in2000.team33.smaafly.model.airports.Airport

/**
 * Repository for airports
 *
 * Caches airport information
 */
class AirportRepository(private val dataSource: AirportDataSource) {

    private var airports: List<Airport> = emptyList()
    private val airportMutex = Mutex()

    /**
     * Fetch airports from cache,
     * if not available,
     * read airports from local json file
     *
     * @param context Context required for opening local json file
     * @return List of airports deserialized from local json file
     */
    @SuppressLint("DefaultLocale")
    suspend fun fetchAirports(context: Context): List<Airport> {
        if (airports.isEmpty()) {
            val fetch = dataSource.fetchAirports(context)
            airportMutex.withLock {
                airports = fetch.map { airport ->
                    airport.copy(
                        lat = String.format("%.4f", airport.lat).toDouble(),
                        long = String.format("%.4f", airport.long).toDouble()
                    )
                }
            }
        }
        return airports
    }

}