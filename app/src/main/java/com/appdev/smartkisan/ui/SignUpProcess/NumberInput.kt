package com.appdev.smartkisan.ui.SignUpProcess

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.appdev.smartkisan.Actions.PhoneAuthAction
import com.appdev.smartkisan.R
import com.appdev.smartkisan.States.PhoneAuthState
import com.appdev.smartkisan.ViewModel.LoginViewModel
import com.appdev.smartkisan.ui.OtherComponents.CustomButton

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun NumberInputRoot(loginViewModel: LoginViewModel = hiltViewModel()) {
    NumberInput(loginViewModel.loginState, loginViewModel::onAction)
}

@Composable
fun NumberInput(loginState: PhoneAuthState, onAction: (PhoneAuthAction) -> Unit) {

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
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "OTP Verification",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 23.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "We will send to one-time password to this mobile number",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                )
                TextField(
                    value = loginState.phoneNumber,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() }) {
                            onAction(PhoneAuthAction.numebrChange(input))
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = Color(0xFFE4E7EE),
                        unfocusedContainerColor = Color(0xFFE4E7EE)
                    ),
                    placeholder = {
                        Text(
                            text = "Enter mobile number"
                        )
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
            CustomButton(
                onClick = { onAction(PhoneAuthAction.SendMeOtp) },
                text = "Get OTP", width = 1f
            )
        }
    }
}

