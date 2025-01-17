package com.appdev.smartkisan.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.appdev.smartkisan.ViewModel.LoginViewModel
import com.appdev.smartkisan.ui.MainAppScreens.BaseScreen
import com.appdev.smartkisan.ui.SellerAppScreens.SellerBaseScreen
import com.appdev.smartkisan.ui.SignUpProcess.NumberInputRoot
import com.appdev.smartkisan.ui.SignUpProcess.OtpInput
import com.appdev.smartkisan.ui.SignUpProcess.OtpInputRoot
import com.appdev.smartkisan.ui.SignUpProcess.UserInfo
import com.appdev.smartkisan.ui.SignUpProcess.UserInfoInputRoot
import com.appdev.smartkisan.ui.SignUpProcess.UserSelection
import com.appdev.smartkisan.ui.SignUpProcess.UserTypeRoot
import com.appdev.smartkisan.ui.onBoarding.BoardingTemplate

@Composable
fun NavGraph() {
    val controller = rememberNavController()
    val loginViewModel: LoginViewModel = hiltViewModel()
    NavHost(navController = controller,
        startDestination = Routes.OnBoarding.route,
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