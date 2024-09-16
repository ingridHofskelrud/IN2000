package no.uio.ifi.in2000.team33.smaafly

import no.uio.ifi.in2000.team33.smaafly.domain.finddistance.FindDistanceUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class CalculateDistanceUnitTest {
    @Test
    fun distance_isCorrect() {
        val oslo = Pair(59.91, 10.75)
        val bergen = Pair(60.39, 5.32)
        val expected = 305
        val findDistance = FindDistanceUseCase()
        val result = findDistance(oslo, bergen)

        assertTrue(abs(result - expected) < 10)
    }

    @Test
    fun distance0_isCorrect() {
        val oslo = Pair(59.91, 10.75)
        val expected = 0
        val findDistance = FindDistanceUseCase()
        val result = findDistance(oslo, oslo)

        assertTrue(abs(result - expected) < 10)
    }

    @Test
    fun routeDistance_isCorrect() {
        val oslo = Pair(59.91, 10.75)
        val bergen = Pair(60.39, 5.32)
        val route = listOf(oslo, bergen, oslo)
        val expected = 610
        val findDistance = FindDistanceUseCase()

        val result = findDistance(route).toInt()

        assertEquals(result, expected)
    }

    @Test
    fun routeDistanceShort_isCorrect() {
        val oslo = Pair(59.91, 10.75)
        val bergen = Pair(60.39, 5.32)
        val route = listOf(oslo, bergen)
        val expected = 305
        val findDistance = FindDistanceUseCase()

        val result = findDistance(route).toInt()

        assertEquals(result, expected)
    }

    @Test
    fun routeDistanceOneStop_isCorrect() {
        val oslo = Pair(59.91, 10.75)
        val route = listOf(oslo)
        val expected = 0
        val findDistance = FindDistanceUseCase()

        val result = findDistance(route).toInt()

        assertEquals(result, expected)
    }

    @Test
    fun routeDistanceEmpty_isCorrect() {
        val route: List<Pair<Double, Double>> = listOf()
        val expected = 0
        val findDistance = FindDistanceUseCase()

        val result = findDistance(route).toInt()

        assertEquals(result, expected)
    }
}