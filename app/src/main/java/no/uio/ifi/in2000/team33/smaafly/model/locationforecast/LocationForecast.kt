package no.uio.ifi.in2000.team33.smaafly.model.locationforecast

import kotlinx.serialization.Serializable

@Serializable
data class GeoJsonForecastTimeseriesDTO(
    val type: String,
    val geometry: GeometryDTO,
    val properties: ForecastPropertiesDTO
)

@Serializable
data class GeometryDTO(
    val type: String,
    val coordinates: List<Double>
)

@Serializable
data class ForecastPropertiesDTO(
    val meta: MetaDTO,
    val timeseries: List<TimeseriesDTO>
)

@Serializable
data class TimeseriesDTO(
    val time: String,
    val data: ForecastForSpecificTimeStepDTO
)

@Serializable
data class ForecastForSpecificTimeStepDTO(
    val instant: ForecastInstantDTO,
    val next_1_hours: PeriodForecastDTO? = null,
    val next_6_hours: PeriodForecastDTO? = null,
    val next_12_hours: PeriodForecastDTO? = null,
)

@Serializable
data class ForecastInstantDTO(
    val details: ForecastInstantDetailsDTO,
)

@Serializable
data class ForecastInstantDetailsDTO(
    val air_pressure_at_sea_level: Double,
    val air_temperature: Double,
    val cloud_area_fraction: Double,
    val relative_humidity: Double,
    val wind_from_direction: Double,
    val wind_speed: Double
)

@Serializable
data class PeriodForecastDTO(
    val details: PeriodForecastDetailsDTO,
    val summary: PeriodForecastSummaryDTO
)

@Serializable
data class PeriodForecastDetailsDTO(
    val precipitation_amount: Double? = null
)

@Serializable
data class PeriodForecastSummaryDTO(
    val symbol_code: String
)

@Serializable
data class MetaDTO(
    val updated_at: String,
    val units: ForecastUnitsDTO
)

@Serializable
data class ForecastUnitsDTO(
    val air_pressure_at_sea_level: String,
    val air_temperature: String,
    val cloud_area_fraction: String,
    val precipitation_amount: String,
    val relative_humidity: String,
    val wind_from_direction: String,
    val wind_speed: String
)

@Serializable
data class Status(
    val last_update: String
)

