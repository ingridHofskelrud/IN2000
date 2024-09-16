package no.uio.ifi.in2000.team33.smaafly.model.sigchart

import kotlinx.serialization.Serializable

@Serializable
data class AvailableSigCharts(
    val params: Params,
    val updated: String,
    val uri: String
)

@Serializable
data class Params(
    val area: String,
    val time: String
)
