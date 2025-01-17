package com.appdev.smartkisan.ui.SignUpProcess

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.appdev.smartkisan.Actions.PhoneAuthAction
import com.appdev.smartkisan.R
import com.appdev.smartkisan.States.UserAuthState
import com.appdev.smartkisan.ViewModel.LoginViewModel
import com.appdev.smartkisan.ui.OtherComponents.CustomButton
import com.appdev.smartkisan.ui.OtherComponents.OtpTextField


@Composable
fun OtpInputRoot(
    navigateToNext: () -> Unit,
    loginViewModel: LoginViewModel,
    navigateUp: () -> Unit
) {
    Log.d("CHKMEA", loginViewModel.loginState.phoneNumber)
    OtpInput(
        number = loginViewModel.loginState.countryCode + loginViewModel.loginState.phoneNumber,
        loginViewModel.loginState,
        onAction = { action ->
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
fun OtpInput(number: String, loginState: UserAuthState, onAction: (PhoneAuthAction) -> Unit) {

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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Enter the OTP sent to",
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = number,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }


                OtpTextField(
                    otpText = loginState.otp,
                    onOtpTextChange = { value, otpInputFilled ->
                        onAction(PhoneAuthAction.otpChange(value))
                    }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Don't receive the OTP ? ",
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Resend OTP",
                        fontSize = 16.sp, textAlign = TextAlign.Center, color = Color(0xFF9747FF)
                    )
                }

            }
            CustomButton(
                onClick = { onAction(PhoneAuthAction.VerifyOtp(number = number, loginState.otp)) },
                text = "Verify", width = 1f
            )
        }
    }
}