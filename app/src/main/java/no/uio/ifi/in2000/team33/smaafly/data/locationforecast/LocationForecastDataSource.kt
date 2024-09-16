package no.uio.ifi.in2000.team33.smaafly.data.locationforecast

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.http.path
import no.uio.ifi.in2000.team33.smaafly.model.locationforecast.GeoJsonForecastTimeseriesDTO
import no.uio.ifi.in2000.team33.smaafly.model.locationforecast.Status
import javax.inject.Named

/**
 * Data source for Location Forecast
 *
 * @param client HttpClient for fetching data from remote API
 */
class LocationForecastDataSource(@Named("proxy") private val client: HttpClient) {
    private val apiPath = "weatherapi/locationforecast/2.0/"

    /**
     * Gets the date and time of the last update of the API.
     * The date is in ISO 8601 format: YYYY-MM-DDThh:mm:ssZ.
     *
     * @return Date in ISO 8601 format
     */
    suspend fun getStatus(): String {
        val response: HttpResponse = client.get(apiPath + "status")
        val status: Status = response.body()
        Log.d("api", "Location Forecast status API call")
        return status.last_update
    }

    /**
     * Fetch forecast data
     *
     * @return Forecast data
     */
    suspend fun getForecastTimeseries(lat: Double, lon: Double): GeoJsonForecastTimeseriesDTO {
        val response: HttpResponse = client.get {
            url {
                path(apiPath + "compact")
                parameter("lat", lat.toString())
                parameter("lon", lon.toString())
            }
        }
        val geoJsonForecastTimeseriesDTO: GeoJsonForecastTimeseriesDTO = response.body()
        Log.d("api", "Location Forecast data API call")
        return geoJsonForecastTimeseriesDTO
    }
}

