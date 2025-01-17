package com.appdev.smartkisan.ui.SignUpProcess

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.appdev.smartkisan.Actions.PhoneAuthAction
import com.appdev.smartkisan.R
import com.appdev.smartkisan.States.UserAuthState
import com.appdev.smartkisan.ViewModel.LoginViewModel
import com.appdev.smartkisan.ui.OtherComponents.CustomButton
import com.talhafaki.composablesweettoast.util.SweetToastUtil.SweetError


@Composable
fun UserInfoInputRoot(
    navigateToNext: () -> Unit,
    loginViewModel: LoginViewModel,
    navigateUp: () -> Unit
) {
    UserInfo(loginViewModel.loginState, onAction = { action ->
        when (action) {
            is PhoneAuthAction.NextScreen -> {
                navigateToNext()
            }

            is PhoneAuthAction.GoBack -> {
                navigateUp()
            }

            else -> loginViewModel.onAction(action)
        }
    })
}

@Composable
fun UserInfo(loginState: UserAuthState, onAction: (PhoneAuthAction) -> Unit) {

    var showToastState by remember { mutableStateOf(Pair(false, "")) }


    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            onAction.invoke(PhoneAuthAction.SelectedImageUri(uri))
//            selectedImageUri = uri
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .padding(horizontal = 18.dp)
                .fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(painter = painterResource(id = R.drawable.tempicon), contentDescription = "")

            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Set Up Your Profile",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 23.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "You are almost done!",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Box(modifier = Modifier.padding(top = 15.dp)) {
                    Box(
                        modifier = Modifier
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFFFF9933), Color(0xFFFF5533))
                                ), RoundedCornerShape(100.dp)
                            )
                            .clip(CircleShape)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(loginState.profileImage)
                                .build(),
                            contentDescription = "Profile Image",
                            placeholder = painterResource(R.drawable.farmer),
                            error = painterResource(R.drawable.farmer),
                            modifier = Modifier
                                .size(120.dp)
                                .padding(),
                            contentScale = ContentScale.Crop
                        )
                    }


                    IconButton(
                        onClick = {
                            launcher.launch("image/*")
                        }, modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(40.dp)
                    ) {
                        Card(
                            shape = CircleShape, colors = CardDefaults.cardColors(
                                containerColor = Color(
                                    0xFF83E978
                                )
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "",
                                tint = Color.Black.copy(alpha = 0.9f),
                                modifier = Modifier.size(27.dp)
                            )
                        }
                    }
                }
                TextField(
                    value = loginState.userName,
                    onValueChange = { input ->
                        onAction.invoke(PhoneAuthAction.Username(username = input))
                    },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = Color(0xFFE4E7EE),
                        unfocusedContainerColor = Color(0xFFE4E7EE),
                    ),
                    placeholder = {
                        Text(
                            text = "Enter your name"
                        )
                    }, modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 15.dp), singleLine = true
                )
            }

            CustomButton(
                onClick = {
                    if (loginState.userName.trim().isNotEmpty()) {
                        onAction.invoke(PhoneAuthAction.SaveUserProfile)
                    } else {
                        showToastState = Pair(true, "Username should not be empty!")
                    }
                },
                text = "Continue", // Changed button text to be more context-specific
                width = 1f
            )
        }


        if (showToastState.first) {
            SweetError(
                message = showToastState.second,
                duration = Toast.LENGTH_SHORT,
                padding = PaddingValues(top = 16.dp),
                contentAlignment = Alignment.TopCenter
            )
            showToastState = Pair(false, "")

        }
    }
}