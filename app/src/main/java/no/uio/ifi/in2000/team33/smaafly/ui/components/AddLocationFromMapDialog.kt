package no.uio.ifi.in2000.team33.smaafly.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.mapbox.geojson.Point

/**
 * Composable for selecting point from a popup map
 *
 * @param point Selected point
 * @param changePoint Function invoked when clicking the map to change point
 * @param onDismissRequest Function invoked when dismissing dialog
 * @param onConfirmClick Function invoked when confirming selected point
 */
@Composable
fun AddLocationFromMapDialog(
    point: Point?,
    changePoint: (Point) -> Unit,
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit,
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier.height(400.dp),
        ) {
            Box {
                PopUpMap(point = point) {
                    changePoint(it)
                }
                Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                    Button(onClick = { if (point != null) onConfirmClick() }) {
                        Text("Bekreft valgt punkt")
                    }
                }
            }
        }
    }
}