package no.uio.ifi.in2000.team33.smaafly.domain.gettafmetar

import no.uio.ifi.in2000.team33.smaafly.data.tafmetar.TafMetarRepository
import no.uio.ifi.in2000.team33.smaafly.model.tafmetar.TafMetarData

/**
 * continues the deserialization of the Taf/Metar data
 * @param tafMetarRepository gets data from TafMetarRepository
 */
class GetTafMetarUseCase(
    private val tafMetarRepository: TafMetarRepository
) {
    suspend fun getTafMetar(icaoId: String): TafMetarData {
        return tafMetarRepository.getTafMetarData(icaoId)
    }
}






