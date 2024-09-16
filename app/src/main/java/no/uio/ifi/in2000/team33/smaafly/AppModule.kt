package no.uio.ifi.in2000.team33.smaafly

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import no.uio.ifi.in2000.team33.smaafly.data.airports.AirportDataSource
import no.uio.ifi.in2000.team33.smaafly.data.airports.AirportRepository
import no.uio.ifi.in2000.team33.smaafly.data.grib.GribDataSource
import no.uio.ifi.in2000.team33.smaafly.data.grib.GribRepository
import no.uio.ifi.in2000.team33.smaafly.data.grib.GribRepositoryImpl
import no.uio.ifi.in2000.team33.smaafly.data.locationforecast.LocationForecastDataSource
import no.uio.ifi.in2000.team33.smaafly.data.locationforecast.LocationForecastRepository
import no.uio.ifi.in2000.team33.smaafly.data.locationforecast.LocationForecastRepositoryImpl
import no.uio.ifi.in2000.team33.smaafly.data.room.LocationDAO
import no.uio.ifi.in2000.team33.smaafly.data.room.LocationDatabase
import no.uio.ifi.in2000.team33.smaafly.data.room.LocationRepository
import no.uio.ifi.in2000.team33.smaafly.data.sigchart.SigChartDataSource
import no.uio.ifi.in2000.team33.smaafly.data.sigchart.SigChartRepository
import no.uio.ifi.in2000.team33.smaafly.data.tafmetar.NetworkTafMetarRepository
import no.uio.ifi.in2000.team33.smaafly.data.tafmetar.TafMetarDataSource
import no.uio.ifi.in2000.team33.smaafly.data.tafmetar.TafMetarRepository
import no.uio.ifi.in2000.team33.smaafly.data.weather.WeatherRepository
import no.uio.ifi.in2000.team33.smaafly.data.weather.WeatherRepositoryImpl
import no.uio.ifi.in2000.team33.smaafly.domain.finddistance.FindDistanceUseCase
import no.uio.ifi.in2000.team33.smaafly.domain.findtime.FindTimeUseCase
import no.uio.ifi.in2000.team33.smaafly.domain.getSymbolCode.GetSymbolCodeUseCase
import no.uio.ifi.in2000.team33.smaafly.domain.getairports.GetAirportsUseCase
import no.uio.ifi.in2000.team33.smaafly.domain.getsigchart.GetSigChartUseCase
import no.uio.ifi.in2000.team33.smaafly.domain.gettafmetar.GetTafMetarUseCase
import no.uio.ifi.in2000.team33.smaafly.ui.components.SharedRoute
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    @Named("proxy")
    fun provideProxyHttpClient(): HttpClient {
        val client = HttpClient(CIO) {
            defaultRequest {
                header("X-Gravitee-API-Key", "9bddd46e-d74a-4825-a74d-5ff2dad25771")
                url("https://gw-uio.intark.uh-it.no/in2000/")
            }

            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }
        return client
    }

    @Provides
    @Singleton
    @Named("metno")
    fun provideMetnoHttpClient(): HttpClient {
        val client = HttpClient(CIO) {
            defaultRequest {
                url("https://in2000.api.met.no/weatherapi/")
            }

            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }

            install(UserAgent) {
                agent = "IN2000/team-33 sebasaw@uio.no"
            }
        }
        return client
    }

    @Provides
    @Singleton
    fun provideAirportDataSource() = AirportDataSource()

    @Provides
    @Singleton
    fun provideAirportRepository(airportDataSource: AirportDataSource) =
        AirportRepository(airportDataSource)

    @Provides
    fun provideGetAirportsUseCase(airportRepository: AirportRepository) =
        GetAirportsUseCase(airportRepository)

    @Provides
    @Singleton
    fun provideGribDataSource(@Named("metno") client: HttpClient) = GribDataSource(client)

    @Provides
    @Singleton
    fun provideGribRepository(gribDataSource: GribDataSource): GribRepository =
        GribRepositoryImpl(gribDataSource)


    @Provides
    @Singleton
    fun provideLocationForecastDataSource(@Named("proxy") client: HttpClient) =
        LocationForecastDataSource(client)

    @Provides
    @Singleton
    fun provideLocationForecastRepository(locationForecastDataSource: LocationForecastDataSource): LocationForecastRepository =
        LocationForecastRepositoryImpl(locationForecastDataSource)

    @Provides
    @Singleton
    fun provideWeatherRepository(
        gribRepository: GribRepository,
        locationForecastRepository: LocationForecastRepository
    ): WeatherRepository = WeatherRepositoryImpl(gribRepository, locationForecastRepository)

    @Provides
    fun provideGetSymbolCodeUseCase(locationForecastRepository: LocationForecastRepository) =
        GetSymbolCodeUseCase(locationForecastRepository)

    @Provides
    fun provideFindDistanceUseCase() = FindDistanceUseCase()

    @Provides
    fun provideFindTimeUseCase(
        findDistanceUseCase: FindDistanceUseCase,
        weatherRepository: WeatherRepository
    ) = FindTimeUseCase(findDistanceUseCase, weatherRepository)

    @Provides
    @Singleton
    fun provideSigChartDataSource(@Named("metno") client: HttpClient) = SigChartDataSource(client)

    @Provides
    @Singleton
    fun provideSigChartRepository(sigChartDataSource: SigChartDataSource) =
        SigChartRepository(sigChartDataSource)

    @Provides
    fun provideSigChartUseCase(sigChartRepository: SigChartRepository) =
        GetSigChartUseCase(sigChartRepository)

    @Provides
    @Singleton
    fun provideLocationDatabase(@ApplicationContext app: Context) = Room.databaseBuilder(
        context = app,
        klass = LocationDatabase::class.java,
        name = "LocationDatabase"
    ).build()

    @Provides
    @Singleton
    fun provideLocationDao(db: LocationDatabase) = db.locationDao()

    @Provides
    @Singleton
    fun provideLocationRepository(dao: LocationDAO) = LocationRepository(dao)

    @Provides
    @Singleton
    fun provideTafMetarDataSource(@Named("proxy") client: HttpClient)
    = TafMetarDataSource(client)

    @Provides
    @Singleton
    fun provideNetworkTafMetarRepository(tafMetarDataSource: TafMetarDataSource): TafMetarRepository
    = NetworkTafMetarRepository(tafMetarDataSource)

    @Provides
    fun provideGetTafMetarUseCase(tafMetarRepository: TafMetarRepository)
    = GetTafMetarUseCase(tafMetarRepository)

    @Singleton
    @Provides
    fun provideSharedRoute() = SharedRoute()
}