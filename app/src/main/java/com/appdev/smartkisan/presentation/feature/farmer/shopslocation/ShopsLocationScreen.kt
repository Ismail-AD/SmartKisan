package com.appdev.smartkisan.presentation.feature.farmer.shopslocation

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.appdev.smartkisan.R
import com.appdev.smartkisan.domain.model.UserEntity
import com.appdev.smartkisan.presentation.Resuable.CustomButton
import com.appdev.smartkisan.presentation.Resuable.DotsLoading
import com.appdev.smartkisan.presentation.theme.myGreen
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapRootScreen(
    navController: NavHostController,
    mapViewModel: MapViewModel = hiltViewModel()
) {
    val state by mapViewModel.state.collectAsStateWithLifecycle()

    MapScreen(
        state = state,
        onAction = { action ->
            mapViewModel.onAction(action)
        },
        onBackPressed = { navController.navigateUp() }
    )
}

@Composable
fun MapScreen(
    state: MapState,
    onAction: (MapActions) -> Unit,
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (state.isLoading) {
            // Show loading indicator
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (state.errorMessage != null) {
            // Show error message
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = state.errorMessage,
                        color = Color.Red,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                    Button(onClick = { onAction(MapActions.LoadSellersData) }) {
                        Text("Retry")
                    }
                }
            }
        } else {
            // Find a seller to center on if available, otherwise use user location
            val mapCenter = if (state.userLocation != null) {
                LatLng(state.userLocation.latitude, state.userLocation.longitude)
            } else if (state.sellers.isNotEmpty()) {
                state.sellers.firstOrNull { it.latitude != 0.0 && it.longitude != 0.0 }?.let {
                    LatLng(it.latitude, it.longitude)
                } ?: LatLng(20.5937, 78.9629) // Default position (center of India)
            } else {
                LatLng(20.5937, 78.9629) // Default position (center of India)
            }

            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(mapCenter, 10f)
            }

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = true,
                    myLocationButtonEnabled = true
                )
            ) {
                // Add markers for all sellers
                state.sellers.forEach { seller ->
                    if (seller.latitude != 0.0 && seller.longitude != 0.0) {
                        Marker(
                            state = MarkerState(
                                position = LatLng(
                                    seller.latitude,
                                    seller.longitude
                                )
                            ),
                            title = seller.shopName.ifEmpty { "Shop" },
                            snippet = "Click for details",
                            onClick = {
                                onAction(MapActions.SelectSeller(seller))
                                true
                            }
                        )
                    }
                }
            }
        }

        // Back button with semi-transparent background
        Box(
            modifier = Modifier
                .padding(16.dp)
                .size(40.dp)
                .clip(CircleShape) // This ensures ripple stays inside the circle
                .background(
                    color = Color.Black.copy(alpha = 0.5f),
                    shape = CircleShape
                )
                .clickable { onBackPressed() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }


        // Show "Nearby Shops" title with semi-transparent background
        Box(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopCenter)
                .background(
                    color = Color.Black.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Nearby Shops",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
        if (state.showSellerDetailsDialog) {
            SellerDetailsDialog(
                sellerMetaData = state.selectedSeller,
                sellerInfo = state.selectedSellerInfo,
                isLoading = state.isSellerInfoLoading,
                onContactClick = { contact ->
                    val dialIntent = Intent(
                        Intent.ACTION_DIAL,
                        Uri.parse("tel:$contact")
                    )
                    context.startActivity(dialIntent)
                },
                onDismiss = { onAction(MapActions.DismissSellerDetailsDialog) }
            )
        }
    }
}

@Composable
fun SellerDetailsDialog(
    sellerMetaData: com.appdev.smartkisan.domain.model.SellerMetaData?,
    sellerInfo: UserEntity?,
    isLoading: Boolean,
    onContactClick: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
            )
        ) {
            Box { // <-- new container
                // 1. Close icon
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close dialog"
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (isLoading) {
                        DotsLoading(
                            modifier = Modifier
                                .height(150.dp)
                                .fillMaxWidth()
                        )

                        Text(
                            text = "Loading shop details...",
                            fontWeight = FontWeight.Medium
                        )
                    } else {
                        // Shop name
                        Text(
                            text = sellerMetaData?.shopName?.ifEmpty { "Shop" } ?: "Shop",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = myGreen, modifier = Modifier.padding(top = 20.dp)
                        )

                        // Shop owner image
                        if (sellerInfo != null) {
                            val context = LocalContext.current
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(sellerInfo.imageUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Shop Owner",
                                placeholder = painterResource(R.drawable.farmer),
                                error = painterResource(R.drawable.farmer),
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, myGreen, CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }

                        // Owner name
                        Text(
                            text = "Owner: ${sellerInfo?.name ?: "Unknown"}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        // Contact info
                        sellerMetaData?.contact?.let { contact ->
                            if (contact.isNotEmpty()) {
                                CustomButton(
                                    onClick = {
                                        onContactClick(contact)
                                    },
                                    text = "Contact: $contact"
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}