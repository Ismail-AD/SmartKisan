package com.appdev.smartkisan.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.appdev.smartkisan.presentation.feature.auth.login.LoginViewModel
import com.appdev.smartkisan.presentation.feature.auth.forgetpassword.ForgotPasswordRoot
import com.appdev.smartkisan.presentation.feature.auth.login.LoginRoot
import com.appdev.smartkisan.presentation.feature.auth.signup.OtpInputRoot
import com.appdev.smartkisan.presentation.feature.auth.forgetpassword.ResetPasswordConfirmationScreen
import com.appdev.smartkisan.presentation.feature.auth.forgetpassword.ResetPasswordRoot
import com.appdev.smartkisan.presentation.feature.auth.signup.SignUpRoot
import com.appdev.smartkisan.presentation.feature.auth.UserRoleSelection.UserTypeRoot
import com.appdev.smartkisan.presentation.feature.onBoarding.BoardingTemplate

@Composable
fun NavGraph(
    notInitialLaunch: Boolean,
    userType: String?,
    userId: String?,
    isSessionValid: Boolean,
    onBoardingDone: () -> Unit
) {
    val controller = rememberNavController()
    val loginViewModel: LoginViewModel = hiltViewModel()

    val initialRoute = if (userId != null && userType != null && isSessionValid) {
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
                    onBoardingDone()
                    controller.navigate(Routes.RoleSelect.route) {
                        popUpTo(controller.graph.startDestinationId)
                    }
                }
            }
            composable(route = Routes.RoleSelect.route) {
                UserTypeRoot(navigateToNext = {
                    controller.navigate(Routes.SignUp.route)
                }, loginViewModel = loginViewModel) {
                    controller.navigateUp()
                }
            }

            composable(route = Routes.PasswordReset.route) {
                ForgotPasswordRoot(
                    loginViewModel = loginViewModel,
                    navigateToLogin = {
                        controller.navigateUp()
                    },
                    navigateToResetPassword = {
                        // This should only navigate to reset password after email is sent
                        controller.navigate(Routes.ResetPasswordConfirmation.route)
                    }
                )
            }

            // Add a confirmation screen
            composable(route = Routes.ResetPasswordConfirmation.route) {
                ResetPasswordConfirmationScreen(
                    navigateToLogin = {
                        controller.navigate(Routes.Login.route) {
                            popUpTo(Routes.Login.route) { inclusive = true }
                        }
                    }
                )
            }


            composable(
                route = Routes.ResetPassword.route,
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern = "com.appdev.smartkisan://reset-password"
                    }
                )
            ) { backStackEntry ->
                // No need to extract token here, as we're handling it in MainActivity

                ResetPasswordRoot(
                    loginViewModel = loginViewModel,
                    navigateToLogin = {
                        controller.navigate(Routes.Login.route) {
                            popUpTo(Routes.Login.route) { inclusive = true }
                        }
                    }
                )
            }


            composable(route = Routes.Login.route) {
                LoginRoot(loginViewModel = loginViewModel, navigateToNext = {
                    when (loginViewModel.loginState.userType) {
                        "Farmer" -> {
                            controller.navigate(Routes.Main.route)
                        }

                        "Seller" -> {
                            controller.navigate(Routes.SellerMain.route)
                        }
                    }
                }, navigateToSignUp = {
                    controller.navigateUp()
                }, navigateUp = {
                    controller.navigateUp()
                }) {
                    controller.navigate(Routes.PasswordReset.route)
                }
            }


            composable(route = Routes.SignUp.route) {
                SignUpRoot(navigateToNext = {
                    controller.navigate(Routes.OtpInput.route)
                }, navigateToLogin = {
                    controller.navigate(Routes.Login.route)
                }, loginViewModel = loginViewModel) {
                    controller.navigateUp()
                }
            }

            composable(route = Routes.OtpInput.route) {
                OtpInputRoot(navigateToNext = {
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
                BaseScreen {
                    controller.navigate(Routes.Login.route) {
                        // Clear the entire back stack
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
            composable(route = Routes.SellerMain.route) {
                SellerBaseScreen {
                    // When logout is triggered from MainScreen, navigate back to Login
                    controller.navigate(Routes.Login.route) {
                        // Clear the entire back stack
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }
    }
}

fun getLaunchRoute(notInitialLaunch: Boolean): String {
    return if (notInitialLaunch) {
        Routes.RoleSelect.route
    } else {
        Routes.OnBoarding.route
    }
}
