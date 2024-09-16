package no.uio.ifi.in2000.team33.smaafly.ui.favorites

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
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
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mapbox.geojson.Point
import no.uio.ifi.in2000.team33.smaafly.model.airports.Airport
import no.uio.ifi.in2000.team33.smaafly.model.room.Location
import no.uio.ifi.in2000.team33.smaafly.ui.components.AddLocationFromMapDialog
import no.uio.ifi.in2000.team33.smaafly.ui.map.SearchBar
import no.uio.ifi.in2000.team33.smaafly.ui.toolbar.BottomBar
import no.uio.ifi.in2000.team33.smaafly.ui.toolbar.TopBar

/**
 * Screen for favorite locations
 *
 * @param navController Navcontroller for navigation
 * @param favoriteScreenViewModel ViewModel for screen
 */
@Composable
fun FavoriteScreen(
    navController: NavController,
    favoriteScreenViewModel: FavoriteScreenViewModel = hiltViewModel(),
) {
    favoriteScreenViewModel.getAirports(LocalContext.current)

    val favoriteUiState by favoriteScreenViewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopBar(navController = navController, screenName = "Favoritter") },
        bottomBar = { BottomBar(navController = navController) },
    ) { paddingValues ->
        when (val uiState = favoriteUiState) {
            is FavoriteUIState.Success -> {
                val airports = uiState.airports
                val favorites = uiState.locations.collectAsState(initial = emptyList())
                val selectedLocation = uiState.selectedLocation

                FavoriteContent(
                    viewModel = favoriteScreenViewModel,
                    airports = airports,
                    favorites = favorites.value,
                    selectedLocation = selectedLocation,
                    paddingValues = paddingValues
                )
            }

            is FavoriteUIState.Error -> {

            }
        }
    }

}

/**
 * Content inside of scaffold for FavoriteScreen
 *
 * @param viewModel ViewModel for calling functions
 * @param airports List of airports for search bar
 * @param favorites List of favorite locations
 * @param selectedLocation Selected location when adding new favorite
 */
@Composable
fun FavoriteContent(
    viewModel: FavoriteScreenViewModel,
    airports: List<Airport>,
    favorites: List<Location>,
    selectedLocation: Location?,
    paddingValues: PaddingValues
) {
    var openAlertDialog by remember { mutableStateOf(false) }
    var openMapDialog by remember { mutableStateOf(false) }
    var openChooseNameDialog by remember { mutableStateOf(false) }
    var point: Point? by remember { mutableStateOf(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        OutlinedButton(
            onClick = { openAlertDialog = true },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        ) {
            Text("Legg til favoritt stopp")
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (favorites.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = " Du har ingen favoritter")
            }
        } else {
            LazyVerticalGrid(
                modifier = Modifier.padding(16.dp),
                columns = GridCells.Adaptive(minSize = 128.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(favorites) {
                    LocationCard(
                        location = it,
                        showDelete = true
                    ) { viewModel.deleteLocation(it) }
                }
            }
        }
    }

    if (openAlertDialog) {
        AddFavoriteDialog(
            viewModel = viewModel,
            airports = airports,
            selectedLocation = selectedLocation,
            onDismissRequest = { openAlertDialog = false },
            onMapButtonClick = { openMapDialog = true },
        )
    }

    if (openMapDialog) {
        AddLocationFromMapDialog(
            point = point,
            changePoint = { point = it },
            onDismissRequest = { openMapDialog = false },
            onConfirmClick = {
                openChooseNameDialog = true
            }
        )
    }

    if (openChooseNameDialog) {
        ChooseNameDialog(
            onDismissRequest = { openChooseNameDialog = false },
            onConfirmClick = {
                openChooseNameDialog = false
                openMapDialog = false
                openAlertDialog = false
                point = null
            },
            viewModel = viewModel,
            point = point!!,
            favorites = favorites,
            airports = airports
        )
    }
}


/**
 * Dialog for adding new favorite location
 *
 * Can either search for airport, or pick location in map
 *
 * @param viewModel FavoriteScreenViewModel for calling functions
 * @param airports List of airports for search bar
 * @param selectedLocation Selected location for adding favorite
 * @param onDismissRequest Function to be called when dismissing dialog
 */
@Composable
fun AddFavoriteDialog(
    viewModel: FavoriteScreenViewModel,
    airports: List<Airport>,
    selectedLocation: Location?,
    onDismissRequest: () -> Unit,
    onMapButtonClick: () -> Unit,
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SearchBar(
                    airports,
                    onClick = { query ->
                        viewModel.selectAirport(airports.firstOrNull { airport -> airport.name.lowercase() == query.lowercase() || airport.icao!!.lowercase() == query.lowercase() })
                    },
                    selectAirport = { viewModel.selectAirport(it) }
                )
                OutlinedButton(
                    onClick = { onMapButtonClick() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Text("Velg fra kart")
                }
                if (selectedLocation != null) {
                    LocationCard(
                        location = selectedLocation,
                        showDelete = false
                    ) {}
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedButton(
                            onClick = {
                                viewModel.insertLocation(selectedLocation)
                                viewModel.selectAirport(airport = null)
                                onDismissRequest()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        ) {
                            Text("Legg til")
                            Icon(
                                imageVector = Icons.Filled.Favorite,
                                contentDescription = "fjern fra favoritter",
                                tint = Color.Red
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Dialog for choosing name for custom point
 *
 * @param onDismissRequest Function invoked when dismissing dialog
 * @param onConfirmClick Function invoked when confirming selected point
 * @param viewModel Favorite screen viewmodel
 * @param point Selected point
 * @param favorites List of favorite locations
 * @param airports List of airports
 */
@Composable
fun ChooseNameDialog(
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit,
    viewModel: FavoriteScreenViewModel,
    point: Point,
    favorites: List<Location>,
    airports: List<Airport>
) {
    var text by remember { mutableStateOf("") }
    Dialog(
        onDismissRequest = { onDismissRequest() }
    ) {
        Card {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                var label by remember { mutableStateOf("Velg navn") }
                var labelColor by remember { mutableStateOf(Color.Unspecified) }

                TextField(
                    value = text,
                    onValueChange = {
                        text = it
                        label = "Velg navn"
                        labelColor = Color.Unspecified
                    },
                    label = { Text(label, color = labelColor) },
                    singleLine = true
                )

                LocationCard(
                    location = Location(
                        name = text.trim(),
                        lat = point.latitude(),
                        lon = point.longitude(),
                        height = -1,
                        icao = null
                    ),
                    showDelete = false
                ) {}

                Button(onClick = {
                    val nameIsTaken =
                        favorites.any { it.name == text.trim() } || airports.any { it.name == text.trim() }
                    if (text != "" && !nameIsTaken) {
                        viewModel.insertLocation(
                            Location(
                                name = text.trim(),
                                lat = point.latitude(),
                                lon = point.longitude(),
                                height = -1,
                                icao = null
                            )
                        )
                        onConfirmClick()
                    } else {
                        label = if (text == "") {
                            "Navnet kan ikke være tomt"
                        } else {
                            "Navnet er ikke unikt"
                        }
                        labelColor = Color.Red
                    }
                }) {
                    Text("Legg til")
                }
            }

        }

    }
}


/**
 * Card for showing a location
 *
 * @param location Location to be displayed
 * @param showDelete Whether or not to show the delete favorite button
 * @param deleteLocation Function to be invoked to delete location for database
 */
@Composable
fun LocationCard(
    location: Location,
    showDelete: Boolean,
    deleteLocation: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(250.dp)
            .height(180.dp)
            .padding(6.dp),
        shape = RoundedCornerShape(25.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(1.dp, Color.Black),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.size(2.dp))
            val height = if (location.height <= 0) {
                "Ukjent"
            } else {
                "${(location.height * 3.28084).toInt()} ft"
            }
            Text(
                location.name, style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            )
            Text("Lat: ${location.lat}")
            Text("Lon: ${location.lon}")
            Text("Høyde: $height")

            Spacer(modifier = Modifier.size(1.dp))
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .padding(8.dp)
            ) {
                if (showDelete) {
                    IconButton(
                        onClick = deleteLocation,
                        modifier = Modifier.align(Alignment.BottomEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "fjern fra favoritter",
                            tint = Color.Red
                        )
                    }
                }
            }
        }
    }
}