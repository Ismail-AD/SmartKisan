package com.appdev.smartkisan.ui.MainAppScreens

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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.appdev.smartkisan.ui.navigation.Routes
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeChild

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseScreen() {
    val controller = rememberNavController()
    val hazeState = remember { HazeState() }

    val currentRoute = controller.currentBackStackEntryAsState().value?.destination?.route
    val hideBottomBarRoutes = listOf(Routes.ProductDetailScreen.route)

    Scaffold(bottomBar = {
        if (currentRoute !in hideBottomBarRoutes) {
            GlassmorphicBottomNavigation(hazeState, navController = controller)
        }
    }) { innerPadding ->
        NavHost(
            navController = controller, startDestination = Routes.HomeScreen.route,
            Modifier.padding(innerPadding)
        ) {
            composable(Routes.HomeScreen.route) { Home() }
            composable(Routes.AccountScreen.route) { Account() }
            composable(Routes.MarketPlace.route) { MarketPlace(controller) }
            composable(Routes.PlantDisease.route) { PlantDisease() }
            composable(route = Routes.ProductDetailScreen.route) {
                ProductDetails(controller)
            }
        }
    }
}

@Composable
fun GlassmorphicBottomNavigation(hazeState: HazeState, navController: NavHostController) {
var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    val isDarkTheme = isSystemInDarkTheme()
    val dayColors = if (isDarkTheme)
        listOf(
            Color.White.copy(alpha = .8f),
            Color.White.copy(alpha = .35f),
        )
    else listOf(
        Color(0xFF8F8F8F).copy(alpha = 0.35f), // Darker border for light mode
        Color(0xFF8F8F8F).copy(alpha = 0.2f)  // Subtle gradient
    )
    Box(
        modifier = Modifier
            .padding(vertical = 24.dp, horizontal = 20.dp)
            .fillMaxWidth()
            .height(64.dp)
            .hazeChild(state = hazeState, shape = CircleShape)
            .border(
                width = if (isDarkTheme) Dp.Hairline else (1.5).dp,
                brush = Brush.verticalGradient(
                    colors =
                    dayColors,
                ),
                shape = CircleShape
            )
    ) {
        val tabs = listOf(
            BottomBarTab.Home(isDarkTheme),
            BottomBarTab.PlantDisease(isDarkTheme),
            BottomBarTab.Profile(isDarkTheme),
            BottomBarTab.Settings(isDarkTheme),
        )

        BottomBarTabs(
            isDarkTheme,
            tabs,
            selectedTab = selectedTabIndex
        ) {
            selectedTabIndex = tabs.indexOf(it)
            when (it) {
                is BottomBarTab.Home -> navController.navigate(Routes.HomeScreen.route)
                is BottomBarTab.PlantDisease -> navController.navigate(Routes.PlantDisease.route)
                is BottomBarTab.Profile -> navController.navigate(Routes.MarketPlace.route)
                is BottomBarTab.Settings -> navController.navigate(Routes.AccountScreen.route)
            }
        }

        val animatedSelectedTabIndex by animateFloatAsState(
            targetValue = selectedTabIndex.toFloat(), label = "animatedSelectedTabIndex",
            animationSpec = spring(
                stiffness = Spring.StiffnessLow,
                dampingRatio = Spring.DampingRatioLowBouncy,
            )
        )

        val animatedColor by animateColorAsState(
            targetValue = tabs[selectedTabIndex].color,
            label = "animatedColor",
            animationSpec = spring(
                stiffness = Spring.StiffnessLow,
            )
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

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
        ) {
            val path = Path().apply {
                addRoundRect(RoundRect(size.toRect(), CornerRadius(size.height)))
            }
            val length = PathMeasure().apply { setPath(path, false) }.length

            val tabWidth = size.width / tabs.size
            drawPath(
                path,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        animatedColor.copy(alpha = 0f),
                        animatedColor.copy(alpha = 1f),
                        animatedColor.copy(alpha = 1f),
                        animatedColor.copy(alpha = 0f),
                    ),
                    startX = tabWidth * animatedSelectedTabIndex,
                    endX = tabWidth * (animatedSelectedTabIndex + 1),
                ),
                style = Stroke(
                    width = 6f,
                    pathEffect = PathEffect.dashPathEffect(
                        intervals = floatArrayOf(length / 2, length)
                    )
                )
            )
        }
    }
}


@Composable
fun BottomBarTabs(
    isDarkTheme: Boolean,
    tabs: List<BottomBarTab>,
    selectedTab: Int,
    onTabSelected: (BottomBarTab) -> Unit,
) {
    CompositionLocalProvider(
        LocalTextStyle provides LocalTextStyle.current.copy(
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
        ),
        LocalContentColor provides if (isDarkTheme) Color.White else Color.Black.copy(alpha = 0.8f)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
        ) {
            for (tab in tabs) {
                val alpha by animateFloatAsState(
                    targetValue = if (selectedTab == tabs.indexOf(tab)) 1f else .4f,
                    label = "alpha"
                )
                val scale by animateFloatAsState(
                    targetValue = if (selectedTab == tabs.indexOf(tab)) 1f else .98f,
                    visibilityThreshold = .000001f,
                    animationSpec = spring(
                        stiffness = Spring.StiffnessLow,
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                    ),
                    label = "scale"
                )
                Column(
                    modifier = Modifier
                        .scale(scale)
                        .alpha(alpha)
                        .fillMaxHeight()
                        .weight(1f)
                        .pointerInput(Unit) {
                            detectTapGestures {
                                onTabSelected(tab)
                            }
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Icon(imageVector = tab.icon, contentDescription = "tab ${tab.title}")
                    Text(text = tab.title)
                }
            }
        }
    }
}

sealed class BottomBarTab(val title: String, val icon: ImageVector, val color: Color) {
    class Home(isDarkTheme: Boolean) : BottomBarTab(
        title = "Home",
        icon = Icons.Rounded.Home,
        color = if (isDarkTheme) Color(0xFFFA6FFF) else Color(0xFFE64A19) // Stronger pink
    )

    class PlantDisease(isDarkTheme: Boolean) : BottomBarTab(
        title = "Diagnosis",
        icon = Icons.Rounded.Build,
        color = if (isDarkTheme) Color(0xFFADFF64) else Color(0xFF2196F3) // Brighter blue
    )

    class Profile(isDarkTheme: Boolean) : BottomBarTab(
        title = "Market",
        icon = Icons.Rounded.ShoppingCart,
        color = if (isDarkTheme) Color(0xFFFFA574) else Color(0xFFE91E63)  // More vibrant orange
    )

    class Settings(isDarkTheme: Boolean) : BottomBarTab(
        title = "Account",
        icon = Icons.Rounded.Person,
        color = if (isDarkTheme) Color(0xFFADFF64) else Color(0xFF4CAF50) // Deeper green
    )


}


//@Composable
//fun BottomNavigatorBar(navController: NavHostController){
//     BottomNavigation()
//}