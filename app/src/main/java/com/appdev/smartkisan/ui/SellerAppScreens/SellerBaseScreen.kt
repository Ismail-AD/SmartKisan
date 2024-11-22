package com.appdev.smartkisan.ui.SellerAppScreens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.appdev.smartkisan.ui.MainAppScreens.BottomBarTab
import com.appdev.smartkisan.ui.MainAppScreens.GlassmorphicBottomNavigation
import com.appdev.smartkisan.ui.navigation.Routes
import dev.chrisbanes.haze.HazeState


@Composable
fun SellerBaseScreen() {
    val controller = rememberNavController()
    val hazeState = remember { HazeState() }
    var isSearchFocused by remember { mutableStateOf(false) }

    val currentRoute = controller.currentBackStackEntryAsState().value?.destination?.route
    val hideBottomBarRoutes = listOf(
        Routes.SellerAccountScreen.route
    )

    Scaffold(bottomBar = {
        if (currentRoute !in hideBottomBarRoutes && !isSearchFocused) {
            GlassmorphicBottomNavigation(
                hazeState = hazeState,
                navController = controller,
                tabs = listOf(
                    BottomBarTab(
                        title = "Home",
                        icon = Icons.Rounded.Home,
                        color = if (isSystemInDarkTheme()) Color(0xFFFA6FFF) else Color(0xFFE64A19) // Stronger pink
                    ),
                    BottomBarTab(
                        title = "Inbox",
                        icon = Icons.Rounded.Email,
                        color = if (isSystemInDarkTheme()) Color(0xFFADFF64) else Color(0xFF2196F3) // Brighter blue
                    ), BottomBarTab(
                        title = "Account",
                        icon = Icons.Rounded.Person,
                        color = if (isSystemInDarkTheme()) Color(0xFFFFA574) else Color(0xFFE91E63)  // More vibrant orange
                    )
                )
            ) { selectedTab ->
                when (selectedTab.title) {
                    "Home" -> controller.navigate(Routes.SellerHomeScreen.route)
                    "Inbox" -> controller.navigate(Routes.SellerInboxScreen.route)
                    "Account" -> controller.navigate(Routes.AccountScreen.route)
                }
            }
        }
    }) { innerPadding ->
        NavHost(
            navController = controller, startDestination = Routes.SellerHomeScreen.route,
            Modifier.padding(innerPadding)
        ) {
            composable(Routes.SellerHomeScreen.route) { SellerHomeScreen(controller) }
            composable(Routes.SellerInboxScreen.route) {
                SellerChatScreen(controller) { state ->
                    isSearchFocused = state
                }
            }
        }
    }
}