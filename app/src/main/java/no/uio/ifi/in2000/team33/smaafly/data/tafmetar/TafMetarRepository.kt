package no.uio.ifi.in2000.team33.smaafly.data.tafmetar

import no.uio.ifi.in2000.team33.smaafly.model.tafmetar.TafMetarData

/**
 * Continues the deserialization of Taf/Metar data
 */
interface TafMetarRepository {
    suspend fun getTafMetarData(icaoId: String): TafMetarData
}

/**
 * @param tafMetarDataSource gets data from TafMetarDataSource
 * Continues the deserialization of Taf/Metar data
 */
class NetworkTafMetarRepository(
    private val tafMetarDataSource: TafMetarDataSource
) : TafMetarRepository {

    override suspend fun getTafMetarData(icaoId: String): TafMetarData {
        return tafMetarDataSource.fetchTafMetar(icaoId)
    }
}

class FakeTafMetarRepository : TafMetarRepository {

    override suspend fun getTafMetarData(icaoId: String): TafMetarData {
        return TafMetarData(
            "ENGM 021100Z 0212/0312 03008KT CAVOK=",
            "ENGM 020620Z 03005KT CAVOK 09/02 Q1023 NOSIG=",
            "ENGM", "2024-05-02T06:50:00",
            "2024-05-02T12:00:00",
            "2024-05-03T12:00:00"
        )
    }
}

