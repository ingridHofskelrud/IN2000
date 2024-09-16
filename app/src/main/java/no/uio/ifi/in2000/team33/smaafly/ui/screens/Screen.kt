package no.uio.ifi.in2000.team33.smaafly.ui.screens

import no.uio.ifi.in2000.team33.smaafly.R

/**
 * @param route add route for navigation
 * @param label add label in bottombar
 * @param icon add drawable xml icon to bottom bar
 */
open class Screen(val route: String, val label: String, val icon: Int) {


    // Create screens for navigating

    object MapScreen : Screen(
        "map",
        "Kart",
        R.drawable.map
    )

    object SigChartScreen : Screen(
        "sigchart",
        "SigChart",
        R.drawable.weather
    )

    object TafMetarScreen : Screen(
        "tafmetar/{icao}",
        "TafMetar",
        R.drawable.sun
    )

    object RouteScreen : Screen(
        "routeScreen",
        "Rute",
        R.drawable.airport
    )

    object FavoriteScreen : Screen(
        "favorite",
        "Favoritter",
        R.drawable.heart
    )
}

