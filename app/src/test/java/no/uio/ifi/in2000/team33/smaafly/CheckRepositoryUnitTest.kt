package no.uio.ifi.in2000.team33.smaafly

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import no.uio.ifi.in2000.team33.smaafly.data.tafmetar.FakeTafMetarRepository
import org.junit.Assert
import org.junit.Test

class CheckRepositoryUnitTest {

    private val tafMetarRepository = FakeTafMetarRepository()

    @Test
    fun parsing_isCorrect() {
        val expected = "ENGM"

        runBlocking {
            launch {

                val result = tafMetarRepository.getTafMetarData(expected)
                println("Expected result: $expected")
                println("Actual result: ${result.icao}")

                Assert.assertEquals(expected, result.icao)
            }
        }
    }
}



