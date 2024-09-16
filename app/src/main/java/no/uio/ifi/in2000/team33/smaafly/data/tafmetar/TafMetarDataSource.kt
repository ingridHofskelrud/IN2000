package no.uio.ifi.in2000.team33.smaafly.data.tafmetar

import android.util.Log
import com.ctc.wstx.stax.WstxInputFactory
import com.ctc.wstx.stax.WstxOutputFactory
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlFactory
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.path
import no.uio.ifi.in2000.team33.smaafly.model.tafmetar.AviationProducts
import no.uio.ifi.in2000.team33.smaafly.model.tafmetar.TafMetarData
import javax.inject.Named
import javax.xml.stream.XMLInputFactory

/** Datasource for Taf/Metar data
 * @param client fetching from Met API
 */
class TafMetarDataSource(
    @Named("proxy") private val client: HttpClient
) {
    /**
     * Deserializes Taf and Metar data for the current time period
     * @param icaoId is passed through from TafMetarScreen for the airport that is selected there
     * @return a list of strings with information parsed from xml
     */
    suspend fun fetchTafMetar(icaoId: String): TafMetarData {

        try {

            Log.d("TafMetar", "Fetching tafmetar data for $icaoId")

            val response = client.get {
                url {
                    path("weatherapi/tafmetar/1.0/tafmetar.xml")
                    parameter("icao", icaoId)
                }
            }

            /* xmlString converts the response to a string for easier parsing
             * inputFactory configures the xmlMapper to ignore namespaces
             * xmlMapper parses the xml data
             * finalObject is the result of the parsing
             */

            val xmlString: String = response.bodyAsText()

            val inputFactory = WstxInputFactory()
            inputFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false)
            val xmlMapper = XmlMapper(XmlFactory(inputFactory, WstxOutputFactory()))
            xmlMapper.registerKotlinModule()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

            val finalObject = xmlMapper.readValue(xmlString, AviationProducts::class.java)

            return TafMetarData(
                finalObject.terminalAerodromeForecast.tafText,
                finalObject.meteorologicalAerodromeReport.metarText,
                finalObject.terminalAerodromeForecast.icaoAirportIdentifier,
                finalObject.meteorologicalAerodromeReport.validTime.timeInstant.timePosition,
                finalObject.terminalAerodromeForecast.validPeriod.beginPosition,
                finalObject.terminalAerodromeForecast.validPeriod.endPosition
            )
        } catch (e: Exception) {
            Log.e("TafMetar", "Error fetching tafmetar data for $icaoId")
            throw e
        }
    }
}
