package no.uio.ifi.in2000.team33.smaafly.domain.getairports

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import no.uio.ifi.in2000.team33.smaafly.data.airports.AirportRepository
import no.uio.ifi.in2000.team33.smaafly.model.airports.Airport

/**
 * Use case for fetching airports
 *
 * @param airportRepository Repository with airports
 */
class GetAirportsUseCase(
    private val airportRepository: AirportRepository
) {

    /**
     * Fetch airports
     *
     * @param context Context required for opening local json file
     * @return List of airports
     */
    suspend operator fun invoke(context: Context): List<Airport> =
        withContext(Dispatchers.IO) {
            return@withContext airportRepository.fetchAirports(context)
        }
}
