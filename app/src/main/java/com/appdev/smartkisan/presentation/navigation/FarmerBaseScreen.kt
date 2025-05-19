package com.appdev.smartkisan.presentation.navigation

import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.appdev.smartkisan.R
import com.appdev.smartkisan.Utils.DateTimeUtils
import com.appdev.smartkisan.domain.model.New
import com.appdev.smartkisan.presentation.feature.farmer.diseasedetection.DiseaseDetectViewModel
import com.appdev.smartkisan.presentation.feature.chatmessages.ChatMessagesRoot
import com.appdev.smartkisan.presentation.feature.farmer.news.AgricultureNewsRoot
import com.appdev.smartkisan.presentation.feature.farmer.chatbot.ChatBotRoot
import com.appdev.smartkisan.presentation.feature.farmer.chats.ChatListRoot
import com.appdev.smartkisan.presentation.feature.farmer.diseasedetection.DiagnosisResult.DiagnosisResultRoot
import com.appdev.smartkisan.presentation.feature.farmer.chats.EmptyState
import com.appdev.smartkisan.presentation.feature.farmer.home.HomeRoot
import com.appdev.smartkisan.presentation.feature.farmer.diseasedetection.ImageCropping.ImageCropperRoot
import com.appdev.smartkisan.presentation.feature.farmer.shopslocation.MapRootScreen
import com.appdev.smartkisan.presentation.feature.farmer.marketplace.MarketPlaceRoot
import com.appdev.smartkisan.presentation.feature.farmer.news.NewsDetailScreen
import com.appdev.smartkisan.presentation.feature.farmer.diseasedetection.ImageInput.PlantDiseaseRoot
import com.appdev.smartkisan.presentation.feature.farmer.marketplace.ProductDetails
import com.appdev.smartkisan.presentation.feature.farmer.account.UserAccountRoot
import com.fa.lib.SlippyBar
import com.fa.lib.SlippyBarStyle
import com.fa.lib.SlippyBottomBar
import com.fa.lib.SlippyIconStyle
import com.fa.lib.SlippyTab
import com.fa.lib.SlippyTextStyle
import com.fa.lib.SlippyTheme
import dev.chrisbanes.haze.HazeState
import kotlinx.serialization.json.Json

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseScreen(onLogout: () -> Unit = {}) {
    val controller = rememberNavController()
    val hazeState = remember { HazeState() }
    val context = androidx.compose.ui.platform.LocalContext.current

    val currentRoute = controller.currentBackStackEntryAsState().value?.destination?.route
    val hideBottomBarRoutes = listOf(
        Routes.ProductDetailScreen.route + "/{productJson}", Routes.DiagnosisResult.route,
        Routes.ChatBotScreen.route,
        Routes.NewsList.route,
        Routes.ImageCropper.route,
        Routes.ShopsOnMap.route,
        Routes.NewsDetails.route + "/{newsJson}",
        Routes.UserChatListScreen.route,
        Routes.ChatInDetailScreen.route + "/{receiverId}/{name}/{profilePic}"
    )


    Scaffold(bottomBar = {
        if (currentRoute !in hideBottomBarRoutes) {
            val tabs: List<SlippyTab> = listOf(
                SlippyTab(name = R.string.home, icon = R.drawable.home_icon, action = {
                    controller.navigate(Routes.HomeScreen.route) {
                        popUpTo(controller.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }),
                SlippyTab(name = R.string.diagnosis, icon = R.drawable.diagnosisicon, action = {
                    controller.navigate(Routes.PlantDisease.route) {
                        popUpTo(controller.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }),
                SlippyTab(name = R.string.market, icon = R.drawable.shop, action = {
                    controller.navigate(Routes.MarketPlace.route) {
                        popUpTo(controller.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }),
                SlippyTab(name = R.string.account, icon = R.drawable.account_icon, action = {
                    controller.navigate(Routes.AccountScreen.route) {
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
            navController = controller,
            startDestination = Routes.HomeScreen.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.HomeScreen.route) { HomeRoot(controller) }
            composable(Routes.ChatBotScreen.route) {
                ChatBotRoot(
                    controller
                )
            }
            composable(Routes.AccountScreen.route) {
                UserAccountRoot(controller) {
                    onLogout()
                }
            }
            composable(Routes.MarketPlace.route) { MarketPlaceRoot(controller) }
            composable(Routes.ShopsOnMap.route) { MapRootScreen(controller) }
            composable(Routes.UserChatListScreen.route) { ChatListRoot(controller) }
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
            composable(Routes.PlantDisease.route) {
                val viewModel: DiseaseDetectViewModel = hiltViewModel()
                PlantDiseaseRoot(controller, viewModel)
            }

            composable(Routes.ImageCropper.route) {
                val parentEntry = remember(controller) {
                    controller.getBackStackEntry(Routes.PlantDisease.route)
                }
                val viewModel: DiseaseDetectViewModel = hiltViewModel(parentEntry)
                ImageCropperRoot(controller, viewModel)
            }

            composable(Routes.DiagnosisResult.route) {
                val parentEntry = remember(controller) {
                    controller.getBackStackEntry(Routes.PlantDisease.route)
                }
                val viewModel: DiseaseDetectViewModel = hiltViewModel(parentEntry)
                DiagnosisResultRoot(controller, viewModel)
            }
            composable(Routes.NewsList.route) {
                AgricultureNewsRoot(controller)
            }

            composable(
                route = "${Routes.NewsDetails.route}/{newsJson}",
                arguments = listOf(navArgument("newsJson") { type = NavType.StringType })
            ) { backStackEntry ->
                val newsJson = backStackEntry.arguments?.getString("newsJson") ?: ""

                val news = try {
                    Json.decodeFromString<New>(Uri.decode(newsJson))
                } catch (e: Exception) {
                    null
                }

                if (news != null) {
                    var date by remember {
                        mutableStateOf(DateTimeUtils.formatDateTime(news.publish_date))
                    }
                    NewsDetailScreen(
                        date,
                        news = news,
                        onBackPressed = { controller.popBackStack() }
                    )
                } else {
                    // Show error state if article not found
                    EmptyState(message = "Article not found or has been removed")
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

// Remove the MaterialBottomNavigation function as it's no longer needed
// Remove the NavigationItem data class as it's no longer needed

// Keep these classes as they might be used elsewhere in the code
interface CommonBottomBarTab {
    val title: String
    val icon: androidx.compose.ui.graphics.vector.ImageVector
    val color: androidx.compose.ui.graphics.Color
}

data class BottomBarTab(
    override val title: String,
    override val icon: androidx.compose.ui.graphics.vector.ImageVector,
    override val color: androidx.compose.ui.graphics.Color
) : CommonBottomBarTab