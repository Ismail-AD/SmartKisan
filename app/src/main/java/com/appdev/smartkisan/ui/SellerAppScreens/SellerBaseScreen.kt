package com.appdev.smartkisan.ui.SellerAppScreens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.appdev.smartkisan.ui.MainAppScreens.GlassmorphicBottomNavigation
import com.appdev.smartkisan.ui.navigation.Routes
import dev.chrisbanes.haze.HazeState


@Composable
fun SellerBaseScreen() {
    val controller = rememberNavController()
    val hazeState = remember { HazeState() }

    val currentRoute = controller.currentBackStackEntryAsState().value?.destination?.route
    val hideBottomBarRoutes = listOf(
        Routes.SellerHomeScreen.route
    )

    Scaffold(bottomBar = {
        if (currentRoute !in hideBottomBarRoutes) {
            GlassmorphicBottomNavigation(hazeState, navController = controller)
        }
    }) { innerPadding ->
        NavHost(
            navController = controller, startDestination = Routes.SellerHomeScreen.route,
            Modifier.padding(innerPadding)
        ) {
            composable(Routes.SellerHomeScreen.route) { SellerHomeScreen(controller) }
        }
    }
}