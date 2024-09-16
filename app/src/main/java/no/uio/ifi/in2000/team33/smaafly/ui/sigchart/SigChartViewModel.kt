package no.uio.ifi.in2000.team33.smaafly.ui.sigchart

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team33.smaafly.domain.getsigchart.GetSigChartUseCase
import javax.inject.Inject

sealed interface SigChartUIState {
    data class Success(val bitmap: Bitmap? = null) :
        SigChartUIState

    data object Error : SigChartUIState
}

/**
 * ViewModel for SigChart screen
 *
 * @param getSigChartUseCase Use case for getting SigChart
 */
@HiltViewModel
class SigChartViewModel @Inject constructor(
    private val getSigChartUseCase: GetSigChartUseCase
) : ViewModel() {
    private val _uiState: MutableStateFlow<SigChartUIState> =
        MutableStateFlow(SigChartUIState.Success())
    val uiState = _uiState.asStateFlow()

    init {
        loadSigCharts()
    }

    /**
     * Load current SigChart for norway
     */
    private fun loadSigCharts() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = try {
                val chart = getSigChartUseCase.getSigChart()
                when (_uiState.value) {
                    is SigChartUIState.Success -> {
                        (_uiState.value as SigChartUIState.Success).copy(
                            bitmap = chart
                        )
                    }

                    is SigChartUIState.Error -> {
                        SigChartUIState.Success(
                            bitmap = chart
                        )
                    }
                }
            } catch (e: Exception) {
                Log.d("SigChartsScreen", "Error fetching bitmap: $e")
                SigChartUIState.Error
            }
        }
    }
}