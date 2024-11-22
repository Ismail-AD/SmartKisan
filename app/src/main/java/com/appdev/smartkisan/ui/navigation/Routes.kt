package com.appdev.smartkisan.ui.navigation

sealed class Routes(val route: String) {
    object OnBoarding : Routes("OnBoarding_Screen")
    object RoleSelect : Routes("RoleSelect_Screen")
    object NumberInput : Routes("NumberInput_Screen")
    object OtpInput : Routes("OtpInput_Screen")
    object HomeScreen : Routes("Home_Screen")
    object AccountScreen : Routes("Account_Screen")
    object MarketPlace : Routes("MarketPlace_Screen")
    object PlantDisease : Routes("PlantDisease_Screen")
    object DiagnosisResult : Routes("DiagnosisResult_Screen")
    object UserInfo : Routes("UserInfo_Screen")
    object ChatBotScreen : Routes("ChatBot_Screen")
    object ProductDetailScreen : Routes("ProductDetail_Screen")
    object Main : Routes("Main_Screen")
    object SellerMain : Routes("SellerMain_Screen")
    object SellerHomeScreen : Routes("SellerHome_Screen")
    object SellerInboxScreen : Routes("SellerInbox_Screen")
    object ChatInDetailScreen : Routes("ChatDetail_Screen")
    object SellerAccountScreen : Routes("SellerAccount_Screen")
}

