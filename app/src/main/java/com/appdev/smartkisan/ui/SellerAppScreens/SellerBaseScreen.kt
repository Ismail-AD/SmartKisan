package com.appdev.smartkisan.ui.SellerAppScreens

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.appdev.smartkisan.ViewModel.StoreViewModel
import com.appdev.smartkisan.data.Product
import com.appdev.smartkisan.ui.MainAppScreens.BottomBarTab
import com.appdev.smartkisan.ui.MainAppScreens.GlassmorphicBottomNavigation
import com.appdev.smartkisan.ui.MainAppScreens.ProductDetails
import com.appdev.smartkisan.ui.navigation.Routes
import dev.chrisbanes.haze.HazeState
import kotlinx.serialization.json.Json


@Composable
fun SellerBaseScreen() {
    val controller = rememberNavController()
    val hazeState = remember { HazeState() }
    var isSearchFocused by remember { mutableStateOf(false) }

    val currentRoute = controller.currentBackStackEntryAsState().value?.destination?.route
    val hideBottomBarRoutes = listOf(
        Routes.ChatInDetailScreen.route,
        Routes.StoreManagementScreen.route,
        Routes.AddProductScreen.route+ "/{productJson}",
        Routes.ProductDetailScreen.route + "/{productJson}"
    )

    val selectedTabIndex = when (currentRoute) {
        Routes.SellerHomeScreen.route -> 0
        Routes.SellerInboxScreen.route -> 1
        Routes.SellerAccountScreen.route -> 2
        else -> -1  // Default value for routes not in the bottom bar
    }

    Scaffold(bottomBar = {
        if (currentRoute !in hideBottomBarRoutes && !isSearchFocused) {
            val tabs = listOf(
                BottomBarTab(
                    title = "Home",
                    icon = Icons.Rounded.Home,
                    color = if (isSystemInDarkTheme()) Color(0xFFFA6FFF) else Color(0xFFE64A19)
                ),
                BottomBarTab(
                    title = "Inbox",
                    icon = Icons.Rounded.Email,
                    color = if (isSystemInDarkTheme()) Color(0xFFADFF64) else Color(0xFF2196F3)
                ),
                BottomBarTab(
                    title = "Account",
                    icon = Icons.Rounded.Person,
                    color = if (isSystemInDarkTheme()) Color(0xFFFFA574) else Color(0xFFE91E63)
                )
            )
            GlassmorphicBottomNavigation(
                hazeState = hazeState,
                navController = controller,
                selectedTabIndex = if (selectedTabIndex >= 0) selectedTabIndex else 0,
                tabs = tabs
            ) { selectedTab ->
                when (selectedTab.title) {
                    "Home" -> controller.navigate(Routes.SellerHomeScreen.route)
                    "Inbox" -> controller.navigate(Routes.SellerInboxScreen.route)
                    "Account" -> controller.navigate(Routes.SellerAccountScreen.route)
                }
            }
        }
    }) { innerPadding ->
        NavHost(
            navController = controller, startDestination = Routes.SellerHomeScreen.route,
            Modifier.padding(innerPadding)
        ) {
            composable(Routes.SellerHomeScreen.route) { SellerHomeScreen(controller) }
            composable(Routes.SellerAccountScreen.route) { SellerProfileScreen() }
            composable(Routes.ChatInDetailScreen.route) { InDetailChatScreen(controller) }
            composable(Routes.StoreManagementScreen.route) {
                StoreManagementRoot(
                    controller
                )
            }
            composable(Routes.AddProductScreen.route + "/{productJson}", arguments = listOf(
                navArgument("productJson") {
                    type = NavType.StringType
                }
            )) {backStackEntry ->
                val productJson = backStackEntry.arguments?.getString("productJson") ?: ""
                val product = try {
                    Json.decodeFromString<Product>(Uri.decode(productJson))
                } catch (e: Exception) {
                    null
                }
                val storeViewModel:StoreViewModel = hiltViewModel()
                if (product != null) {
                    storeViewModel.initializeWithProduct(product)
                }
                AddProductRoot(storeViewModel = storeViewModel, navHostController = controller)
            }
            composable(Routes.SellerInboxScreen.route) {
                SellerChatScreen(controller) { state ->
                    isSearchFocused = state
                }
            }
            composable(route = Routes.ProductDetailScreen.route + "/{productJson}",
                arguments = listOf(
                    navArgument("productJson") {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->
                val productJson = backStackEntry.arguments?.getString("productJson") ?: ""
                val product = try {
                    Json.decodeFromString<Product>(Uri.decode(productJson))
                } catch (e: Exception) {
                    null
                }
                if (product != null) {
                    ProductDetails(product = product, controller = controller)
                }
            }
        }
    }
}