package no.uio.ifi.in2000.team33.smaafly.data.locationforecast


interface LocationForecastRepository {
    suspend fun getStatus(): String
    suspend fun getAirPressureSeaLevelNow(lat: Double, lon: Double): Double
    suspend fun getAirTemperatureSeaLevelNow(lat: Double, lon: Double): Double

    suspend fun getSymbolIdNow(lat: Double, lon: Double): String?
}

const val KELVIN_OFFSET = 273.15

class LocationForecastRepositoryImpl(
    private val locationForecastDataSource: LocationForecastDataSource,
) : LocationForecastRepository {
    /**
     * Gets the date and time of the last update of the API.
     * The date is in ISO 8601 format: YYYY-MM-DDThh:mm:ssZ.
     *
     * @return The last update date and time as a String.
     */
    override suspend fun getStatus(): String {
        return locationForecastDataSource.getStatus()
    }

    /**
     * Gets the most recent air pressure at sea level at a specific point as hecto Pascal (hPa),
     * given longitude and latitude.
     *
     *  @param lat The latitude of the point
     *  @param lon The longitude of the point
     *  @return The air pressure at sea level as a Double.
     */
    override suspend fun getAirPressureSeaLevelNow(
        lat: Double,
        lon: Double
    ): Double { // TODO: Error handling?
        return locationForecastDataSource
            .getForecastTimeseries(lat, lon)
            .properties
            .timeseries
            .first()
            .data
            .instant
            .details
            .air_pressure_at_sea_level
    }

    /**
     * Gets the most recent air temperature at sea level at a specific point as Kelvin (K), given
     * longitude and latitude.
     *
     *  @param lat The latitude of the point
     *  @param lon The longitude of the point
     *  @return The air temperature at sea level as a Double.
     */
    override suspend fun getAirTemperatureSeaLevelNow(lat: Double, lon: Double): Double {
        val celsius = locationForecastDataSource
            .getForecastTimeseries(lat, lon)
            .properties.timeseries
            .first()
            .data
            .instant
            .details
            .air_temperature

        return celsius + KELVIN_OFFSET
    }

    /**
     *Gets the most recent symbol code at a specific point, given latitude and longitude
     *
     * @param lat The latitude of the point
     * @param lon The longitude of the point
     * @return The symbol code as String
     */
    override suspend fun getSymbolIdNow(lat: Double, lon: Double): String? {

        return locationForecastDataSource.getForecastTimeseries(lat, lon)
            .properties.timeseries
            .first()
            .data
            .next_1_hours
            ?.summary
            ?.symbol_code
    }
}

class FakeLocationForecastRepository : LocationForecastRepository {
    override suspend fun getStatus(): String {
        return ""
    }

    override suspend fun getAirPressureSeaLevelNow(lat: Double, lon: Double): Double {
        return 101325.0
    }

    override suspend fun getAirTemperatureSeaLevelNow(lat: Double, lon: Double): Double {
        return 288.0
    }

    override suspend fun getSymbolIdNow(lat: Double, lon: Double): String {
        TODO("Not yet implemented")
    }

}
