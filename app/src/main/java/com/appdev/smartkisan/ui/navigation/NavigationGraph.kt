package com.appdev.smartkisan.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.appdev.smartkisan.ViewModel.LoginViewModel
import com.appdev.smartkisan.ui.MainAppScreens.BaseScreen
import com.appdev.smartkisan.ui.SellerAppScreens.SellerBaseScreen
import com.appdev.smartkisan.ui.SignUpProcess.NumberInputRoot
import com.appdev.smartkisan.ui.SignUpProcess.OtpInputRoot
import com.appdev.smartkisan.ui.SignUpProcess.UserInfoInputRoot
import com.appdev.smartkisan.ui.SignUpProcess.UserTypeRoot
import com.appdev.smartkisan.ui.onBoarding.BoardingTemplate

@Composable
fun NavGraph(
    notInitialLaunch: Boolean,
    userType: String?,
    userId: String?,
    userAccessToken: String?,
    saveNewToken: (String) -> Unit
) {
    val controller = rememberNavController()
    val loginViewModel: LoginViewModel = hiltViewModel()

    val initialRoute = if (userId != null && userType != null) {
        userAccessToken?.let { token ->
            loginViewModel.refreshToken(token) { newtoken ->
                newtoken?.let { newToken ->
                    saveNewToken(newToken)
                }
            }
        }
        when (userType) {
            "Farmer" -> Routes.Main.route
            "Seller" -> Routes.SellerMain.route
            else -> getLaunchRoute(notInitialLaunch)
        }
    } else {
        getLaunchRoute(notInitialLaunch)
    }

    if (initialRoute != null) {
        NavHost(navController = controller,
            startDestination = initialRoute,
            enterTransition = {
                fadeIn(animationSpec = tween(500, delayMillis = 90))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(90))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(500, delayMillis = 90))
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(90))
            }) {
            composable(route = Routes.OnBoarding.route) {
                BoardingTemplate {
                    controller.navigate(Routes.RoleSelect.route) {
                        popUpTo(controller.graph.startDestinationId)
                    }
                }
            }
            composable(route = Routes.RoleSelect.route) {
                UserTypeRoot(navigateToNext = {
                    controller.navigate(Routes.NumberInput.route)
                }, loginViewModel = loginViewModel) {
                    controller.navigateUp()
                }
            }
            composable(route = Routes.NumberInput.route) {

                NumberInputRoot(navigateToNext = {
                    controller.navigate(Routes.OtpInput.route)
                }, loginViewModel = loginViewModel) {
                    controller.navigateUp()
                }
            }
            composable(
                route = Routes.OtpInput.route
            ) {
                OtpInputRoot(navigateToNext = {
                    controller.navigate(Routes.UserInfo.route)
                }, loginViewModel = loginViewModel) {
                    controller.navigateUp()
                }
            }
            composable(route = Routes.UserInfo.route) {
                UserInfoInputRoot(navigateToNext = {
                    when (loginViewModel.loginState.userType) {
                        "Farmer" -> {
                            controller.navigate(Routes.Main.route)
                        }

                        "Seller" -> {
                            controller.navigate(Routes.SellerMain.route)
                        }
                    }
                }, loginViewModel = loginViewModel) {
                    controller.navigateUp()
                }
            }
            composable(route = Routes.Main.route) {
                BaseScreen()
            }
            composable(route = Routes.SellerMain.route) {
                SellerBaseScreen()
            }
        }
    }
}

fun getLaunchRoute(notInitialLaunch: Boolean): String {
    return if (notInitialLaunch) {
        Routes.NumberInput.route
    } else {
        Routes.OnBoarding.route
    }
}
