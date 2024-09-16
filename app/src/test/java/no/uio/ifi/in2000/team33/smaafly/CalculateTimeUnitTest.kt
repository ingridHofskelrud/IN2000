package no.uio.ifi.in2000.team33.smaafly

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import no.uio.ifi.in2000.team33.smaafly.data.weather.MockWeatherRepository
import no.uio.ifi.in2000.team33.smaafly.domain.finddistance.FindDistanceUseCase
import no.uio.ifi.in2000.team33.smaafly.domain.findtime.FindTimeUseCase
import org.junit.Assert.assertEquals
import org.junit.Test

class CalculateTimeUnitTest {
    private val findDistanceUseCase = FindDistanceUseCase()
    private val weatherRepository = MockWeatherRepository()
    private val findTimeUseCase = FindTimeUseCase(findDistanceUseCase, weatherRepository)
    private val speed = 100 * 1.852

    @Test
    fun distZero_isCorrect() {
        val oslo = Pair(59.91, 10.75)
        val route = listOf(oslo, oslo)

        runBlocking {
            launch {
                val result = findTimeUseCase(route)
                val expected = 0.0

                assertEquals(expected, result, 0.01)
            }
        }
    }

    @Test
    fun travelNorth_isCorrect() {
        val p1 = Pair(60.0, 10.0)
        val p2 = Pair(70.0, 10.0)
        val route = listOf(p1, p2)
        val distance = findDistanceUseCase(p1, p2)
        val groundSpeed = speed + (5 * 3.6)
        println("Distance: $distance")
        println("Expected speed: $groundSpeed")
        val expected = distance / groundSpeed

        runBlocking {
            launch {
                val result = findTimeUseCase(route)
                assertEquals(expected, result, 0.01)
            }
        }

    }

    @Test
    fun travelSouth_isCorrect() {
        val p1 = Pair(60.0, 10.0)
        val p2 = Pair(50.0, 10.0)
        val route = listOf(p1, p2)
        val distance = findDistanceUseCase(p1, p2)
        val groundSpeed = speed + (-5 * 3.6)
        println("Distance: $distance")
        println("Expected Speed: $groundSpeed")
        val expected = distance / groundSpeed

        runBlocking {
            launch {
                val result = findTimeUseCase(route)
                assertEquals(expected, result, 0.01)
            }
        }

    }

    @Test
    fun travelEast_isCorrect() {
        val p1 = Pair(60.0, 10.0)
        val p2 = Pair(60.0, 20.0)
        val route = listOf(p1, p2)
        val distance = findDistanceUseCase(p1, p2)
        val groundSpeed = speed + (10 * 3.6)
        println("Distance: $distance")
        println("Expected Speed: $groundSpeed")
        val expected = distance / groundSpeed

        runBlocking {
            launch {
                val result = findTimeUseCase(route)
                assertEquals(expected, result, 0.01)
            }
        }

    }

    @Test
    fun travelWest_isCorrect() {
        val p1 = Pair(60.0, 10.0)
        val p2 = Pair(60.0, 0.0)
        val route = listOf(p1, p2)
        val distance = findDistanceUseCase(p1, p2)
        val groundSpeed = speed + (-10 * 3.6)
        println("Distance: $distance")
        println("Expected Speed: $groundSpeed")
        val expected = distance / groundSpeed

        runBlocking {
            launch {
                val result = findTimeUseCase(route)
                assertEquals(expected, result, 0.01)
            }
        }
    }

    @Test
    fun routeEmptyTime_isCorrect() {
        val route: List<Pair<Double, Double>> = listOf()

        runBlocking {
            launch {
                val result = findTimeUseCase(route)
                val expected = 0.0

                assertEquals(result, expected, 0.01)
            }
        }
    }
}
