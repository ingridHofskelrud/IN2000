```mermaid
classDiagram
    class RouteViewModel{
        +GetAirportsUseCase
        +GetSymbolCodeUseCase
        +FindTimeUseCase
        +LocationRepository
        +SharedRoute
        +updateRoute(newRoute: List<Airport>)
        +removeAirportFromList(index: Int)
        +addAirportToList(airport: Airport)
        +getAirports(context: Context)
        +selectAirport(airport: Airport?)
        +getFavorites()
        +deleteFavorite(airport: Airport)
        +insertFavorite(airport: Airport)
    }
    class LocationDAO {
        +getAll() Flow~List~Location~~
        +insertLocation(location: Location)
        +delete(location: Location)
    }

    class LocationRepository {
        -locationDAO: LocationDAO
        +getAllLocations() Flow~List~Location~~
        +insertLocation(location: Location)
        +deleteLocation(location: Location)
    }
        class GetAirportsUseCase {
        -airportRepository: AirportRepository
        +invoke(context: Context)
    }

    class AirportRepository {
        -dataSource: AirportDataSource
        -airports: List~Airport~
        -airportMutex: Mutex
        +fetchAirports(context: Context) List~Airport~
    }

    class AirportDataSource {
        +fetchAirports(context: Context) List~Airport~
    }

    class ViewModel {
    }


    class GetSymbolCodeUseCase{
        +locationForecastRepository: LocationForecastRepository
    }
    class LocationForecastRepositoryImpl{
        +LocationForecastDataSource
        +getStatus()
        +getAirPressureSeaLevelNow(lat: Double, lon: Double)
        +getAirTemperatureSeaLevelNow(lat: Double, lon: Double)
        +getSymbolIdNow(lat: Double, lon: Double)
    }
    class LocationForecastDataSource{
        +HttpClient
        +getStatus(): String
        +getForecastTimeseries(lat: Double, lon: Double):GeoJsonForecastTimeseriesDTO
    }

    class SharedRoute{
        +addPointToRoute(airport: Airport)
        +removePointFromRoute(index: Int)
        +updateRoute(newRoute: List<Airport>)
    }

    RouteViewModel --> LocationRepository : Dependency
    RouteViewModel --> GetAirportsUseCase : Dependency
    RouteViewModel --> GetSymbolCodeUseCase : Dependency 
    RouteViewModel --> SharedRoute : Dependency  
    RouteViewModel <|-- ViewModel : Inheritance

    LocationRepository --> LocationDAO : Dependency
    GetAirportsUseCase --> AirportRepository : Dependency
    AirportRepository --> AirportDataSource : Dependency

    GetSymbolCodeUseCase --> LocationForecastRepositoryImpl : Dependency
    LocationForecastRepositoryImpl --> LocationForecastDataSource : Dependency



