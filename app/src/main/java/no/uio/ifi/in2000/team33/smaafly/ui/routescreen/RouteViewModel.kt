package no.uio.ifi.in2000.team33.smaafly.ui.routescreen

import android.content.Context
import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team33.smaafly.data.room.LocationRepository
import no.uio.ifi.in2000.team33.smaafly.domain.findtime.FindTimeUseCase
import no.uio.ifi.in2000.team33.smaafly.domain.getSymbolCode.GetSymbolCodeUseCase
import no.uio.ifi.in2000.team33.smaafly.domain.getairports.GetAirportsUseCase
import no.uio.ifi.in2000.team33.smaafly.model.airports.Airport
import no.uio.ifi.in2000.team33.smaafly.model.room.Location
import no.uio.ifi.in2000.team33.smaafly.ui.components.SharedRoute
import javax.inject.Inject

sealed interface RouteUIState {
    data class Success(
        val airports: List<Airport> = listOf(),
        val favorites: Flow<List<Location>> = flowOf(emptyList()),
        val route: StateFlow<List<Airport>> = MutableStateFlow(
            emptyList()
        ),
        val selectedAirport: Airport? = null,
        val symbolMap: Map<Airport, String?> = emptyMap()
    ) : RouteUIState

    data object Error : RouteUIState
}

/**
 * ViewModel for route screen
 *
 * @param airportsUseCase Use case for getting airports
 * @param getSymbolCodeUseCase Use case for getting weather symbol code
 * @param findTimeUseCase Use case for calculating time of route
 * @param locationRepository Repository for favorite locations
 * @param sharedRoute Shared route between screens
 */
@HiltViewModel
class RouteViewModel @Inject constructor(
    private val airportsUseCase: GetAirportsUseCase,
    private val getSymbolCodeUseCase: GetSymbolCodeUseCase,
    private val findTimeUseCase: FindTimeUseCase,
    private val locationRepository: LocationRepository,
    private val sharedRoute: SharedRoute,
) : ViewModel() {

    private val _uiState: MutableStateFlow<RouteUIState> = MutableStateFlow(
        RouteUIState.Success(
            route = sharedRoute.route
        )
    )
    val uiState = _uiState.asStateFlow()

    private var initialized = false

    init {
        initialize()
        getFavorites()
    }

    /**
     * Fetch grib data in the background for lower latency
     * when selecting route
     */
    @MainThread
    private fun initialize() {
        if (initialized) {
            return
        }
        initialized = true
        viewModelScope.launch(Dispatchers.Default) {
            try {
                findTimeUseCase()
            } catch (_: Exception) {

            }
        }
    }

    /**
     * Update route after rearranging order
     *
     * @param newRoute New route
     */
    fun updateRoute(newRoute: List<Airport>) {
        viewModelScope.launch {
            sharedRoute.updateRoute(newRoute)
        }
    }

    /**
     * Remove stop from route
     *
     * @param index Index of stop to remove
     */
    fun removeAirportFromList(index: Int) {
        viewModelScope.launch {
            sharedRoute.removePointFromRoute(index)
        }
    }

    /**
     * Add stop to route
     *
     * @param airport Stop to add to route
     */
    fun addAirportToList(airport: Airport) {
        viewModelScope.launch {
            sharedRoute.addPointToRoute(airport)
            val currentState = _uiState.value
            if (currentState is RouteUIState.Success) {
                try {
                    val symbol = getSymbolCodeUseCase(airport.lat, airport.long)
                    _uiState.value = (_uiState.value as RouteUIState.Success).copy(
                        symbolMap = (_uiState.value as RouteUIState.Success).symbolMap + mapOf(
                            airport to symbol
                        )
                    )
                    Log.d(
                        "RouteViewModel",
                        "success fetching symbol code for $airport weather is $symbol"
                    )
                } catch (e: Exception) {
                    Log.e("RouteViewModel", "Error fetching symbol code for $airport", e)
                }
            }
        }
    }

    /**
     * Get airports
     *
     * @param context Context required for reading assets
     */
    fun getAirports(context: Context) {
        viewModelScope.launch(Dispatchers.Default) {
            _uiState.value = try {
                val airports = airportsUseCase(context)
                when (_uiState.value) {
                    is RouteUIState.Success -> {
                        (_uiState.value as RouteUIState.Success).copy(
                            airports = airports
                        )
                    }

                    is RouteUIState.Error -> {
                        RouteUIState.Success(airports = airports)
                    }
                }
            } catch (e: RuntimeException) {
                RouteUIState.Error
            }
        }
    }

    /**
     * Select airport
     *
     * @param airport Airport to select
     */
    fun selectAirport(airport: Airport?) {
        _uiState.value = when (_uiState.value) {
            is RouteUIState.Success -> {
                (_uiState.value as RouteUIState.Success).copy(selectedAirport = airport)
            }

            is RouteUIState.Error -> {
                RouteUIState.Success(selectedAirport = airport)
            }
        }
    }

    /**
     * Get all favorites
     *
     * Saved as favorites in [RouteUIState.Success]
     *
     * Is Flow, so only needs to be called once
     */
    private fun getFavorites() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = try {
                val favorites = locationRepository.getAllLocations()
                when (_uiState.value) {
                    is RouteUIState.Success -> {
                        (_uiState.value as RouteUIState.Success).copy(
                            favorites = favorites
                        )
                    }

                    is RouteUIState.Error -> {
                        RouteUIState.Success(favorites = favorites)
                    }
                }
            } catch (_: Exception) {
                RouteUIState.Error
            }
        }
    }

    /**
     * Remove location from favorites
     *
     * @param airport Airport object to removed from favorite locations
     */
    fun deleteFavorite(airport: Airport) {
        viewModelScope.launch(Dispatchers.IO) {
            val location = Location(
                name = airport.name,
                lat = airport.lat,
                lon = airport.long,
                height = airport.elevation,
                icao = airport.icao,
            )

            try {
                locationRepository.deleteLocation(location)
            } catch (_: Exception) {
            }
        }
    }

    /**
     * Save location as favorite
     *
     * @param airport Airport object to be saved as favorite location
     */
    fun insertFavorite(airport: Airport) {
        viewModelScope.launch(Dispatchers.IO) {
            val location = Location(
                name = airport.name,
                lat = airport.lat,
                lon = airport.long,
                height = airport.elevation,
                icao = airport.icao
            )

            try {
                locationRepository.insertLocation(location)
            } catch (_: Exception) {
            }
        }
    }
}