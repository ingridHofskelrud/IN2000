package no.uio.ifi.in2000.team33.smaafly.ui.map

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.mapbox.geojson.Point
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState

/**
 * FAB for centering location
 *
 * @param mapViewportState ViewportState to update
 */
@OptIn(MapboxExperimental::class)
@Composable
fun CenterMapFAB(mapViewportState: MapViewportState) {
    FloatingActionButton(
        onClick = {
            mapViewportState.flyTo(
                cameraOptions {
                    zoom(5.0)
                    center(Point.fromLngLat(8.2, 60.53))
                    pitch(0.0)
                    bearing(0.0)
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        shape = CircleShape,
    ) {
        Icon(Icons.Filled.LocationOn, "Center map")
    }
}