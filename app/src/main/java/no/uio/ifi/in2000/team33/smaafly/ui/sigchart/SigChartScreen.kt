package no.uio.ifi.in2000.team33.smaafly.ui.sigchart

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import no.uio.ifi.in2000.team33.smaafly.ui.toolbar.BottomBar
import no.uio.ifi.in2000.team33.smaafly.ui.toolbar.TopBar

/**
 * Screen for SigCharts
 *
 * @param navController NavController for navigating between screens
 * @param sigChartViewModel ViewModel for SigCharts
 */
@Composable
fun SigChartScreen(
    navController: NavController,
    sigChartViewModel: SigChartViewModel = hiltViewModel()
) {
    val sigChartUIState by sigChartViewModel.uiState.collectAsState()

    when (val uiState = sigChartUIState) {
        is SigChartUIState.Success -> {
            val bitmap = uiState.bitmap
            Scaffold(
                topBar = { TopBar(navController = navController, screenName = "SigChart") },
                bottomBar = { BottomBar(navController = navController) }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                        .background(MaterialTheme.colorScheme.background),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    var scale by remember {
                        mutableFloatStateOf(1f)
                    }
                    var offset by remember {
                        mutableStateOf(Offset.Zero)
                    }
                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxSize()
                            .aspectRatio(595f / 841f)
                    ) {
                        val state =
                            rememberTransformableState { zoomChange, panChange, _ ->
//                  line under = min and max som now set to 1f, 4f so 1-1 in start and max 4x zoom
                                scale = (scale * zoomChange).coerceIn(1f, 4f)
//                  boundWith and height calculates borders so that you dons scroll past the edge of the image.
                                val boundWhit = (scale - 1) * constraints.maxHeight
                                val boundHeight = (scale - 1) * constraints.maxHeight
//                  divided by 2 sins is both sides of current position
                                val maxY = boundHeight / 2
                                val maxX = boundWhit / 2
                                offset = Offset(
                                    x = ((offset.x + scale * panChange.x).coerceIn(-maxX, maxX)),
                                    y = ((offset.y + scale * panChange.y).coerceIn(-maxY, maxY))
                                )
                            }
                        bitmap?.let { it1 ->
                            Image(
                                bitmap = it1.asImageBitmap(),
                                contentDescription = "SigChart",
                                contentScale = ContentScale.FillWidth,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .graphicsLayer {
                                        scaleX = scale
                                        scaleY = scale
                                        translationX = offset.x
                                        translationY = offset.y
                                    }
                                    .transformable(state)
                            )
                        }
                    }
                }
            }
        }

        is SigChartUIState.Error -> {
            SigChartError(navController = navController)
        }
    }

}

/**
 * Error screens if unable to load SigChart
 */
@Composable
fun SigChartError(navController: NavController) {
    Scaffold(
        topBar = { TopBar(navController = navController, screenName = "Sigchart") },
        bottomBar = { BottomBar(navController = navController) }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Kan ikke laste SigChart")
        }
    }
}
