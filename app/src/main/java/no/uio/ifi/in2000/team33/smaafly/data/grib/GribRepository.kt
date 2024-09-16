package no.uio.ifi.in2000.team33.smaafly.data.grib

import android.util.Log
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface GribRepository {
    /**
     * Fetch nearest grib data
     *
     * @return Map from pressure of isobaric layer to triple with wind U, wind V and temperature in Kelvin
     */
    suspend fun fetchGribData(): Map<Double, Triple<List<Double>, List<Double>, List<Double>>>
}

class GribRepositoryImpl(private val gribDataSource: GribDataSource) : GribRepository {
    private var data: Map<Double, Triple<List<Double>, List<Double>, List<Double>>> = mapOf()
    private var lastUpdate = ""
    private val dataMutex = Mutex()

    /**
     * Fetch nearest grib data
     *
     * @return Map from pressure of isobaric layer to triple with wind U, wind V, and temperature in Kelvin
     */
    override suspend fun fetchGribData(): Map<Double, Triple<List<Double>, List<Double>, List<Double>>> {
        val updateTime = gribDataSource.getFormattedDateTime(gribDataSource.getCurrentDateTime())
        if (updateTime != lastUpdate) {
            val newData = gribDataSource.fetchGribData()
            dataMutex.withLock {
                data = newData.filter { it.header.surface1Value > 60000.0 }
                    .groupBy { it.header.surface1Value }.map {
                        it.key to Triple(it.value.first { gribItem ->
                            gribItem.header.parameterNumberName.contains(
                                "U"
                            )
                        }.data, it.value.first { gribItem ->
                            gribItem.header.parameterNumberName.contains(
                                "V"
                            )
                        }.data, it.value.first { gribItem ->
                            gribItem.header.parameterNumberName.contains(
                                "T"
                            )
                        }.data
                        )
                    }.toMap()
                Log.d("grib", "Isobaric layers: ${data.size}")
            }
        }
        lastUpdate = updateTime
        return data
    }
}

class FakeGribRepository : GribRepository {
    private var d: Map<Double, Triple<List<Double>, List<Double>, List<Double>>> = mapOf()
    private val mutex = Mutex()

    private val data2 = Triple((0..14399).toList().map { it.toDouble() * -1 / 1000 },
        (0..14399).toList().map { it.toDouble() * -2 / 1000 },
        (0..14399).toList().map { it.toDouble() * -1 / 1000 }
    )

    private val data1 = Triple((0..14399).toList().map { it.toDouble() / 1000 },
        (0..14399).toList().map { it.toDouble() * 2 / 1000 },
        (0..14399).toList().map { it.toDouble() * 3 / 1000 })

    private val map: Map<Double, Triple<List<Double>, List<Double>, List<Double>>> =
        mapOf(85000.0 to data1, 50000.0 to data2)

    override suspend fun fetchGribData(): Map<Double, Triple<List<Double>, List<Double>, List<Double>>> {
        if (d.isEmpty()) {
            mutex.withLock {
                d = map
            }
        }
        return map
    }
}