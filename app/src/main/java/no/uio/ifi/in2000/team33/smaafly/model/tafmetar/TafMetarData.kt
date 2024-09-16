package no.uio.ifi.in2000.team33.smaafly.model.tafmetar

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class TafMetarData(
    val taf: String,
    val metar: String,
    val icao: String,
    val metarTime: String,
    val tafBegin: String,
    val tafEnd: String
)

/**
 * @JsonProperty declares the name spaces metno and gml for the xml file
 * @class AviationProducts is the root element of the file
 */
data class AviationProducts(
    @JsonProperty("metno:terminalAerodromeForecast")
    val terminalAerodromeForecast: TerminalAerodromeForecast,

    @JsonProperty("metno:meteorologicalAerodromeReport")
    val meteorologicalAerodromeReport: MeteorologicalAerodromeReport
)

/**
 * @JsonCreator constructor declares the children and subchildren of AviationProducts
 */
data class MeteorologicalAerodromeReport @JsonCreator constructor(
    @JsonProperty("metno:validTime")
    val validTime: ValidTime,
    @JsonProperty("metno:metarText")
    val metarText: String
)

data class TerminalAerodromeForecast @JsonCreator constructor(
    @JsonProperty("metno:icaoAirportIdentifier")
    val icaoAirportIdentifier: String,
    @JsonProperty("metno:validPeriod")
    val validPeriod: ValidPeriod,
    @JsonProperty("metno:tafText")
    val tafText: String
)

data class ValidTime @JsonCreator constructor(
    @JsonProperty("gml:TimeInstant")
    val timeInstant: TimeInstant
)

data class TimeInstant @JsonCreator constructor(
    @JsonProperty("gml:timePosition")
    val timePosition: String
)

data class ValidPeriod @JsonCreator constructor(
    @JsonProperty("gml:beginPosition")
    val beginPosition: String,
    @JsonProperty("gml:endPosition")
    val endPosition: String
)

