package com.appdev.smartkisan.ui.MainAppScreens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.location.LocationManagerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.appdev.smartkisan.Actions.HomeScreenActions
import com.appdev.smartkisan.R
import com.appdev.smartkisan.States.LocationPermissionState
import com.appdev.smartkisan.States.WeatherState
import com.appdev.smartkisan.Utils.SessionManagement
import com.appdev.smartkisan.Utils.WeatherDrawables
import com.appdev.smartkisan.ViewModel.HomeScreenViewModel
import com.appdev.smartkisan.ui.OtherComponents.CustomButton
import com.appdev.smartkisan.ui.navigation.Routes
import com.appdev.smartkisan.ui.theme.myGreen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeRoot(
    controller: NavHostController,
    homeScreenViewModel: HomeScreenViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val locationPermissionState = rememberPermissionState(
        permission = Manifest.permission.ACCESS_FINE_LOCATION
    )
    val weatherState by homeScreenViewModel.weatherState.collectAsState()

    HomeScreen(weatherState) { action ->
        when (action) {
            is HomeScreenActions.GoToChatBotScreen -> {
                controller.navigate(Routes.ChatBotScreen.route)
            }
            is HomeScreenActions.GoToNewsScreen -> {
                controller.navigate(Routes.NewsList.route)
            }

            is HomeScreenActions.GoToMapScreen->{
                if (locationPermissionState.status.isGranted) {
                    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                    val isLocationEnabled = LocationManagerCompat.isLocationEnabled(locationManager)
                    if (isLocationEnabled) {
                        controller.navigate(Routes.ShopsOnMap.route)
                    } else {
                        homeScreenViewModel.onAction(
                            HomeScreenActions.UpdateLocationPermission(
                                LocationPermissionState.LOCATION_DISABLED
                            )
                        )
                    }
                } else if (!locationPermissionState.status.isGranted &&
                    !locationPermissionState.status.shouldShowRationale
                ) {
                    homeScreenViewModel.onAction(
                        HomeScreenActions.UpdateLocationPermission(
                            LocationPermissionState.PERMANENTLY_DENIED
                        )
                    )
                } else {
                    locationPermissionState.launchPermissionRequest()
                }
            }


            is HomeScreenActions.RequestWeatherUpdate -> {
                if (locationPermissionState.status.isGranted) {
                    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                    val isLocationEnabled = LocationManagerCompat.isLocationEnabled(locationManager)
                    if (isLocationEnabled) {
                        homeScreenViewModel.onAction(HomeScreenActions.RequestCurrentLocation)
                    } else {
                        homeScreenViewModel.onAction(
                            HomeScreenActions.UpdateLocationPermission(
                                LocationPermissionState.LOCATION_DISABLED
                            )
                        )
                    }
                } else if (!locationPermissionState.status.isGranted &&
                    !locationPermissionState.status.shouldShowRationale
                ) {
                    homeScreenViewModel.onAction(
                        HomeScreenActions.UpdateLocationPermission(
                            LocationPermissionState.PERMANENTLY_DENIED
                        )
                    )
                } else {
                    locationPermissionState.launchPermissionRequest()
                }
            }

            else -> homeScreenViewModel.onAction(action)
        }
    }


    val locationSettingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        checkLocationEnabled(context, homeScreenViewModel::onAction)
    }


    // Show location dialog based on state
    if (weatherState.showLocationDialog) {
        val state = weatherState.locationPermissionState

        when (state) {
            LocationPermissionState.PERMANENTLY_DENIED -> {
                AlertDialog(
                    onDismissRequest = {
                        homeScreenViewModel.onAction(HomeScreenActions.DismissLocationDialog)
                    },
                    title = { Text("Location Permission Required") },
                    text = {
                        Text(
                            "To provide accurate weather information, we need access to your location. " +
                                    "Please grant location permission in app settings."
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                homeScreenViewModel.onAction(HomeScreenActions.DismissLocationDialog)
                                val intent =
                                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                        data = Uri.fromParts("package", context.packageName, null)
                                    }
                                context.startActivity(intent)
                            }
                        ) {
                            Text("Open Settings")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                homeScreenViewModel.onAction(HomeScreenActions.DismissLocationDialog)
                            }
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            }

            LocationPermissionState.LOCATION_DISABLED -> {
                AlertDialog(
                    onDismissRequest = {
                        homeScreenViewModel.onAction(HomeScreenActions.DismissLocationDialog)
                    },
                    title = { Text("Location Services Disabled") },
                    text = {
                        Text(
                            "To provide accurate weather information based on your location, " +
                                    "please enable location services."
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                homeScreenViewModel.onAction(HomeScreenActions.DismissLocationDialog)
                                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                                locationSettingsLauncher.launch(intent)
                            }
                        ) {
                            Text("Open Location Settings")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                homeScreenViewModel.onAction(HomeScreenActions.DismissLocationDialog)
                            }
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            }

            else -> { /* No dialog needed */
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(weatherState: WeatherState, onAction: (HomeScreenActions) -> Unit) {
    val context = LocalContext.current
    Scaffold(topBar = {
        TopAppBar(title = {
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = "Hello ${weatherState.userName} \uD83D\uDC4B",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }, actions = {
            Box(modifier = Modifier.padding(end = 12.dp)) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(weatherState.userImage)
                        .crossfade(true)
                        .build(),
                    contentDescription = "User Image",
                    placeholder = painterResource(R.drawable.farmer),
                    error = painterResource(R.drawable.farmer),
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(1.dp, Color.LightGray, CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
        }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent))
    }) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                WeatherCard(weatherState) {
                    onAction(HomeScreenActions.RequestWeatherUpdate)
                }
                ActionCard(
                    title = "Ask Kisan Assistant",
                    iconResId = R.drawable.chatbot,
                    iconContentDescription = "ChatBot",
                    onClick = { onAction(HomeScreenActions.GoToChatBotScreen) },
                    lightModeColor = myGreen
                )
                ActionCard(
                    title = "Agriculture News",
                    iconResId = R.drawable.news,
                    iconContentDescription = "news",
                    onClick = { onAction(HomeScreenActions.GoToNewsScreen) },
                    lightModeColor = myGreen
                )
                ActionCard(
                    title = "Shops Nearby your location",
                    iconResId = R.drawable.pin,
                    iconContentDescription = "shopsnearby",
                    onClick = { onAction(HomeScreenActions.GoToMapScreen) },
                    lightModeColor = myGreen
                )

            }
        }
    }

}

@Composable
fun ActionCard(
    modifier: Modifier = Modifier,
    title: String,
    iconResId: Int,
    iconContentDescription: String,
    onClick: () -> Unit,
    darkModeColor: Color = Color(0xFF114646),
    lightModeColor: Color
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSystemInDarkTheme()) darkModeColor else lightModeColor
        ),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                    colors = listOf(Color(0xFF66BB6A), Color(0xFF2E7D32))
                )), // Apply gradient here
            contentAlignment = Alignment.Center
        ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Image(
                painter = painterResource(id = iconResId),
                contentDescription = iconContentDescription,
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
        }
    }
}

@Composable
fun WeatherCard(
    weatherState: WeatherState,
    onRequestWeatherInfo: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        colors = CardDefaults.cardColors(containerColor = getWeatherCardBackgroundColor(weatherState.weatherData?.iconCode))
    ) {
        when {
            // When loading
            weatherState.isLoading -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            // When data is available
            weatherState.weatherData != null -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Row(modifier = Modifier.weight(1f)) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(5.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "${weatherState.weatherData.temperature}\t\u00B0",
                                fontSize = 40.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = textColorCalculation()
                            )
                            Text(
                                text = weatherState.weatherData.weatherDescription,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = textColorCalculation().copy(alpha = 0.8f),
                                textAlign = TextAlign.Start
                            )
                            Text(
                                text = weatherState.weatherData.location,
                                fontSize = 15.sp,
                                color = textColorCalculation()
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            WeatherIcon(iconCode = weatherState.weatherData.iconCode)
                        }
                    }
                }
            }

            // When no data is available - show statusMessage from state
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Use the centralized status message from the state
                    Text(
                        text = weatherState.statusMessage,
                        color = textColorCalculation(),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    CustomButton(
                        text = "Get Weather Info",
                        onClick = onRequestWeatherInfo,
                        modifier = Modifier,
                        height = 46.dp,
                        fontSize = 17.sp
                    )
                }
            }
        }
    }
}

@Composable
fun WeatherIcon(iconCode: String) {
    val drawableId by remember {
        mutableIntStateOf(WeatherDrawables.getDrawableForWeather(iconCode))
    }
    Image(
        painter = painterResource(id = drawableId),
        contentDescription = "Weather Condition",
        modifier = Modifier.size(110.dp)
    )
}

@Composable
fun getWeatherCardBackgroundColor(iconCode: String?): Color {
    // Determine background color based on weather condition and time of day
    return when {
        iconCode == null -> Color(0xB7FFE484) // Default
        iconCode.startsWith("01") -> Color(0xB7FFE484) // Clear sky
        iconCode.startsWith("02") || iconCode.startsWith("03") -> Color(0xB7E0E0E0) // Few clouds
        iconCode.startsWith("04") -> Color(0xB7D3D3D3) // Broken clouds
        iconCode.startsWith("09") || iconCode.startsWith("10") -> Color(0xB7ADD8E6) // Rain
        iconCode.startsWith("11") -> Color(0xB7A9A9A9) // Thunderstorm
        iconCode.startsWith("13") -> Color(0xB7F0F8FF) // Snow
        iconCode.startsWith("50") -> Color(0xB7D3D3D3) // Mist
        else -> Color(0xB7FFE484)
    }
}

// Keep the existing functions
fun textColorCalculation(): Color {
    return Color.Black
}

fun colorCalculation(): Color {
    return Color(0xB7FFE484)
}

private fun checkLocationEnabled(
    context: Context,
    onAction: (HomeScreenActions) -> Unit
) {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val isLocationEnabled = LocationManagerCompat.isLocationEnabled(locationManager)

    if (isLocationEnabled) {
        onAction(HomeScreenActions.UpdateLocationPermission(LocationPermissionState.GRANTED))
    } else {
        onAction(HomeScreenActions.UpdateLocationPermission(LocationPermissionState.LOCATION_DISABLED))
    }
}



