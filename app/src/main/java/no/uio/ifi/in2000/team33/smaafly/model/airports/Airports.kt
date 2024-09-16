package no.uio.ifi.in2000.team33.smaafly.model.airports

import kotlinx.serialization.Serializable

/**
 * Container class for airports necessary for reading json file
 */
@Serializable
data class Airports(val airports: List<Airport>)
