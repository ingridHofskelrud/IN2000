package no.uio.ifi.in2000.team33.smaafly.ui.toolbar

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import no.uio.ifi.in2000.team33.smaafly.ui.screens.Screen


/**
 * Bottom bar for app
 *
 * @param navController NavController for navigating screens
 * @param modifier Modifier
 */

@Composable
fun BottomBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {

    //retrieve list of screens from Screen

    val screens = listOf(
        Screen.RouteScreen,
        Screen.MapScreen,
        Screen.SigChartScreen,
        Screen.TafMetarScreen,
        Screen.FavoriteScreen
    )

    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        //get label from screens list with current index
        //get icon from screens list with current index

        screens.forEachIndexed { index, screen ->
            NavigationBarItem(
                label = {
                    Text(screens[index].label)
                },
                icon = {
                    Icon(painter = painterResource((screens[index].icon)), "")
                },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                        restoreState = true

                    }
                },
                enabled = currentRoute != screen.route
            )
        }
    }
}
