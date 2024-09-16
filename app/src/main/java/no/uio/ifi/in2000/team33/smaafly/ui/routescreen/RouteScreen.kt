package no.uio.ifi.in2000.team33.smaafly.ui.routescreen


import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Menu
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
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.mapbox.geojson.Point
import no.uio.ifi.in2000.team33.smaafly.model.airports.Airport
import no.uio.ifi.in2000.team33.smaafly.model.room.Location
import no.uio.ifi.in2000.team33.smaafly.ui.components.AddLocationFromMapDialog
import no.uio.ifi.in2000.team33.smaafly.ui.map.SearchBar
import no.uio.ifi.in2000.team33.smaafly.ui.screens.Screen
import no.uio.ifi.in2000.team33.smaafly.ui.toolbar.BottomBar
import no.uio.ifi.in2000.team33.smaafly.ui.toolbar.TopBar
import sh.calvin.reorderable.ReorderableColumn


/**
 * Screen for showing and modifying route.
 * This is the main screen of the app.
 *
 * @param navController NavController for navigating
 * @param routeViewModel ViewModel containing state of screen and for interacting with data layer
 */
@Composable
fun RouteScreen(navController: NavController, routeViewModel: RouteViewModel = hiltViewModel()) {

    routeViewModel.getAirports(LocalContext.current)
    val routeUIState by routeViewModel.uiState.collectAsState()

    when (val uiState = routeUIState) {
        is RouteUIState.Success -> {
            val route = uiState.route.collectAsState()
            val airports = uiState.airports //list of airports.
            val favorites = uiState.favorites.collectAsState(initial = emptyList())
            val selectedAirport = uiState.selectedAirport
            val symbolMap = uiState.symbolMap
            val airportList = route.value

            var showMapDialog by remember { mutableStateOf(false) }
            var showChooseLocationNameDialog by remember { mutableStateOf(false) }
            var airportToFavorite: Airport? by remember { mutableStateOf(null) }
            var point: Point? by remember { mutableStateOf(null) }

            Scaffold(
                topBar = { TopBar(navController = navController, screenName = "Ruteplanlegger") },
                bottomBar = { BottomBar(navController = navController) }
            ) { paddingValues ->

                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .background(MaterialTheme.colorScheme.background)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            if (favorites.value.isNotEmpty()) {
                                LazyRow(
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .height(30.dp)
                                ) {
                                    items(favorites.value) {
                                        FavoriteCard(location = it, viewModel = routeViewModel)
                                        Spacer(modifier = Modifier.width(10.dp))
                                    }
                                }
                            }

                            SearchBar(
                                airports = airports,
                                onClick = { query ->
                                    routeViewModel.selectAirport(airports.firstOrNull { airport -> airport.name.lowercase() == query.lowercase() || airport.icao!!.lowercase() == query.lowercase() })
                                },
                                selectAirport = { airport ->
                                    routeViewModel.selectAirport(airport)
                                }
                            )

                            OutlinedButton(
                                onClick = { showMapDialog = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            ) {
                                Text("Velg fra kart")
                            }
                            if (selectedAirport != null && airportList.isEmpty()) {
                                Log.d(
                                    "RouteScreen",
                                    "Adding airport: ${selectedAirport.name} to empty list"
                                )
                                routeViewModel.addAirportToList(selectedAirport)
                                routeViewModel.selectAirport(null)
                                Log.d(
                                    "RouteScreen",
                                    "Airport added. Total count: ${airportList.size}"
                                )
                            } else if (selectedAirport != null && selectedAirport != airportList[airportList.lastIndex]) {
                                Log.d(
                                    "RouteScreen",
                                    "Adding airport: ${selectedAirport.name}, , vær kode = ${symbolMap[selectedAirport]}, aiportlist last element is ${airportList[airportList.lastIndex]}"
                                )
                                routeViewModel.addAirportToList(selectedAirport)
                                routeViewModel.selectAirport(null)
                                Log.d(
                                    "RouteScreen",
                                    "Airport added. Total count: ${airportList.size}, vær kode = ${symbolMap[selectedAirport]}"
                                )
                            } else {
                                Log.d("RouteScreen", "Airport $selectedAirport ble ikke lagt til")
                                routeViewModel.selectAirport(null)
                            }
                            if (airportList.size > 1) {
                                val scrollState = rememberScrollState()
                                ReorderableColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp)
                                        .verticalScroll(scrollState),
                                    list = airportList,
                                    onSettle = { fromIndex, toIndex ->
                                        val updatedList = airportList.toMutableList().apply {
                                            add(toIndex, removeAt(fromIndex))
                                        }
                                        routeViewModel.updateRoute(updatedList)
                                    },
                                    onMove = {},
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                ) { index, item, _ ->
                                    key(item) {
                                        val interactionSource =
                                            remember { MutableInteractionSource() }
                                        Log.d(
                                            "RouteScreen",
                                            "line 93, interactionsours = $interactionSource"
                                        )

                                        StopCard(
                                            airport = item,
                                            index = index,
                                            favorites = favorites.value,
                                            symbolMap = symbolMap,
                                            changeAirport = { airportToFavorite = it },
                                            changeShowDialog = {
                                                showChooseLocationNameDialog = true
                                            },
                                            interactionSource = interactionSource,
                                            navController = navController,
                                            viewModel = routeViewModel,
                                            draggableModifier = Modifier
                                                .draggableHandle(
                                                    interactionSource = interactionSource,
                                                )
                                                .padding(4.dp),
                                        )
                                    }
                                }
                                Log.d("RouteScreen", "listens rekkefølge:")
                                airportList.forEach { element ->
                                    Log.d("RouteScreen", element.name)
                                }

                            } else {
                                if (airportList.size == 1) {
                                    Log.d(
                                        "RouteScreen",
                                        "Airport Total count: ${airportList.size}, går fir else if == 1"
                                    )
                                    StopCard(
                                        airport = airportList[0],
                                        index = 0,
                                        favorites = favorites.value,
                                        symbolMap = symbolMap,
                                        changeAirport = { airportToFavorite = it },
                                        changeShowDialog = { showChooseLocationNameDialog = true },
                                        interactionSource = null,
                                        navController = navController,
                                        viewModel = routeViewModel,
                                        draggableModifier = Modifier
                                    )

                                } else {
                                    Card(
                                        modifier = Modifier
                                            .padding(6.dp)
                                            .height(25.dp)
                                            .width(200.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                        )

                                    ) { Text(text = "  Legg til hvor du vil fly!") }
                                }

                            }
                        }

                        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                            OutlinedButton(
                                onClick = {
                                    navController.navigate(Screen.MapScreen.route) {
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
                                )
                            ) {
                                Text(text = "Se ruten i kart")
                            }
                        }
                    }
                }

                if (showMapDialog) {
                    AddLocationFromMapDialog(
                        point = point,
                        changePoint = { point = it },
                        onDismissRequest = {
                            showMapDialog = false
                        }) {
                        val airport = Airport(
                            name = "",
                            icao = null,
                            lat = point!!.latitude(),
                            long = point!!.longitude(),
                            elevation = -1,
                        )

                        routeViewModel.addAirportToList(airport)
                        showMapDialog = false
                        point = null
                    }
                }

                if (showChooseLocationNameDialog) {
                    ChooseLocationNameDialog(
                        onDismissRequest = { showChooseLocationNameDialog = false },
                        favorites = favorites.value,
                        airports = airportList,
                        airport = airportToFavorite,
                        viewModel = routeViewModel,
                    ) {
                        airportToFavorite = null
                        showChooseLocationNameDialog = false
                    }
                }
            }
        }

        is RouteUIState.Error ->
            Text(text = "woops \n Something wen´t wrong!")

    }
}

/**
 * Dialog for selecting name when saving location as favorite
 * Only needed if the name of the location is currently empty
 * Name has to be given as it is used as primary key
 *
 * @param onDismissRequest Function invoked when dismissing dialog
 * @param favorites List of saved favorites
 * @param airports List of all airports
 * @param airport Airport to save as favorite location
 * @param viewModel ViewModel needed for interacting with database
 * @param onConfirmClick Function invoked when confirming name
 */
@Composable
fun ChooseLocationNameDialog(
    onDismissRequest: () -> Unit,
    favorites: List<Location>,
    airports: List<Airport>,
    airport: Airport?,
    viewModel: RouteViewModel,
    onConfirmClick: () -> Unit,
) {
    var text by remember { mutableStateOf("") }
    var label by remember { mutableStateOf("Velg navn") }
    var labelColor by remember { mutableStateOf(Color.Unspecified) }
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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

                Button(onClick = {
                    val nameIsTaken =
                        favorites.any { it.name == text.trim() } || airports.any { it.name == text.trim() }
                    if (text != "" && !nameIsTaken && airport != null) {
                        airport.name = text.trim()
                        viewModel.insertFavorite(
                            airport
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
                    Text("Bekreft navn")
                }
            }
        }
    }
}

@Composable
fun FavoriteCard(location: Location, viewModel: RouteViewModel) {
    Card(
        onClick = {
            viewModel.addAirportToList(
                Airport(
                    name = location.name,
                    icao = location.icao,
                    lat = location.lat,
                    long = location.lon,
                    elevation = location.height

                )
            )
        },
        shape = RoundedCornerShape(5.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
    ) {
        val text = location.icao ?: location.name
        Row {
            Text(text = text, fontSize = 20.sp)
            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = "fjern fra favoritter",
                tint = Color.Red
            )
        }
    }
}

/**
 * Card for displaying information about each stop along the selected route
 *
 * @param airport Airport to display info for
 * @param favorites List of favorite locations
 * @param changeAirport Function invoked to select airport to choose name for when button is clicked
 * @param changeShowDialog Function enabling [ChooseLocationNameDialog]
 * @param interactionSource Interaction source for reordering stops
 * @param navController NavController for navigating
 * @param viewModel ViewModel for modifying route
 * @param draggableModifier Special modifier with .draggableHandle required for reordering stops, must be passed in because of problems with scope
 */
@Composable
fun StopCard(
    airport: Airport,
    index: Int,
    favorites: List<Location>,
    symbolMap: Map<Airport, String?>,
    changeAirport: (Airport) -> Unit,
    changeShowDialog: () -> Unit,
    interactionSource: MutableInteractionSource?,
    navController: NavController,
    viewModel: RouteViewModel,
    @SuppressLint("ModifierParameter") draggableModifier: Modifier,
) {
    if (interactionSource != null) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(0.dp),
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(5.dp)
                        .weight(0.8f),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start
                ) {
                    val name = if (airport.name == "") "Ukjent" else airport.name
                    val icao =
                        if (airport.icao == null) "" else ": ${airport.icao}"
                    Text(
                        text = "\n$name$icao",
                        style = TextStyle(
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                        ),
                        lineHeight = 20.sp,
                        textDecoration = TextDecoration.Underline
                    )
                    val elevation =
                        if (airport.elevation <= 0) "Ukjent" else "${(airport.elevation * 3.28084).toInt()} ft."
                    Text("Breddegrad: ${airport.lat}")
                    Text("Lengdegrad: ${airport.long}")
                    Text("Høyde: $elevation")
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
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 8.dp
                            ),
                            modifier = Modifier.padding(
                                horizontal = 0.dp,
                                vertical = 2.dp
                            )
                        ) {
                            Text(text = "TAF/METAR")
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(4.dp)
                        .align(Alignment.CenterVertically),
                    contentAlignment = Alignment.Center
                ) {
                    Log.d("findSymbol", "screen ${symbolMap[airport]}")
                    val symbol = symbolMap[airport]
                    if (symbol != null) {
                        val context = LocalContext.current
                        val imagePainter = rememberAsyncImagePainter(
                            model = ImageRequest.Builder(context)
                                .data("file:///android_asset/weather_symbols/${symbol}.svg")
                                .decoderFactory(SvgDecoder.Factory())
                                .build(),
                        )
                        Image(
                            painter = imagePainter,
                            contentDescription = "Weather image",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .align(Alignment.Center)
                        )
                    } else if (airport.weatherSymbol != null) {
                        val context = LocalContext.current
                        val imagePainter = rememberAsyncImagePainter(
                            model = ImageRequest.Builder(context)
                                .data("file:///android_asset/weather_symbols/${airport.weatherSymbol}.svg")
                                .decoderFactory(SvgDecoder.Factory())
                                .build(),
                        )
                        Image(
                            painter = imagePainter,
                            contentDescription = "Weather image",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .align(Alignment.Center)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Search,
                            modifier = Modifier.align(Alignment.Center),
                            contentDescription = "failed to load image"
                        )
                    }

                }
                Spacer(modifier = Modifier.width(15.dp))

                Column(
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier
                        .fillMaxHeight()
                ) {
                    IconButton(
                        onClick = {
                            viewModel.removeAirportFromList(
                                index
                            )
                        },
                        modifier = Modifier
                            .padding(top = 0.dp, end = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Exit"
                        )
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    val isFavorite =
                        favorites.any { it.name == airport.name }
                    IconButton(onClick = {
                        if (isFavorite) {
                            viewModel.deleteFavorite(airport)
                        } else {
                            if (airport.name == "") {
                                changeAirport(airport)
                                changeShowDialog()
                            } else {
                                viewModel.insertFavorite(airport)
                            }
                        }
                    }) {

                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = if (isFavorite) "fjern fra favoritter" else "legg til i favoritter",
                            tint = Color.Red
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    IconButton(
                        modifier = draggableModifier,
                        onClick = {},
                    ) {
                        Icon(
                            Icons.Rounded.Menu,
                            contentDescription = "Reorder"
                        )
                    }
                }
            }
        }
    } else {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(10.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            ),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(0.dp),
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(5.dp)
                        .weight(0.8f),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start
                ) {
                    val name =
                        if (airport.name == "") "Ukjent" else airport.name
                    val icao =
                        if (airport.icao == null) "" else ": ${airport.icao}"
                    Text(
                        "\n$name$icao",
                        style = TextStyle(
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                        ),
                        lineHeight = 20.sp,
                        textDecoration = TextDecoration.Underline
                    )
                    val elevation =
                        if (airport.elevation <= 0) "Ukjent" else "${airport.elevation} ft."
                    Text("Breddegrad: ${airport.lat}")
                    Text("Lengdegrad: ${airport.long}")
                    Text("Høyde: $elevation")
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
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 8.dp
                            ),
                            modifier = Modifier.padding(
                                horizontal = 0.dp,
                                vertical = 2.dp
                            )
                        ) {
                            Text(text = "TAF/METAR")
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(4.dp)
                        .align(Alignment.CenterVertically),
                    contentAlignment = Alignment.Center
                ) {
                    val symbol = symbolMap[airport]
                    Log.d("findSymbol", "screen $symbol")
                    if (symbol != null) {
                        val context = LocalContext.current
                        val imagePainter = rememberAsyncImagePainter(
                            model = ImageRequest.Builder(context)
                                .data("file:///android_asset/weather_symbols/${symbol}.svg")
                                .decoderFactory(SvgDecoder.Factory())
                                .build(),
                        )
                        Image(
                            painter = imagePainter,
                            contentDescription = "Weather image",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .align(Alignment.Center)
                        )
                    } else if (airport.weatherSymbol != null) {
                        val context = LocalContext.current
                        val imagePainter = rememberAsyncImagePainter(
                            model = ImageRequest.Builder(context)
                                .data("file:///android_asset/weather_symbols/${airport.weatherSymbol}.svg")
                                .decoderFactory(SvgDecoder.Factory())
                                .build(),
                        )
                        Image(
                            painter = imagePainter,
                            contentDescription = "Weather image",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .align(Alignment.Center)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Search,
                            modifier = Modifier.align(Alignment.Center),
                            contentDescription = "failed to load image"
                        )
                    }

                }
                Spacer(modifier = Modifier.width(15.dp))

                Column(
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier
                        .fillMaxHeight()
                ) {
                    IconButton(
                        onClick = {
                            viewModel.removeAirportFromList(
                                index
                            )
                        },
                        modifier = Modifier
                            .padding(top = 0.dp, end = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Exit"
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    val isFavorite =
                        favorites.any { it.name == airport.name }
                    IconButton(onClick = {
                        if (isFavorite) {
                            viewModel.deleteFavorite(airport)
                        } else {
                            if (airport.name == "") {
                                changeAirport(airport)
                                changeShowDialog()
                            } else {
                                viewModel.insertFavorite(airport)
                            }
                        }
                    }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = if (isFavorite) "fjern fra favoritter" else "legg til i favoritter",
                            tint = Color.Red
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    IconButton(
                        modifier = Modifier
                            .padding(4.dp),
                        onClick = {},
                    ) {
                        Icon(
                            Icons.Rounded.Menu,
                            contentDescription = "Reorder"
                        )
                    }
                }

            }
        }
    }
}