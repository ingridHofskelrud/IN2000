package no.uio.ifi.in2000.team33.smaafly.data.grib

import android.annotation.SuppressLint
import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.path
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import no.uio.ifi.in2000.team33.smaafly.model.grib2json.IsobaricGRIBItem
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone
import javax.inject.Named

const val TIME_DELTA = 3

class GribDataSource(@Named("metno") private val client: HttpClient) {

    /**
     * Fetches nearest (0 - 2 hours) isobaric grib data
     *
     * @return List with data for wind and temperature for each isobaric layer
     */
    @SuppressLint("SimpleDateFormat")
    @OptIn(ExperimentalSerializationApi::class)
    suspend fun fetchGribData(): List<IsobaricGRIBItem> = withContext(Dispatchers.IO) {
        var time = getCurrentDateTime()
        Log.d("time", "Time: $time")

        time = getFormattedDateTime(time)

        Log.d("time", "Time: $time")

        /*
        We could not use the IFI proxy because the binary files were corrupted
         */
        val response = client.get {
            url {
                path("isobaricgrib/1.0/grib2")
                parameter("area", "southern_norway")
                parameter("time", time)
            }
        }

        val responseBody: ByteArray = response.body()
        val file = File.createTempFile("out", "bin")
        file.writeBytes(responseBody)

        val outFile = File.createTempFile("output", "json")

        try {
            WriteJson.write(file, outFile)
        } catch (e: Exception) {
            return@withContext emptyList()
        }

        val outStream = BufferedInputStream(FileInputStream(outFile))
        val result: List<IsobaricGRIBItem> = Json.decodeFromStream(outStream)
        Log.d("grib", "Finished fetchGribData")
        Log.d("grib", "Size of list: ${result.size}")

        Log.d("api", "Grib API call")
        return@withContext result
    }

    /**
     * Get current time in ISO 8601 format
     * With minutes and seconds set to 0
     * @return Time in ISO 8601 format
     */
    @SuppressLint("SimpleDateFormat")
    fun getCurrentDateTime(): String {
        val c = Calendar.getInstance()
        val sdf: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH':00:00Z'")
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(c.time)
    }

    /**
     * Format data for every 3 hours
     *
     * @param date Date in ISO 8601 format
     * @return Date with valid hours for grib API get request
     */
    fun getFormattedDateTime(date: String): String {
        val hour = date.substring(11, 13).toInt()
        val offset = (hour + (TIME_DELTA - hour % TIME_DELTA)) - TIME_DELTA
        val replacement = if (offset.toString().length == 2) offset.toString() else "0$offset"
        val time = date.replaceRange(11, 13, replacement)
        return time.replaceRange(14, 19, "00:00")
    }

}