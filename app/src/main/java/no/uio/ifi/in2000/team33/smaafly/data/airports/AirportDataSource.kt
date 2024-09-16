package no.uio.ifi.in2000.team33.smaafly.data.airports

import android.content.Context
import android.util.Log
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import no.uio.ifi.in2000.team33.smaafly.model.airports.Airport
import no.uio.ifi.in2000.team33.smaafly.model.airports.Airports
import java.io.InputStream

/**
 * Class for fetching airports information
 */
class AirportDataSource {

    /**
     * Fetch airports from local json file
     *
     * @param context Context required for opening local json file
     * @return List of airports deserialized from local json file
     */
    @OptIn(ExperimentalSerializationApi::class)
    fun fetchAirports(context: Context): List<Airport> {
        val inputStream: InputStream = context.assets.open("AirportsNorway.json")

        val airports = Json.decodeFromStream<Airports>(inputStream).airports
        Log.d("airport", "${airports.size}")

        Log.d("api", "Airport API call")
        return airports
    }
}
