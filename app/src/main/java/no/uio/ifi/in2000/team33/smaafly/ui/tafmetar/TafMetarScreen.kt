package no.uio.ifi.in2000.team33.smaafly.ui.tafmetar

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import no.uio.ifi.in2000.team33.smaafly.model.airports.Airport
import no.uio.ifi.in2000.team33.smaafly.model.tafmetar.TafMetarData
import no.uio.ifi.in2000.team33.smaafly.ui.map.SearchBar
import no.uio.ifi.in2000.team33.smaafly.ui.toolbar.BottomBar
import no.uio.ifi.in2000.team33.smaafly.ui.toolbar.TopBar

/**
 * @param icao the icao value that is retrieved from the airport for which one wants data
 */
@Composable
fun TafMetarScreen(
    navController: NavController,
    icao: String? = null,
    tafMetarViewModel: TafMetarViewModel = hiltViewModel()
) {

    tafMetarViewModel.getAirports(LocalContext.current)
    val tafMetarUIState by tafMetarViewModel.uiState.collectAsState()

    when (val uiState = tafMetarUIState) {
        is TafMetarUIState.Success -> {

            val airports = uiState.airports
            var selectedAirport: Airport? by remember { mutableStateOf(null) }
            val symbolCode = uiState.symbolCode
            val tafMetar = uiState.tafMetar

            /**
             * If there is no selected airport, data for the icao chosen with navigation is loaded
             * Checking that icao is not the null argument {icao} that is being passed along when navigating with bottom bar
             */

            if (selectedAirport == null && icao != "{icao}") {
                tafMetarViewModel.loadTafMetar(icao!!)
                //Looping through airports to find the matching symbol code, since airport icao and navigation icao are two separate parameters
                airports.forEach {
                    if (it.icao == icao) {
                        tafMetarViewModel.getSymbolCode(
                            it.lat,
                            it.long
                        )
                    }
                }
                Log.d("Icao", "Icao: $icao")
            }

            /***
             * If there is a selectedAirport, load data f
             */

            if (selectedAirport != null) {
                selectedAirport!!.icao?.let { tafMetarViewModel.loadTafMetar(it) }
                tafMetarViewModel.getSymbolCode(
                    selectedAirport!!.lat,
                    selectedAirport!!.long
                )
            }
            Scaffold(
                topBar = {
                    TopBar(
                        navController = navController,
                        screenName = "Taf/Metar"
                    )
                },
                bottomBar = { BottomBar(navController = navController) }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.size(32.dp))
                    SearchBar(
                        airports = airports,
                        onClick = { query ->
                            selectedAirport = (airports.firstOrNull { airport ->
                                airport.name.lowercase() == query.lowercase()
                                    || airport.icao!!.lowercase() == query.lowercase()
                            })
                        },
                        //Assigning airport to selectedAirport by clicking search bar
                        selectAirport = { airport ->
                            selectedAirport = airport
                        }
                    )
                    TafMetarGrid(
                        tafMetar = tafMetar,
                        symbolCode = symbolCode
                    )
                }
            }
        }

        TafMetarUIState.Error -> {
            Scaffold(
                topBar = {
                    TopBar(
                        navController = navController,
                        screenName = "TAF/METAR"
                    )
                },
                bottomBar = { BottomBar(navController = navController) }
            ) {
                Column(modifier = Modifier.padding(it)) {
                    Text("Error")
                }
            }
        }
    }
}

@Composable
fun TafMetarGrid(
    tafMetar: List<TafMetarData>,
    symbolCode: String?
) {

    if (tafMetar.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Ingen Taf/Metar-data funnet.")
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            items(tafMetar) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            it.icao,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.size(5.dp))
                    //Fetching symbol code
                    Column {
                        if (symbolCode != null) {
                            val context = LocalContext.current
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
                            )
                        }
                    }
                }
                Column {
                    Spacer(modifier = Modifier.size(24.dp))
                    TafMetarCard(tafMetar = it, value = "taf")
                    Spacer(modifier = Modifier.size(8.dp))
                    TafMetarCard(tafMetar = it, value = "metar")
                }
            }
        }
    }
}

@Composable
fun TafMetarCard(
    tafMetar: TafMetarData,
    value: String
) {
    Card(
        modifier = Modifier
            .padding(6.dp),
        shape = RoundedCornerShape(25.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(1.dp, Color.Black),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(
                    top = 12.dp,
                    bottom = 12.dp,
                    start = 12.dp,
                    end = 12.dp
                )
        ) {
            //Assigning Taf and Metar data to the cards
            if (value == "taf") {
                Text(
                    "TAF",
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = TextDecoration.Underline
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text("Gyldig fra ${tafMetar.tafBegin}")
                Text("Gyldig til ${tafMetar.tafEnd}")
                Text(tafMetar.taf)
            } else if (value == "metar") {
                Text(
                    "METAR",
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = TextDecoration.Underline
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text("Gyldig for ${tafMetar.metarTime}")
                Text(tafMetar.metar)
            }
        }
    }
}