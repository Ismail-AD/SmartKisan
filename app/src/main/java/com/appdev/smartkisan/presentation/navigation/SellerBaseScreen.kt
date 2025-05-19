package com.appdev.smartkisan.presentation.navigation

import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.appdev.smartkisan.R
import com.appdev.smartkisan.presentation.feature.Seller.store.StoreViewModel
import com.appdev.smartkisan.domain.model.Product
import com.appdev.smartkisan.presentation.feature.farmer.marketplace.ProductDetails
import com.appdev.smartkisan.presentation.feature.chatmessages.ChatMessagesRoot
import com.appdev.smartkisan.presentation.feature.Seller.productmanagement.AddProductRoot
import com.appdev.smartkisan.presentation.feature.Seller.inbox.SellerChatScreenRoot
import com.appdev.smartkisan.presentation.feature.Seller.home.SellerHomeScreen
import com.appdev.smartkisan.presentation.feature.Seller.account.SellerProfileRoot
import com.appdev.smartkisan.presentation.feature.Seller.store.StoreManagementRoot
import com.fa.lib.SlippyBar
import com.fa.lib.SlippyBarStyle
import com.fa.lib.SlippyBottomBar
import com.fa.lib.SlippyIconStyle
import com.fa.lib.SlippyTab
import com.fa.lib.SlippyTextStyle
import com.fa.lib.SlippyTheme
import dev.chrisbanes.haze.HazeState
import kotlinx.serialization.json.Json


@Composable
fun SellerBaseScreen(onLogout: () -> Unit = {}) {
    val controller = rememberNavController()
    val hazeState = remember { HazeState() }
    var isSearchFocused by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val currentRoute = controller.currentBackStackEntryAsState().value?.destination?.route
    val hideBottomBarRoutes = listOf(
        Routes.ChatInDetailScreen.route,
        Routes.StoreManagementScreen.route,
        Routes.AddProductScreen.route+ "/{productJson}",
        Routes.ProductDetailScreen.route + "/{productJson}",
        Routes.ChatInDetailScreen.route + "/{receiverId}/{name}/{profilePic}"
    )

    Scaffold(bottomBar = {
        if (currentRoute !in hideBottomBarRoutes && !isSearchFocused) {
            val tabs: List<SlippyTab> = listOf(
                SlippyTab(name = R.string.home, icon = R.drawable.home_icon, action = {
                    controller.navigate(Routes.SellerHomeScreen.route) {
                        popUpTo(controller.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }),
                SlippyTab(name = R.string.inbox, icon = R.drawable.email, action = {
                    controller.navigate(Routes.SellerInboxScreen.route) {
                        popUpTo(controller.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }),
                SlippyTab(name = R.string.account, icon = R.drawable.account_icon, action = {
                    controller.navigate(Routes.SellerAccountScreen.route) {
                        popUpTo(controller.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                })
            )

            SlippyBottomBar(
                theme = SlippyTheme.CLASSIC,
                bar = SlippyBar(
                    barStyle = SlippyBarStyle(backgroundColor = MaterialTheme.colorScheme.surfaceVariant),
                    textStyle = SlippyTextStyle(
                        enabledTextColor = MaterialTheme.colorScheme.onBackground,
                        disabledTextColor = MaterialTheme.colorScheme.inverseSurface
                    ),
                    iconStyle = SlippyIconStyle(
                        disabledIconColor = MaterialTheme.colorScheme.inverseSurface,
                        enabledIconColor = MaterialTheme.colorScheme.tertiaryContainer,
                    ),
                    startIndex = 0
                ),
                tabs = tabs
            )
        }
    }) { innerPadding ->
        NavHost(
            navController = controller, startDestination = Routes.SellerHomeScreen.route,
            Modifier.padding(innerPadding)
        ) {
            composable(Routes.SellerHomeScreen.route) { SellerHomeScreen(controller) }
            composable(Routes.SellerAccountScreen.route) { SellerProfileRoot{
                onLogout()
            } }
            composable(
                route = Routes.ChatInDetailScreen.route + "/{receiverId}/{name}/{profilePic}",
                arguments = listOf(
                    navArgument("receiverId") { type = NavType.StringType },
                    navArgument("name") { type = NavType.StringType },
                    navArgument("profilePic") {
                        type = NavType.StringType
                        // This allows the profilePic parameter to contain slashes
                        nullable = false
                    }
                )
            ) { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("receiverId") ?: ""
                val username = backStackEntry.arguments?.getString("name") ?: ""
                val encodedProfilePic = backStackEntry.arguments?.getString("profilePic") ?: ""
                val profilePic = Uri.decode(encodedProfilePic)

                ChatMessagesRoot(
                    userId,
                    username,
                    profilePic,
                    controller = controller,
                )
            }
            composable(Routes.StoreManagementScreen.route) {
                StoreManagementRoot(
                    controller
                )
            }
            composable(
                Routes.AddProductScreen.route + "/{productJson}", arguments = listOf(
                    navArgument("productJson") {
                        type = NavType.StringType
                    }
                )) {backStackEntry ->
                val productJson = backStackEntry.arguments?.getString("productJson") ?: ""
                val product = try {
                    Json.decodeFromString<com.appdev.smartkisan.domain.model.Product>(Uri.decode(productJson))
                } catch (e: Exception) {
                    null
                }
                val storeViewModel: StoreViewModel = hiltViewModel()
                if (product != null) {
                    storeViewModel.initializeWithProduct(product)
                }
                AddProductRoot(storeViewModel = storeViewModel, navHostController = controller)
            }
            composable(Routes.SellerInboxScreen.route) {
                SellerChatScreenRoot(controller)
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
                    Json.decodeFromString<com.appdev.smartkisan.domain.model.Product>(Uri.decode(productJson))
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