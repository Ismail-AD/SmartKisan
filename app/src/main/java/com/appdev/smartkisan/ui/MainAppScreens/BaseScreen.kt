package com.appdev.smartkisan.ui.MainAppScreens

import android.net.Uri
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.appdev.smartkisan.Actions.ChatActions
import com.appdev.smartkisan.Utils.DateTimeUtils
import com.appdev.smartkisan.ViewModel.NewsViewModel
import com.appdev.smartkisan.ViewModel.UserChatViewModel
import com.appdev.smartkisan.data.New
import com.appdev.smartkisan.data.Product
import com.appdev.smartkisan.ui.SharedScreens.ChatMessagesRoot
import com.appdev.smartkisan.ui.navigation.Routes
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeChild
import kotlinx.serialization.json.Json

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseScreen(onLogout: () -> Unit = {}) {
    val controller = rememberNavController()
    val hazeState = remember { HazeState() }

    val currentRoute = controller.currentBackStackEntryAsState().value?.destination?.route
    val hideBottomBarRoutes = listOf(
        Routes.ProductDetailScreen.route + "/{productJson}", Routes.DiagnosisResult.route,
        Routes.ChatBotScreen.route,
        Routes.NewsList.route,
        Routes.ShopsOnMap.route,
        Routes.NewsDetails.route+"/{newsJson}",
        Routes.UserChatListScreen.route,
        Routes.ChatInDetailScreen.route + "/{receiverId}/{name}/{profilePic}"
    )

    val selectedTabIndex = when (currentRoute) {
        Routes.HomeScreen.route -> 0
        Routes.PlantDisease.route -> 1
        Routes.MarketPlace.route -> 2
        Routes.AccountScreen.route -> 3
        else -> -1  // Default value for routes not in the bottom bar
    }

    Scaffold(bottomBar = {
        if (currentRoute !in hideBottomBarRoutes) {
            GlassmorphicBottomNavigation(
                hazeState = hazeState,
                navController = controller,
                selectedTabIndex = if (selectedTabIndex >= 0) selectedTabIndex else 0,
                tabs = listOf(
                    BottomBarTab(
                        title = "Home",
                        icon = Icons.Rounded.Home,
                        color = if (isSystemInDarkTheme()) Color(0xFFFA6FFF) else Color(0xFFE64A19) // Stronger pink
                    ),
                    BottomBarTab(
                        title = "Diagnosis",
                        icon = Icons.Rounded.Build,
                        color = if (isSystemInDarkTheme()) Color(0xFFADFF64) else Color(0xFF2196F3) // Brighter blue
                    ), BottomBarTab(
                        title = "Market",
                        icon = Icons.Rounded.ShoppingCart,
                        color = if (isSystemInDarkTheme()) Color(0xFFFFA574) else Color(0xFFE91E63)  // More vibrant orange
                    ), BottomBarTab(
                        title = "Account",
                        icon = Icons.Rounded.Person,
                        color = if (isSystemInDarkTheme()) Color(0xFFADFF64) else Color(0xFF4CAF50) // Deeper green
                    )
                )
            ) { selectedTab ->
                when (selectedTab.title) {
                    "Home" -> controller.navigate(Routes.HomeScreen.route)
                    "Diagnosis" -> controller.navigate(Routes.PlantDisease.route)
                    "Market" -> controller.navigate(Routes.MarketPlace.route)
                    "Account" -> controller.navigate(Routes.AccountScreen.route)
                }
            }
        }
    }) { innerPadding ->
        NavHost(
            navController = controller, startDestination = Routes.HomeScreen.route,
            Modifier.padding(innerPadding)
        ) {
            composable(Routes.HomeScreen.route) { HomeRoot(controller) }
            composable(Routes.ChatBotScreen.route) {
                ChatBotRoot(
                    controller
                )
            }
            composable(Routes.AccountScreen.route) {
                UserAccountRoot (controller){
                    onLogout()
                }
            }
            composable(Routes.MarketPlace.route) { MarketPlaceRoot(controller) }
            composable(Routes.ShopsOnMap.route) { MapRootScreen(controller) }
            composable(Routes.PlantDisease.route) { PlantDiseaseRoot(controller) }
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

            composable(Routes.DiagnosisResult.route) { DiagnosisResult(controller) }
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

@Composable
fun GlassmorphicBottomNavigation(
    hazeState: HazeState,
    navController: NavHostController,
    tabs: List<CommonBottomBarTab>,
    selectedTabIndex: Int,
    onTabSelected: (CommonBottomBarTab) -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    val dayColors = if (isDarkTheme)
        listOf(
            Color.White.copy(alpha = .8f),
            Color.White.copy(alpha = .35f),
        )
    else listOf(
        Color(0xFF8F8F8F).copy(alpha = 0.35f),
        Color(0xFF8F8F8F).copy(alpha = 0.2f)
    )

    Box(
        modifier = Modifier
            .padding(vertical = 24.dp, horizontal = 20.dp)
            .fillMaxWidth()
            .height(64.dp)
            .hazeChild(state = hazeState, shape = CircleShape)
            .border(
                width = if (isDarkTheme) Dp.Hairline else (1.5).dp,
                brush = Brush.verticalGradient(colors = dayColors),
                shape = CircleShape
            )
    ) {
        BottomBarTabs(
            isDarkTheme = isDarkTheme,
            tabs = tabs,
            selectedTab = selectedTabIndex
        ) {
            onTabSelected(it)
        }


        val animatedSelectedTabIndex by animateFloatAsState(
            targetValue = selectedTabIndex.toFloat(),
            animationSpec = spring(
                stiffness = Spring.StiffnessLow,
                dampingRatio = Spring.DampingRatioLowBouncy
            )
        )

        val animatedColor by animateColorAsState(
            targetValue = tabs[selectedTabIndex].color,
            animationSpec = spring(stiffness = Spring.StiffnessLow)
        )

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .blur(50.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
        ) {
            val tabWidth = size.width / tabs.size
            drawCircle(
                color = animatedColor.copy(alpha = .6f),
                radius = size.height / 2,
                center = Offset(
                    (tabWidth * animatedSelectedTabIndex) + tabWidth / 2,
                    size.height / 2
                )
            )
        }
    }
}

@Composable
fun BottomBarTabs(
    isDarkTheme: Boolean,
    tabs: List<CommonBottomBarTab>,
    selectedTab: Int,
    onTabSelected: (CommonBottomBarTab) -> Unit,
) {
    CompositionLocalProvider(
        LocalTextStyle provides LocalTextStyle.current.copy(
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
        ),
        LocalContentColor provides if (isDarkTheme) Color.White else Color.Black.copy(alpha = 0.8f)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            for (tab in tabs) {
                val alpha by animateFloatAsState(
                    targetValue = if (selectedTab == tabs.indexOf(tab)) 1f else .4f, label = ""
                )
                val scale by animateFloatAsState(
                    targetValue = if (selectedTab == tabs.indexOf(tab)) 1f else .98f,
                    visibilityThreshold = .000001f,
                    animationSpec = spring(
                        stiffness = Spring.StiffnessLow,
                        dampingRatio = Spring.DampingRatioMediumBouncy
                    ), label = ""
                )
                Column(
                    modifier = Modifier
                        .scale(scale)
                        .alpha(alpha)
                        .fillMaxHeight()
                        .weight(1f)
                        .pointerInput(Unit) {
                            detectTapGestures { onTabSelected(tab) }
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(imageVector = tab.icon, contentDescription = "tab ${tab.title}")
                    Text(text = tab.title)
                }
            }
        }
    }
}


interface CommonBottomBarTab {
    val title: String
    val icon: ImageVector
    val color: Color
}

data class BottomBarTab(
    override val title: String,
    override val icon: ImageVector,
    override val color: Color
) : CommonBottomBarTab

