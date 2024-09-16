package no.uio.ifi.in2000.team33.smaafly.domain.finddistance

import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


const val RADIUS = 6371

/**
 * Finds distance between 2 points or a list of points.
 *
 * Each point with format (lat, lon)
 */
class FindDistanceUseCase {
    /**
     * Calculate distance between 2 points
     *
     * @param cord1 Point 1 with (lat, lon)
     * @param cord2 Point 2 with (lat, lon)
     * @return Distance in kilometers
     */
    operator fun invoke(cord1: Pair<Double, Double>, cord2: Pair<Double, Double>) =
        calculateDistance(cord1, cord2)

    /**
     * Calculate distance between each pair of 2 points in a list of points
     * @param points List of points with (lat, lon)
     * @return Total distance in kilometers
     */
    operator fun invoke(points: List<Pair<Double, Double>>) = calculateRouteDistance(points)

    /**
     * Calculate distance between 2 points
     *
     * @param cord1 Point 1 with (lat, lon)
     * @param cord2 Point 2 with (lat, lon)
     * @return Distance in kilometers
     */
    private fun calculateDistance(
        cord1: Pair<Double, Double>, cord2: Pair<Double, Double>
    ): Double {
        val latDistance = Math.toRadians(cord1.first - cord2.first)
        val lonDistance = Math.toRadians(cord1.second - cord2.second)
        val angle =
            sin(latDistance / 2) * sin(latDistance / 2) + cos(Math.toRadians(cord1.first)) * cos(
                Math.toRadians(cord2.first)
            ) * sin(lonDistance / 2) * sin(
                lonDistance / 2
            )
        return 2 * RADIUS * asin(sqrt(angle))
    }

    /**
     * Calculate distance between each pair of 2 points in a list of points
     * @param points List of points with (lat, lon)
     * @return Total distance in kilometers
     */
    private fun calculateRouteDistance(points: List<Pair<Double, Double>>): Double =
        points.zipWithNext().sumOf { calculateDistance(it.first, it.second) }

}