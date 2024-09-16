package no.uio.ifi.in2000.team33.smaafly.domain.findtime

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import no.uio.ifi.in2000.team33.smaafly.data.weather.WeatherRepository
import no.uio.ifi.in2000.team33.smaafly.domain.finddistance.FindDistanceUseCase
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

const val AVERAGE_SPEED_KMH = 100 * 1.852
const val AVERAGE_HEIGHT_METERS = (5000 / 3.281).toInt()
const val STEP_SIZE_DEGREES = 1

/**
 * Finds travel time between 2 points or a list of points.
 *
 * Each point is a Pair of Double with format (lat, lon)
 */
class FindTimeUseCase(
    private val findDistanceUseCase: FindDistanceUseCase,
    private val weatherRepository: WeatherRepository
) {

    /**
     * Prefetch grib data
     */
    suspend operator fun invoke() = withContext(Dispatchers.IO) {
        weatherRepository.getWind(1000, Pair(60.0, 10.0))
    }

    /**
     * Calculate travel time between 2 points
     *
     * @param cord1 Point 1 with (lat, lon)
     * @param cord2 Point 2 with (lat, lon)
     * @return Time in hours as Double
     */
    suspend operator fun invoke(cord1: Pair<Double, Double>, cord2: Pair<Double, Double>) =
        withContext(Dispatchers.Default) {
            calculateTime(listOf(cord1, cord2))
        }

    /**
     * Calculate travel time between a list of points
     *
     * @param route List of points with (lat, lon)
     * @return Time in hours/h as Double
     */
    suspend operator fun invoke(route: List<Pair<Double, Double>>) =
        withContext(Dispatchers.Default) {
            calculateTime(route)
        }

    /**
     * Calculate travel time between a list of points
     *
     * @param route List of points with (lat, lon)
     * @return Time in hours as Double
     */
    private suspend fun calculateTime(route: List<Pair<Double, Double>>): Double {
        var sum = 0.0

        route.zipWithNext { cord1, cord2 ->
            val deltaLat = (cord2.first - cord1.first)
            val deltaLon = (cord2.second - cord1.second)

            println("Initial flightAngle: ${Math.toDegrees(calculateAngle(cord1, cord2))}")

            val distance = (findDistanceUseCase(cord1, cord2) / 40007.863) * 360
            Log.d("wind", "Distance: $distance")
            val steps = (distance / STEP_SIZE_DEGREES).toInt()

            val latStepSize = deltaLat / steps
            val lonStepSize = deltaLon / steps

            println("Lat step size: $latStepSize, Lon step size: $lonStepSize, num steps: $steps")

            var startLat = cord1.first
            var startLon = cord1.second
            for (i in 0..<steps) {
                val destLat = startLat + latStepSize
                val destLon = startLon + lonStepSize
                sum += calculateTime(Pair(startLat, startLon), Pair(destLat, destLon))
                println("Point: ${Pair(destLat, destLon)}")
                Log.d("points", "${Pair(startLat, startLon)}")
                startLat = destLat
                startLon = destLon
            }

            sum += calculateTime(Pair(startLat, startLon), cord2)
        }

        return sum
    }

    /**
     * Calculate between two points
     *
     * @param cord1 Coordinate 1
     * @param cord2 Coordinate 2
     * @return Time in hours
     */
    private suspend fun calculateTime(
        cord1: Pair<Double, Double>,
        cord2: Pair<Double, Double>
    ): Double {
        val planeAngle = calculateAngle(cord1, cord2)
        val midPoint = Pair((cord1.first + cord2.first) / 2, (cord1.second + cord2.second) / 2)
        val (windU, windV) = try {
            weatherRepository.getWind(AVERAGE_HEIGHT_METERS, midPoint)
        } catch (e: Exception) {
            Pair(0.0, 0.0)
        }
        val windSpeed = sqrt(windU * windU + windV * windV)
        val windAngle = atan2(windU, windV)
        println("Wind angle: ${Math.toDegrees(windAngle)}")

        val angle = abs(planeAngle - windAngle)
        val speed = windSpeed * sin(Math.PI / 2 - angle) * 3.6
        Log.d("wind", "$speed")
        println("Speed: ${AVERAGE_SPEED_KMH + speed}")
        val distance = findDistanceUseCase(cord1, cord2)
        return distance / (AVERAGE_SPEED_KMH + speed)
    }

    /**
     * Calculate angle between two point
     * @param cord1 point 1
     * @param cord2 point 2
     * @return Angle in radians
     */
    private fun calculateAngle(cord1: Pair<Double, Double>, cord2: Pair<Double, Double>): Double {
        val lat1 = Math.toRadians(cord1.first)
        val lon1 = Math.toRadians(cord1.second)
        val lat2 = Math.toRadians(cord2.first)
        val lon2 = Math.toRadians(cord2.second)

        val x = cos(lat2) * sin(lon2 - lon1)
        val y = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(lon2 - lon1)
        val angle = atan2(x, y)
        println("Flight angle: ${Math.toDegrees(angle)}")
        return angle
    }
}

