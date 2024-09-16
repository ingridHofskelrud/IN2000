package no.uio.ifi.in2000.team33.smaafly.ui.map

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import no.uio.ifi.in2000.team33.smaafly.model.airports.Airport
import no.uio.ifi.in2000.team33.smaafly.model.room.Location
import no.uio.ifi.in2000.team33.smaafly.ui.toolbar.BottomBar
import no.uio.ifi.in2000.team33.smaafly.ui.toolbar.TopBar
import kotlin.math.roundToInt

/**
 * Main composable for map screen
 *
 * @param navController NavController for navigating screens
 * @param mapScreenViewModel Map screen view model
 */
@OptIn(MapboxExperimental::class)
@Composable
fun MapScreen(
    navController: NavController,
    mapScreenViewModel: MapScreenViewModel = hiltViewModel()
) {
    mapScreenViewModel.getAirports(LocalContext.current)
    mapScreenViewModel.calculateTime()

    val mapUiState by mapScreenViewModel.uiState.collectAsState()

    when (val uiState = mapUiState) {
        is MapUIState.Success -> {
            val sharedRoute = uiState.route.collectAsState()
            val airports = uiState.airports
            val favorites = uiState.favorites.collectAsState(initial = emptyList())
            Log.d("air", "MapScreen $airports")
            val route = sharedRoute.value
            val symbolCode = uiState.symbolCode
            val extraPoints = uiState.extraPoints

            val mapViewportState = rememberMapViewportState { MapViewportState() }
            val selectedAirport = uiState.selectedAirport

            Log.d("airport", "In screen: ${airports.size}")
            Scaffold(
                topBar = { TopBar(navController = navController, screenName = "Kart") },
                bottomBar = { BottomBar(navController = navController) },
                floatingActionButton = { CenterMapFAB(mapViewportState = mapViewportState) }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .background(MaterialTheme.colorScheme.background),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        SearchBar(
                            airports = airports,
                            onClick = { query ->
                                mapScreenViewModel.selectAirport(airports.firstOrNull { airport -> airport.name.lowercase() == query.lowercase() || airport.icao!!.lowercase() == query.lowercase() })
                            },
                            selectAirport = { airport ->
                                mapScreenViewModel.selectAirport(airport)
                            }
                        )
                        Map(
                            airports = airports,
                            favorites = favorites.value,
                            extraPoints = extraPoints,
                            route = route,
                            mapViewportState = mapViewportState,
                            viewModel = mapScreenViewModel,
                            onPointClick = { mapScreenViewModel.selectAirport(it) },
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                        ) {
                            RouteInfo(uiState.distance, uiState.hours, uiState.minutes)
                        }
                        Box(modifier = Modifier.align(Alignment.Center)) {
                            if (selectedAirport != null) {
                                ShowInfo(
                                    chosenAirport = uiState.selectedAirport,
                                    viewModel = mapScreenViewModel,
                                    symbolCode = symbolCode,
                                    navController = navController,
                                    favorites = favorites.value
                                )
                            }
                        }
                    }
                }
            }
        }

        is MapUIState.Error -> {
            Text("Error")
        }

    }
}

/**
 * Small composable for route info at bottom of map screen
 *
 * @param distance Route distance in km
 * @param hours Route time in hours
 * @param minutes Route time in minutes
 */
@Composable
fun RouteInfo(distance: Double, hours: Int, minutes: Int) {
    val visualHours = if (hours < 10) "0$hours" else "$hours"
    val visualMinutes = if (minutes < 10) "0$minutes" else "$minutes"
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(0.65f),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.secondary
        ),
    ) {
        Box(
            modifier = Modifier
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Distanse: ${distance.toInt()}km")
                Text(text = "Tid: ${visualHours}:${visualMinutes}")
            }
        }
    }
}

/**
 * Display a dialog card showing information about the selected airport
 *
 * @param chosenAirport The airport chosen for the information to be displayed.
 * @param viewModel The viewmodel associated the information about airports, route and symbolCode
 */
@Composable
fun ShowInfo(
    chosenAirport: Airport?,
    viewModel: MapScreenViewModel,
    symbolCode: String?,
    navController: NavController,
    favorites: List<Location>
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    if (chosenAirport != null) {
        Card(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .pointerInput(Unit) {
                    detectDragGestures { _, dragAmount ->
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                },
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color.LightGray),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            )
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.padding(top = 5.dp, bottom = 10.dp, start = 20.dp, end = 20.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    chosenAirport.let { airport ->
                        val elevation = if (airport.elevation <= 0) {
                            "Ukjent"
                        } else {
                            "${(airport.elevation * 3.28084).toInt()} ft"
                        }
                        Column {
                            if (airport.name != "") {
                                Text(
                                    "\n${airport.name}:   ${airport.icao}",
                                    style = TextStyle(
                                        fontFamily = FontFamily.SansSerif,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp,
                                    ),
                                    lineHeight = 20.sp,
                                    textDecoration = TextDecoration.Underline

                                )
                            } else {
                                Text(
                                    "\nUkjent",
                                    style = TextStyle(
                                        fontFamily = FontFamily.SansSerif,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp,
                                    ),
                                    lineHeight = 20.sp,
                                    textDecoration = TextDecoration.Underline

                                )
                            }
                            Text(
                                "Breddegrad: ${airport.lat}",
                            )
                            Text(
                                "Lengdegrad: ${airport.long}",
                            )
                            Text(
                                "Høyde: $elevation",
                            )
                            if (airport.icao != null) {
                                OutlinedButton(
                                    onClick = {
                                        navController.navigate("tafmetar/${airport.icao}") {
                                            popUpTo(navController.graph.startDestinationId) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    ),
                                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                                    modifier = Modifier.padding(horizontal = 0.dp, vertical = 2.dp)
                                ) {
                                    Text(text = "TAF/METAR")

                                }
                            }
                            Button(onClick = {
                                viewModel.addStop(airport)
                                viewModel.selectAirport(null)
                            }) {
                                Text("Legg til i rute")
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            IconButton(
                                onClick = { viewModel.selectAirport(null) }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Exit"
                                )
                            }
                            Spacer(modifier = Modifier.size(4.dp))
                            Box(
                                modifier = Modifier
                                    .size(45.dp)
                                    .background(
                                        MaterialTheme.colorScheme.primary,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                            ) {
                                val context = LocalContext.current
                                if (symbolCode != null) {
                                    val imagePainter = rememberAsyncImagePainter(
                                        model = ImageRequest.Builder(context)
                                            .data("file:///android_asset/weather_symbols/$symbolCode.svg")
                                            .decoderFactory(SvgDecoder.Factory())
                                            .build(),
                                    )
                                    Image(
                                        painter = imagePainter,
                                        contentDescription = "Weather image",
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier
                                            .size(38.dp)
                                            .align(Alignment.Center)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        modifier = Modifier
                                            .size(38.dp)
                                            .align(Alignment.Center),
                                        contentDescription = "Kunne ikke laste inn bilde"
                                    )
                                }

                            }
                            Spacer(modifier = Modifier.size(4.dp))
                            val isFavorite = favorites.any { it.name == airport.name }
                            IconButton(onClick = {
                                if (isFavorite) {
                                    viewModel.deleteFavorite(airport)
                                } else {
                                    viewModel.insertFavorite(airport)
                                }
                            }) {
                                Icon(
                                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                    contentDescription = if (isFavorite) "fjern fra favoritter" else "legg til i favoritter",
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                }
            }
        }

    }
}


