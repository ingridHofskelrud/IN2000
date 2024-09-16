package no.uio.ifi.in2000.team33.smaafly.model.grib2json

import kotlinx.serialization.Serializable

@Serializable
data class IsobaricGRIBItem(
    val data: List<Double>,
    val header: Header
)