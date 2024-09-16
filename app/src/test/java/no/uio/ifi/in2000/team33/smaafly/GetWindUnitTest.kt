package no.uio.ifi.in2000.team33.smaafly

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import no.uio.ifi.in2000.team33.smaafly.data.grib.FakeGribRepository
import no.uio.ifi.in2000.team33.smaafly.data.locationforecast.FakeLocationForecastRepository
import no.uio.ifi.in2000.team33.smaafly.data.weather.WeatherRepositoryImpl
import org.junit.Assert.assertEquals
import org.junit.Test


class GetWindUnitTest {
    @Test
    fun assertWind_isCorrect() {
        val repo = FakeGribRepository()
        val weather = WeatherRepositoryImpl(repo, FakeLocationForecastRepository())
        val height = 1000
        val point = Pair(60.0, 10.0)
        val expected = Pair(6914.0 / 1000, 6914.0 * 2 / 1000)
        runBlocking {
            launch {
                val wind = try {
                    weather.getWind(height, point)
                } catch (e: IndexOutOfBoundsException) {
                    Pair(-1.0, -1.0)
                }
                assertEquals(expected, wind)
            }
        }

    }

    @Test
    fun assertWindFirst_isCorrect() {
        val repo = FakeGribRepository()
        val weather = WeatherRepositoryImpl(repo, FakeLocationForecastRepository())
        val height = 1000
        val point = Pair(64.25, -1.45)
        val expected = Pair(0.0 / 1000, 0.0 * 2 / 1000)
        runBlocking {
            launch {
                val wind = try {
                    weather.getWind(height, point)
                } catch (e: IndexOutOfBoundsException) {
                    Pair(-1.0, -1.0)
                }
                assertEquals(expected, wind)
            }
        }
    }

    @Test
    fun assertWindLast_isCorrect() {
        val repo = FakeGribRepository()
        val weather = WeatherRepositoryImpl(repo, FakeLocationForecastRepository())
        val height = 1000
        val point = Pair(55.35, 14.51)
        val expected = Pair(14399.0 / 1000, 14399.0 * 2 / 1000)
        runBlocking {
            launch {
                val wind = try {
                    weather.getWind(height, point)
                } catch (e: IndexOutOfBoundsException) {
                    Pair(-1.0, -1.0)
                }
                assertEquals(expected, wind)
            }
        }
    }

    @Test
    fun assertWindError_isCorrect() {
        val repo = FakeGribRepository()
        val weather = WeatherRepositoryImpl(repo, FakeLocationForecastRepository())
        val height = 1000
        val point = Pair(0.0, 0.0)
        val expected = Pair(-1.0, -1.0)
        runBlocking {
            launch {
                val wind = try {
                    weather.getWind(height, point)
                } catch (e: IndexOutOfBoundsException) {
                    Pair(-1.0, -1.0)
                }
                assertEquals(expected, wind)
            }
        }
    }

    @Test
    fun assertWindHigh_isCorrect() {
        val repo = FakeGribRepository()
        val weather = WeatherRepositoryImpl(repo, FakeLocationForecastRepository())
        val height = 10000
        val point = Pair(64.25, -1.3)
        val expected = Pair(-1.0 / 1000, -1.0 * 2 / 1000)
        runBlocking {
            launch {
                val wind = try {
                    weather.getWind(height, point)
                } catch (e: IndexOutOfBoundsException) {
                    Pair(-1.0, -1.0)
                }
                assertEquals(expected, wind)
            }
        }
    }

    @Test
    fun assertWindErrorNorth_isCorrect() {
        val weather = WeatherRepositoryImpl(FakeGribRepository(), FakeLocationForecastRepository())
        val height = 1000
        val point = Pair(67.28, 14.40501)
        val expected = Pair(-1.0, -1.0)

        runBlocking {
            launch {
                val wind = try {
                    weather.getWind(height, point)
                } catch (e: IndexOutOfBoundsException) {
                    Pair(-1.0, -1.0)
                }
                assertEquals(expected, wind)
            }
        }
    }
}