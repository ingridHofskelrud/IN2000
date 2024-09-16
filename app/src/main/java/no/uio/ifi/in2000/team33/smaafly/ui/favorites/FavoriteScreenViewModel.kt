package no.uio.ifi.in2000.team33.smaafly.ui.favorites

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team33.smaafly.data.room.LocationRepository
import no.uio.ifi.in2000.team33.smaafly.domain.getairports.GetAirportsUseCase
import no.uio.ifi.in2000.team33.smaafly.model.airports.Airport
import no.uio.ifi.in2000.team33.smaafly.model.room.Location
import javax.inject.Inject

sealed interface FavoriteUIState {
    data class Success(
        val airports: List<Airport> = emptyList(),
        val selectedLocation: Location? = null,
        val locations: Flow<List<Location>> = flowOf(emptyList()),
        val searchedLocation: Location? = null
    ) : FavoriteUIState

    data object Error : FavoriteUIState
}

/**
 * ViewModel for FavoriteScreen
 *
 * @param getAirportsUseCase UseCase for getting list of airports
 * @param locationRepository Repository for interacting with database
 */
@HiltViewModel
class FavoriteScreenViewModel @Inject constructor(
    private val getAirportsUseCase: GetAirportsUseCase,
    private val locationRepository: LocationRepository
) : ViewModel() {
    private val _uiState: MutableStateFlow<FavoriteUIState> =
        MutableStateFlow(FavoriteUIState.Success())
    val uiState = _uiState.asStateFlow()

    init {
        getLocations()
    }

    /**
     * Insert location into favorites database
     *
     * @param location Location to be inserted into database
     */
    fun insertLocation(location: Location) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                locationRepository.insertLocation(location)
            } catch (e: Exception) {
                Log.e("favorites", "Could not insert location")
            }
        }
    }

    /**
     * Delete location from favorites
     *
     * @param location Location to delete
     */
    fun deleteLocation(location: Location) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                locationRepository.deleteLocation(location)
            } catch (_: Exception) {
            }
        }
    }

    /**
     * Get favorite locations from local database
     * Stored as Flow in [FavoriteUIState.Success]
     */
    private fun getLocations() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = try {
                val locations = locationRepository.getAllLocations()
                when (_uiState.value) {
                    is FavoriteUIState.Success -> {
                        (_uiState.value as FavoriteUIState.Success).copy(
                            locations = locations
                        )
                    }

                    is FavoriteUIState.Error -> {
                        FavoriteUIState.Success(locations = locations)
                    }
                }
            } catch (e: Exception) {
                FavoriteUIState.Error
            }
        }
    }

    /**
     * Get airports for search.
     *
     * Updates airports in state.
     *
     * @param context Context needed for opening file from assets
     */
    fun getAirports(context: Context) {
        viewModelScope.launch(Dispatchers.Default) {
            _uiState.value = try {
                val airports = getAirportsUseCase(context)
                when (_uiState.value) {
                    is FavoriteUIState.Success -> {
                        (_uiState.value as FavoriteUIState.Success).copy(
                            airports = airports
                        )
                    }

                    is FavoriteUIState.Error -> {
                        FavoriteUIState.Success(airports = airports)
                    }
                }
            } catch (e: RuntimeException) {
                FavoriteUIState.Error
            }
        }
    }

    /**
     * Select airport to be added to favorites
     * Updates UI State
     *
     * @param airport Airport to select
     */
    fun selectAirport(airport: Airport?) {
        val location = if (airport == null) null else Location(
            airport.name,
            airport.lat,
            airport.long,
            airport.elevation,
            airport.icao
        )

        _uiState.value = when (_uiState.value) {
            is FavoriteUIState.Success -> {
                (_uiState.value as FavoriteUIState.Success).copy(selectedLocation = location)
            }

            is FavoriteUIState.Error -> {
                FavoriteUIState.Success(selectedLocation = location)
            }
        }
    }
}
