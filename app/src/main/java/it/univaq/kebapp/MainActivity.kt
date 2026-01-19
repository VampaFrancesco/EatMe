package it.univaq.kebapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import it.univaq.kebapp.ui.theme.KebabbariApplicationTheme
import kotlinx.serialization.Serializable
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.toRoute
import dagger.hilt.android.AndroidEntryPoint
import it.univaq.kebapp.ui.screen.detail.DetailScreen
import it.univaq.kebapp.ui.screen.home.HomeScreen
import it.univaq.kebapp.ui.screen.list.ListScreen
import it.univaq.kebapp.ui.screen.map.MapScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KebabbariApplicationTheme {
                val navController = rememberNavController()

                // Osserva rotta corrente
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    // Mostra la bottom bar SOLO se non siamo in Home
                    bottomBar = {
                        if (!isHomeDestination(currentDestination)) {
                            BottomNavigationBar(navController)
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        modifier = Modifier.padding(innerPadding),
                        navController = navController,
                        startDestination = Screen.Home
                    ) {
                        composable<Screen.Home> {
                            HomeScreen(
                                modifier = Modifier.fillMaxSize(),
                                onNavigateToList = {
                                    navController.navigate(Screen.List)
                                },
                                onNavigateToMap = {
                                    navController.navigate(Screen.Map)
                                }
                            )
                        }
                        composable<Screen.List> {
                            ListScreen(
                                modifier = Modifier.fillMaxSize(),
                                onNavigateBack = { navController.popBackStack() },   // AGGIUNTO
                                onNavigateToDetail = { kebabbari ->
                                    navController.navigate(
                                        Screen.Detail(
                                            id = kebabbari.id ?: 0,
                                            cnome = kebabbari.cnome,
                                            ccomune = kebabbari.ccomune,
                                            cprovincia = kebabbari.cprovincia,
                                            cregione = kebabbari.cregione,
                                            clatitudine = kebabbari.clatitudine,
                                            clongitudine = kebabbari.clongitudine
                                        )
                                    )
                                }
                            )
                        }

                        composable<Screen.Map> {
                            MapScreen(
                                modifier = Modifier.fillMaxSize(),
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable<Screen.Detail> {
                            val detail = it.toRoute<Screen.Detail>()
                            DetailScreen(
                                modifier = Modifier.fillMaxSize(),
                                kebabbari = detail,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun isHomeDestination(destination: NavDestination?): Boolean {
        // Route generata da navigation-compose-serialization è il canonicalName della classe
        return destination?.route == Screen.Home::class.java.canonicalName
    }

    @Composable
    fun BottomNavigationBar(navController: NavHostController) {
        val items = remember {
            listOf(
                BottomNavigationItem(
                    title = "Lista",
                    image = Icons.AutoMirrored.Default.List,
                    route = Screen.List
                ),
                BottomNavigationItem(
                    title = "Mappa",
                    image = Icons.Default.LocationOn,
                    route = Screen.Map
                )
            )
        }

        androidx.compose.material3.NavigationBar {
            val navigationBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navigationBackStackEntry?.destination?.route

            items.forEach { item ->
                NavigationBarItem(
                    selected = currentRoute == item.route.javaClass.canonicalName,
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Icon(item.image, contentDescription = item.title) },
                    label = { Text(item.title) }
                )
            }
        }
    }

    data class BottomNavigationItem(
        val title: String,
        val image: ImageVector,
        val route: Screen
    )

    sealed class Screen {
        @Serializable
        data object Home : Screen()

        @Serializable
        data object List : Screen()

        @Serializable
        data object Map : Screen()

        @Serializable
        data class Detail(
            val id: Int,
            val cnome: String,
            val ccomune: String,
            val cprovincia: String,
            val cregione: String,
            val clatitudine: Double,
            val clongitudine: Double
        ) : Screen()
    }
}
