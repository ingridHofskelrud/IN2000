package no.uio.ifi.in2000.team33.smaafly.data.weather

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import no.uio.ifi.in2000.team33.smaafly.data.grib.GribRepository
import no.uio.ifi.in2000.team33.smaafly.data.locationforecast.LocationForecastRepository
import kotlin.math.abs
import kotlin.math.ln

const val R = 287
const val g = 9.81


interface WeatherRepository {
    /**
     * Get wind data (U and V component) given height and coordinate
     *
     * @param height Height in meters
     * @param point Coordinate in degrees
     * @return U and V component of wind
     * @throws IndexOutOfBoundsException
     */
    suspend fun getWind(height: Int, point: Pair<Double, Double>): Pair<Double, Double>

    /**
     * Get temperature for given height and coordinate
     *
     * @param height Height in meters
     * @param point Coordinate (lat, lon)
     * @return Temperature in kelvin
     * @throws IndexOutOfBoundsException
     */
    suspend fun getTemperature(height: Int, point: Pair<Double, Double>): Double
}

class WeatherRepositoryImpl(
    private val gribRepository: GribRepository,
    private val locationForecastRepository: LocationForecastRepository
) : WeatherRepository {

    /**
     * Calculates height for an isobaric layer using the barometric formula
     *
     * @param p0 Pressure at sea level
     * @param p Pressure for isobaric layer
     * @param t0 Temperature at sea level in Kelvin
     * @param t Temperature for isobaric layer
     * @return Height in meters for isobaric layer
     */
    private fun calculateHeight(p0: Double, p: Double, t0: Double, t: Double): Int {
        return ((R / g) * ((t0 + t) / 2) * ln(p0 / p)).toInt()
    }

    /**
     * Retrieves data from point
     *
     * @param data List to retrieve data from
     * @param point Point to retrieve data from
     * @return Data at point in list
     */
    private fun getDataFromPoint(data: List<Double>, point: Pair<Double, Double>): Double =
        data[calculateIndex(point)]

    /**
     * Calculates index in data array for coordinate
     *
     * @param point Point to calculate index for
     * @return Index
     * @throws IndexOutOfBoundsException Thrown if point outside data area
     */
    private fun calculateIndex(point: Pair<Double, Double>): Int {
        if (point.first > 64.25 || point.first < 55.35) {
            throw IndexOutOfBoundsException("Point is outside data area")
        }

        if (point.second > 14.51 || point.second < -1.45) {
            throw IndexOutOfBoundsException("Point is outside data area")
        }

        val lat = 64.25
        val lon = -1.45
        val index = ((lat - point.first) * 160 * 10 + (point.second - lon) * 10).toInt()
        Log.d("index", "Point: $point, index: $index")
        if (index < 0 || index > 14399) {
            throw IndexOutOfBoundsException("Point is outside data area")
        }
        return index
    }

    /**
     * Finds nearest isobaric layer
     *
     * @param height Height in meters
     * @param point Coordinate (lat, lon)
     * @param data Data map from pressure -> U wind, V wind, temperature in kelvin
     * @return Pressure of isobaric layer in Pascal
     */
    private suspend fun calculateIsobaricLayer(
        height: Int,
        point: Pair<Double, Double>,
        data: Map<Double, Triple<List<Double>, List<Double>, List<Double>>>
    ): Double = withContext(Dispatchers.Default) {

        val getAirPressure = async(Dispatchers.IO) {
            return@async locationForecastRepository.getAirPressureSeaLevelNow(
                point.first, point.second
            )
        }
        val airPressure = getAirPressure.await()

        val getTemp = async(Dispatchers.IO) {
            return@async locationForecastRepository.getAirTemperatureSeaLevelNow(
                point.first,
                point.second
            )
        }
        val seaTemp = getTemp.await()

        var pressure = 0.0
        var nearest = 0
        data.forEach {
            val temp: Double = getDataFromPoint(it.value.third, point)
            val h = calculateHeight(airPressure, it.key, seaTemp, temp)
            println("Pressure: ${it.key}\nHeight: $h")
            if (nearest == 0 || abs(height - nearest) > abs(height - h)) {
                nearest = h
                pressure = it.key
            }
        }
        println(pressure)
        return@withContext pressure
    }

    /**
     * Get wind data (U and V component) given height and coordinate
     *
     * @param height Height in meters
     * @param point Coordinate in degrees
     * @return U and V component of wind
     * @throws IndexOutOfBoundsException
     */
    override suspend fun getWind(height: Int, point: Pair<Double, Double>): Pair<Double, Double> =
        coroutineScope {

            // Cancel call early if outside range
            try {
                calculateIndex(point)
            } catch (e: IndexOutOfBoundsException) {
                throw e
            }

            val getData = async(Dispatchers.IO) {
                try {
                    return@async gribRepository.fetchGribData()
                } catch (e: Exception) {
                    return@async emptyMap()
                }
            }
            val data = getData.await()

            val getPressure = async(Dispatchers.Default) {
                return@async calculateIsobaricLayer(
                    height, point, data
                )
            }
            val pressure = getPressure.await()

            return@coroutineScope Pair<Double, Double>(
                getDataFromPoint(data[pressure]!!.first, point),
                getDataFromPoint(data[pressure]!!.second, point)
            )
        }

    /**
     * Get temperature for given height and coordinate
     *
     * @param height Height in meters
     * @param point Coordinate (lat, lon)
     * @return Temperature in kelvin
     * @throws IndexOutOfBoundsException
     */
    override suspend fun getTemperature(height: Int, point: Pair<Double, Double>): Double =
        coroutineScope {

            // Cancel call early if outside range
            try {
                calculateIndex(point)
            } catch (e: IndexOutOfBoundsException) {
                throw e
            }

            val getData = async(Dispatchers.IO) {
                try {
                    return@async gribRepository.fetchGribData()
                } catch (e: Exception) {
                    return@async emptyMap()
                }
            }
            val data = getData.await()

            val getPressure = async(Dispatchers.Default) {
                return@async calculateIsobaricLayer(
                    height, point, data
                )
            }
            val pressure = getPressure.await()

            return@coroutineScope getDataFromPoint(data[pressure]!!.third, point)
        }
}

class MockWeatherRepository : WeatherRepository {
    override suspend fun getWind(height: Int, point: Pair<Double, Double>): Pair<Double, Double> {
        return Pair(10.0, 5.0)
    }

    override suspend fun getTemperature(height: Int, point: Pair<Double, Double>): Double {
        return 288.0
    }
}
