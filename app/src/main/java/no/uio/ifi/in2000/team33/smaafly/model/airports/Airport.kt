package no.uio.ifi.in2000.team33.smaafly.model.airports

import kotlinx.serialization.Serializable

/**
 * Data class for airports
 */
@Serializable
data class Airport(
    var name: String,
    val icao: String?,
    val lat: Double,
    val long: Double,
    val elevation: Int = 0,
    var weatherSymbol: String? = "",
)




