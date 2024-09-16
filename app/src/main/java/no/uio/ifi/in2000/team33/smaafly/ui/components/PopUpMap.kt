package no.uio.ifi.in2000.team33.smaafly.ui.components

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapInitOptions
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import no.uio.ifi.in2000.team33.smaafly.R

/**
 * Pop up map
 *
 * @param point Selected point
 * @param changePoint Function invoked when clicking the map to change point
 */
@SuppressLint("DefaultLocale")
@OptIn(MapboxExperimental::class)
@Composable
fun PopUpMap(
    point: Point?,
    changePoint: (Point) -> Unit,
) {
    MapboxMap(
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
        onMapClickListener = {
            changePoint(
                Point.fromLngLat(
                    String.format("%.04f", it.longitude()).toDouble(),
                    String.format("%.04f", it.latitude()).toDouble()
                )
            ); true
        }
    ) {
        if (point != null) {
            PointAnnotation(
                point = point,
                iconImageBitmap = ContextCompat.getDrawable(
                    LocalContext.current,
                    R.drawable.is_blue_marker
                )!!.toBitmap(),
                iconSize = 0.7
            )
        }
    }
}