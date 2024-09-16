package no.uio.ifi.in2000.team33.smaafly.ui.tafmetar

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team33.smaafly.domain.getSymbolCode.GetSymbolCodeUseCase
import no.uio.ifi.in2000.team33.smaafly.domain.getairports.GetAirportsUseCase
import no.uio.ifi.in2000.team33.smaafly.domain.gettafmetar.GetTafMetarUseCase
import no.uio.ifi.in2000.team33.smaafly.model.airports.Airport
import no.uio.ifi.in2000.team33.smaafly.model.tafmetar.TafMetarData
import javax.inject.Inject

sealed interface TafMetarUIState {
    data class Success(
        val tafMetar: List<TafMetarData> = listOf(),
        val airports: List<Airport> = listOf(),
        val selectedAirport: Airport? = null,
        val symbolCode: String? = null
    ) : TafMetarUIState

    data object Error : TafMetarUIState
}

/**
 * @param getTafMetarUseCase Fetch TafMetarData
 * @param airportsUseCase Fetch airports
 * @param getSymbolCodeUseCase Fetch symbol codes
 */
@HiltViewModel
class TafMetarViewModel @Inject constructor(
    private val getTafMetarUseCase: GetTafMetarUseCase,
    private val airportsUseCase: GetAirportsUseCase,
    private val getSymbolCodeUseCase: GetSymbolCodeUseCase
) : ViewModel() {

    private var oldICAO: String? = null

    private val _uiState: MutableStateFlow<TafMetarUIState> =
        MutableStateFlow(TafMetarUIState.Success(listOf()))
    val uiState = _uiState.asStateFlow()

    /**
     * Get the deserialized Taf/Metar data for the chosen airport and current time
     */
    fun loadTafMetar(icaoId: String) {

        if (icaoId == oldICAO) {
            return
        }
        oldICAO = icaoId

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = try {
                Log.d("TafMetar", "loading tafmetar")
                val tafMetar = listOf(getTafMetarUseCase.getTafMetar(icaoId))
                when (_uiState.value) {
                    is TafMetarUIState.Success -> {
                        (_uiState.value as TafMetarUIState.Success).copy(
                            tafMetar = tafMetar
                        )
                    }

                    is TafMetarUIState.Error -> {
                        TafMetarUIState.Success(tafMetar = tafMetar)
                    }
                }
            } catch (e: Exception) {
                when (_uiState.value) {
                    is TafMetarUIState.Success -> {
                        (_uiState.value as TafMetarUIState.Success).copy(
                            tafMetar = emptyList()
                        )
                    }

                    is TafMetarUIState.Error -> {
                        TafMetarUIState.Success(tafMetar = emptyList())
                    }
                }
            }
        }
    }

    /**
     * Get airports to display on map.
     *
     * Updates airports in state.
     *
     * @param context Context needed for opening file from assets
     */

    fun getAirports(context: Context) {
        viewModelScope.launch(Dispatchers.Default) {
            _uiState.value = try {
                val airports = airportsUseCase(context)
                when (_uiState.value) {
                    is TafMetarUIState.Success -> {
                        (_uiState.value as TafMetarUIState.Success).copy(
                            airports = airports
                        )
                    }

                    is TafMetarUIState.Error -> {
                        TafMetarUIState.Success(airports = airports)
                    }
                }
            } catch (e: RuntimeException) {
                TafMetarUIState.Error
            }
        }
    }

    /**
     * @param airport Airport being selected
     */
    fun selectAirport(airport: Airport?) {
        _uiState.value = when (_uiState.value) {
            is TafMetarUIState.Success -> {
                (_uiState.value as TafMetarUIState.Success).copy(selectedAirport = airport)
            }

            is TafMetarUIState.Error -> {
                TafMetarUIState.Success(selectedAirport = airport)
            }
        }
    }

    /**
     * Get symbol code for point
     * @param lat Latitude of point
     * @param lon Longitude of point
     */
    fun getSymbolCode(lat: Double, lon: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = try {
                val symbol = getSymbolCodeUseCase(lat, lon)
                Log.d("findSymbol", "viewmodel: $symbol")
                when (_uiState.value) {
                    is TafMetarUIState.Success -> {
                        (_uiState.value as TafMetarUIState.Success).copy(
                            symbolCode = symbol
                        )
                    }

                    is TafMetarUIState.Error -> {
                        TafMetarUIState.Success(symbolCode = symbol)
                    }
                }
            } catch (e: Exception) {
                if (_uiState.value is TafMetarUIState.Success) {
                    (_uiState.value as TafMetarUIState.Success).copy(symbolCode = null)
                } else {
                    TafMetarUIState.Success(symbolCode = null)
                }
            }
        }
    }
}
