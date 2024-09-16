package no.uio.ifi.in2000.team33.smaafly

import io.ktor.client.HttpClient
import no.uio.ifi.in2000.team33.smaafly.data.grib.GribDataSource
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for formatting time for IsobaricGRIB api call
 */
class FormatTimeUnitTest {
    private val gribDataSource = GribDataSource(client = HttpClient())

    @Test
    fun assertTimeMinutes_isCorrect() {
        val date = "2024-04-15T10:30:00Z"
        val expected = "2024-04-15T09:00:00Z"
        val result = gribDataSource.getFormattedDateTime(date)

        assertEquals(expected, result)
    }

    @Test
    fun assertTime3Hours_isCorrect() {
        val date = "2024-04-15T11:59:59Z"
        val expected = "2024-04-15T09:00:00Z"
        val result = gribDataSource.getFormattedDateTime(date)

        assertEquals(expected, result)
    }

    @Test
    fun assertTimeNow_isCorrect() {
        val date = "2024-04-15T12:00:00Z"
        val expected = "2024-04-15T12:00:00Z"
        val result = gribDataSource.getFormattedDateTime(date)

        assertEquals(expected, result)
    }

    @Test
    fun assertTime1Hour_isCorrect() {
        val date = "2024-04-15T13:00:00Z"
        val expected = "2024-04-15T12:00:00Z"
        val result = gribDataSource.getFormattedDateTime(date)

        assertEquals(expected, result)
    }

    @Test
    fun assertTimeHigh_isCorrect() {
        val date = "2024-04-15T23:59:59Z"
        val expected = "2024-04-15T21:00:00Z"
        val result = gribDataSource.getFormattedDateTime(date)

        assertEquals(expected, result)
    }

    @Test
    fun assertTimeLow_isCorrect() {
        val date = "2024-04-15T02:59:59Z"
        val expected = "2024-04-15T00:00:00Z"
        val result = gribDataSource.getFormattedDateTime(date)

        assertEquals(expected, result)
    }
}