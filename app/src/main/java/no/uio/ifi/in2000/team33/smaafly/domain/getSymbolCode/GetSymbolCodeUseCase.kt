package no.uio.ifi.in2000.team33.smaafly.domain.getSymbolCode

import no.uio.ifi.in2000.team33.smaafly.data.locationforecast.LocationForecastRepository

/**
 * Use case for getting weather symbol code
 *
 * @param locationForecastRepository Repository for location forecast
 */
class GetSymbolCodeUseCase(private val locationForecastRepository: LocationForecastRepository) {

    /**
     * Get weather symbol code
     *
     * @param lat Latitude of point
     * @param lon Longitude of point
     * @return Weather symbol code as string
     */
    suspend operator fun invoke(lat: Double, lon: Double): String? {
        return locationForecastRepository.getSymbolIdNow(lat, lon)
    }
}
