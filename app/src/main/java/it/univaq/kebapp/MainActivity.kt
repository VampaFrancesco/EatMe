package it.univaq.kebapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import it.univaq.kebapp.ui.theme.KebabbariApplicationTheme
import kotlinx.serialization.Serializable
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import dagger.hilt.android.AndroidEntryPoint
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
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { BottomNavigationBar(navController) }
                ) { innerPadding ->
                    NavHost(
                        modifier = Modifier.padding(innerPadding),
                        navController = navController,
                        startDestination = Screen.List
                    ){
                        composable<Screen.List>{
                            ListScreen(
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        composable<Screen.Map>{
                            MapScreen(
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                }
            }
        }
    }
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

    NavigationBar {

        val navigationBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navigationBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route.javaClass.canonicalName ,
                onClick = {
                    navController.navigate(item.route)
                    {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(item.image, contentDescription = item.title)
                },
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
    data object List : Screen()

    @Serializable
    data object Map : Screen()
}