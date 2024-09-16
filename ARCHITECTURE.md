# Architecture

Our app uses the MVVM architecture, where every screen has its own ViewModel, and every ViewModel has what it needs of Model, consisting of repositories and use cases.

This contributes to high cohesion, with every class having its own well defined area of responsibility.
MVVM also helps with low coupling, by dividing the architecture into layers, with every layer only having a minimal amount of connections.

In our app architecture we have a view layer with user interface elements, developed with jetpack-compose. This is the interactive part that the user sees. In the same layer, we have the viewmodel, which acts as a holder for data and retrieves data that should be on the screen. It is also in the viewmodel that logic is controlled.

After this we have a domain layer with use cases that use our data. This is an optional layer, but we have chosen to use it in some cases. We have used it to encapsulate complex business logic and to do calculations.

A the very end is the data layer, which is where we extract data and make it usable, with repository and data source. This is also called the layer that process the business logic. Business logic is what gives our app value in terms of how data is used and handled. The repository is responsible for updating changes in data from the data source. Each data source must be responsible for only one source of data, such as file, network source or local database.

Our app also follows the unidirectional data flow (UDF) principle, where events/function calls flow downwards, and the data flows upwards. With this principle gives our application UI consistency, because all state updates are reflected in the UI, using for example stateflow.

[Drawing of architecture](ArchitectureFINAL.md)

# Technologies

We have used quite a lot of new techonologies in our app, that are recommended by Google.

Dagger Hilt is used for automatic dependency injection, for the most with Singleton pattern, which among other things helps with caching of data. This allows us a design pattern that allows classes to define the dependencies they need without acutally constructing them. This works so that a class does not need to define the instances of the object it depends on. Instead, these are injected directly into the class. This often happens when starting the app or when needed. Hilt is recommended to use for Kotlin and works well with Android components like Activities, and therefore we chose it. 

User data (favorite locations) is stored in a local room SQL database, in order to have persistent storage. Using room to have a local database is recommended in Android applications, because it simplifies the work.

# API level

Our app uses API level 24, as it is the standard recommendation from Android Studio, and the API level will cover most phyiscal Android devices used today. We also do not use any dependencies that require a higher API level, so there is no need for a higher API level.
