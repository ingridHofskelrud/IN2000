package no.uio.ifi.in2000.team33.smaafly.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import no.uio.ifi.in2000.team33.smaafly.ui.favorites.FavoriteScreen
import no.uio.ifi.in2000.team33.smaafly.ui.map.MapScreen
import no.uio.ifi.in2000.team33.smaafly.ui.routescreen.RouteScreen
import no.uio.ifi.in2000.team33.smaafly.ui.screens.Screen
import no.uio.ifi.in2000.team33.smaafly.ui.sigchart.SigChartScreen
import no.uio.ifi.in2000.team33.smaafly.ui.tafmetar.TafMetarScreen

/**
 * Starting point of app
 *
 * Responsible for handling navigation between screens
 */
@Composable
fun Controller() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = Screen.RouteScreen.route) {
        composable(Screen.MapScreen.route) { MapScreen(navController) }
        composable(Screen.SigChartScreen.route) { SigChartScreen(navController) }
        composable(Screen.RouteScreen.route) { RouteScreen(navController) }
        composable(Screen.TafMetarScreen.route) { backStackEntry ->
            val icao = backStackEntry.arguments?.getString("icao")
            TafMetarScreen(navController, icao)
        }
        composable(Screen.FavoriteScreen.route) { FavoriteScreen(navController = navController) }

    }
}

