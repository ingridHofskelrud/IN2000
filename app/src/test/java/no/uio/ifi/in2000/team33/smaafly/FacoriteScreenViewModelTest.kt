package no.uio.ifi.in2000.team33.smaafly

import kotlinx.coroutines.test.runTest
import no.uio.ifi.in2000.team33.smaafly.data.room.LocationRepository
import no.uio.ifi.in2000.team33.smaafly.domain.getairports.GetAirportsUseCase
import no.uio.ifi.in2000.team33.smaafly.model.airports.Airport
import no.uio.ifi.in2000.team33.smaafly.ui.favorites.FavoriteScreenViewModel
import no.uio.ifi.in2000.team33.smaafly.ui.favorites.FavoriteUIState
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

class FacoriteScreenViewModelTest {
    private lateinit var viewModel: FavoriteScreenViewModel
    private val getAirportsUseCase: GetAirportsUseCase = mock(GetAirportsUseCase::class.java)
    private val locationRepository: LocationRepository = mock(LocationRepository::class.java)

    @Before
    fun setup() {
        viewModel = FavoriteScreenViewModel(getAirportsUseCase, locationRepository)
    }

    @Test
    fun testSelectAirportWithNullAirportUpdatesStateCorrectly(): Unit = runTest {
        viewModel.selectAirport(null)
        val state = viewModel.uiState.value
        Assert.assertTrue(state is FavoriteUIState.Success)
        Assert.assertNull((state as FavoriteUIState.Success).selectedLocation)
    }

    @Test
    fun testSelectAirport(): Unit = runTest {
        val airport = Airport("testName", "testIcao", 13.5, 20.1, 100, "sunny")

        viewModel.selectAirport(airport)

        val state = viewModel.uiState.value

        Assert.assertTrue(state is FavoriteUIState.Success)
        Assert.assertNotNull((state as FavoriteUIState.Success).selectedLocation)

        with(state.selectedLocation!!) {
            Assert.assertEquals("testName", name)
            Assert.assertEquals("testIcao", icao)
            Assert.assertEquals(13.5, lat, 0.01)
            Assert.assertEquals(20.1, lon, 0.01)
        }
    }
}
