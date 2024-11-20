package com.appdev.smartkisan.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.appdev.smartkisan.ui.MainAppScreens.BaseScreen
import com.appdev.smartkisan.ui.SellerAppScreens.SellerBaseScreen
import com.appdev.smartkisan.ui.SignUpProcess.NumberInput
import com.appdev.smartkisan.ui.SignUpProcess.OtpInput
import com.appdev.smartkisan.ui.SignUpProcess.UserInfo
import com.appdev.smartkisan.ui.SignUpProcess.UserSelection
import com.appdev.smartkisan.ui.onBoarding.BoardingTemplate

@Composable
fun NavGraph() {
    val controller = rememberNavController()
    NavHost(navController = controller, startDestination = Routes.OnBoarding.route) {
        composable(route = Routes.OnBoarding.route) {
            BoardingTemplate {
                controller.navigate(Routes.RoleSelect.route) {
                    popUpTo(controller.graph.startDestinationId)
                }
            }
        }
        composable(route = Routes.RoleSelect.route) {
            UserSelection {
                controller.navigate(Routes.NumberInput.route)
            }
        }
        composable(route = Routes.NumberInput.route) {
            NumberInput {
                controller.navigate(Routes.OtpInput.route)
            }
        }
        composable(route = Routes.OtpInput.route) {
            OtpInput() {
                controller.navigate(Routes.UserInfo.route)
            }
        }
        composable(route = Routes.UserInfo.route) {
          UserInfo (controller) {
                controller.navigate(Routes.SellerMain.route) {
                    popUpTo(controller.graph.startDestinationId)
                }
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