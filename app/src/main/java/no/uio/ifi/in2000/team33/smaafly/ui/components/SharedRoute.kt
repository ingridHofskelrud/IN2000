package no.uio.ifi.in2000.team33.smaafly.ui.components

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import no.uio.ifi.in2000.team33.smaafly.model.airports.Airport
import javax.inject.Singleton

/**
 * Class containing the route
 * Used for sharing route between screens
 */
@Singleton
class SharedRoute {
    private val _route: MutableStateFlow<List<Airport>> = MutableStateFlow(emptyList())
    val route = _route.asStateFlow()

    /**
     * Add Point To Route
     * @param airport to be added to the route
     */
    fun addPointToRoute(airport: Airport) {
        _route.update {
            it + airport
        }
    }

    /**
     * Remove Point Form Route
     * @param index as int of the airport to be removed.
     */
    fun removePointFromRoute(index: Int) {
        _route.update {
            val list = it.toMutableList()
            list.removeAt(index)
            list.toList()
        }
    }

    /**
     * Update Route
     * @param newRoute: List<Airport> the list after reorganising
     */
    fun updateRoute(newRoute: List<Airport>) {
        _route.update {
            newRoute
        }
    }
}
