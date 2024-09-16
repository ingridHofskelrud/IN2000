package no.uio.ifi.in2000.team33.smaafly.ui.map

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapInitOptions
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PolylineAnnotation
import no.uio.ifi.in2000.team33.smaafly.R
import no.uio.ifi.in2000.team33.smaafly.model.airports.Airport
import no.uio.ifi.in2000.team33.smaafly.model.room.Location


/**
 * Main map for map screen
 *
 * @param airports List of airports
 * @param favorites List of favorite locations
 * @param extraPoints List of custom points
 * @param route Selected route
 * @param mapViewportState ViewportState for moving camera
 * @param viewModel Map screen view model
 * @param onPointClick Function invoked when clicking on map
 */
@SuppressLint("DefaultLocale")
@OptIn(MapboxExperimental::class)
@Composable
fun Map(
    airports: List<Airport>,
    favorites: List<Location>,
    extraPoints: List<Airport>,
    route: List<Airport>,
    mapViewportState: MapViewportState,
    viewModel: MapScreenViewModel,
    onPointClick: (Airport) -> Unit,
) {
    MapboxMap(
        Modifier.fillMaxSize(),
        mapInitOptionsFactory = { context ->
            MapInitOptions(
                context = context,
                cameraOptions = CameraOptions.Builder()
                    .center(Point.fromLngLat(8.2, 60.53))
                    .zoom(5.0)
                    .pitch(0.0)
                    .bearing(0.0)
                    .build()
            )
        },
        mapViewportState = mapViewportState,

        onMapLongClickListener = {
            val long = String.format("%.04f", it.longitude()).toDouble()
            val lat = String.format("%.04f", it.latitude()).toDouble()
            viewModel.addExtraPoint(
                Airport(
                    name = "",
                    icao = null,
                    lat = lat,
                    long = long
                )
            )
            true
        },
        onMapClickListener = {
            viewModel.selectAirport(null)
            true
        }
    ) {
        airports.forEach { airport ->
            PointAnnotation(
                point = Point.fromLngLat(airport.long, airport.lat),
                iconImageBitmap = ContextCompat.getDrawable(
                    LocalContext.current,
                    R.drawable.plane
                )!!.toBitmap(),
                iconSize = 0.7,
                onClick = {
                    onPointClick(airport)
                    true
                }
            )
        }

        favorites.forEach { location ->
            if (location.icao == null) {
                PointAnnotation(
                    point = Point.fromLngLat(location.lon, location.lat),
                    iconImageBitmap = ContextCompat.getDrawable(
                        LocalContext.current,
                        R.drawable.is_blue_marker
                    )!!.toBitmap(),
                    iconSize = 0.5,
                    onClick = {
                        onPointClick(
                            Airport(
                                name = location.name,
                                lat = location.lat,
                                long = location.lon,
                                icao = null,
                                elevation = location.height
                            )
                        )
                        true
                    }
                )
            }
        }

        extraPoints.forEach { airport ->
            PointAnnotation(
                point = Point.fromLngLat(airport.long, airport.lat),
                iconImageBitmap = ContextCompat.getDrawable(
                    LocalContext.current,
                    R.drawable.is_blue_marker
                )!!.toBitmap(),
                iconSize = 0.5,
                onClick = {
                    onPointClick(
                        airport
                    )
                    true
                }
            )
        }

        PolylineAnnotation(
            points = route.map { Point.fromLngLat(it.long, it.lat) },
            lineWidth = 2.0,
            lineColorString = "red"
        )
    }
}
