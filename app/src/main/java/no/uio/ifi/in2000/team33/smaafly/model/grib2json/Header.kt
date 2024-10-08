package no.uio.ifi.in2000.team33.smaafly.model.grib2json

import kotlinx.serialization.Serializable

@Serializable
data class Header(
    val basicAngle: Int,
    val center: Int,
    val centerName: String,
    val discipline: Int,
    val disciplineName: String,
    val dx: Double,
    val dy: Double,
    val forecastTime: Int,
    val genProcessType: Int,
    val genProcessTypeName: String,
    val gribEdition: Int,
    val gribLength: Int,
    val gridDefinitionTemplate: Int,
    val gridDefinitionTemplateName: String,
    val gridUnits: String,
    val la1: Double,
    val la2: Double,
    val lo1: Double,
    val lo2: Double,
    val numberPoints: Int,
    val nx: Int,
    val ny: Int,
    val parameterCategory: Int,
    val parameterCategoryName: String,
    val parameterNumber: Int,
    val parameterNumberName: String,
    val parameterUnit: String,
    val productDefinitionTemplate: Int,
    val productDefinitionTemplateName: String,
    val productStatus: Int,
    val productStatusName: String,
    val productType: Int,
    val productTypeName: String,
    val refTime: String,
    val resolution: Int,
    val scanMode: Int,
    val shape: Int,
    val shapeName: String,
    val significanceOfRT: Int,
    val significanceOfRTName: String,
    val subcenter: Int,
    val surface1Type: Int,
    val surface1TypeName: String,
    val surface1Value: Double,
    val surface2Type: Int,
    val surface2TypeName: String,
    val surface2Value: Double,
    val winds: String
)