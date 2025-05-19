package com.appdev.smartkisan

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.appdev.smartkisan.presentation.feature.auth.login.UserAuthAction
import com.appdev.smartkisan.Utils.SessionManagement
import com.appdev.smartkisan.presentation.feature.auth.login.LoginViewModel
import com.appdev.smartkisan.presentation.navigation.NavGraph
import com.appdev.smartkisan.presentation.theme.SmartKisanTheme
import dagger.hilt.android.AndroidEntryPoint
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var supabaseClient: SupabaseClient

    private val loginViewModel: LoginViewModel by viewModels<LoginViewModel>()

    @Inject
    lateinit var sessionManagement: SessionManagement
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent?.data?.let { uri ->
            handleDeepLink(uri)
        }


        val notInitialLaunch = sessionManagement.isOnboardingCompleted()
        val userType = sessionManagement.getUserType()
        val userId = sessionManagement.getUserId()
        lifecycleScope.launch {
            val isSessionValid = refreshSessionIfNeeded()
            setContent {
                SmartKisanTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        NavGraph(notInitialLaunch, userType, userId, isSessionValid) {
                            sessionManagement.setOnboardingCompleted(true)
                        }
                    }
                }
            }
        }

    }

    private fun handleDeepLink(uri: android.net.Uri) {
        if (uri.scheme == "com.appdev.smartkisan" && uri.host == "reset-password") {
            val fragment = uri.fragment ?: return

            // parse everything into a Map<String,String>
            val tokenMap = fragment
                .split("&")
                .map { it.split("=", limit = 2) }
                .filter { it.size == 2 }
                .associate { it[0] to it[1] }

            val accessToken = tokenMap["access_token"]
            val refreshToken = tokenMap["refresh_token"]
            val expiresIn = tokenMap["expires_in"]?.toIntOrNull()
            val tokenType = tokenMap["token_type"]     // <- new
            val type = tokenMap["type"] ?: "recovery"     // <- new


            if (accessToken != null && tokenType != null) {
                loginViewModel.onAction(
                    UserAuthAction.SetResetToken(
                        token = accessToken,
                        refreshToken = refreshToken,
                        expiresIn = expiresIn,
                        type = type,
                        resetTokenType = tokenType      // <- pass it through
                    )
                )
            }
        }
    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.data?.let { uri ->
            handleDeepLink(uri)
        }
    }


    private suspend fun refreshSessionIfNeeded(): Boolean {
        if (!sessionManagement.isSessionValid() && sessionManagement.getRefreshToken() != null) {
            try {
                val getRefreshToken = sessionManagement.getRefreshToken()

                val session =
                    supabaseClient.auth.refreshSession(getRefreshToken!!)

                sessionManagement.saveSession(
                    accessToken = session.accessToken,
                    refreshToken = session.refreshToken,
                    expiresAt = session.expiresAt.epochSeconds,
                    userId = session.user!!.id,
                    userEmail = session.user!!.email ?: ""
                )
                return true
            } catch (e: Exception) {
                sessionManagement.clearSession()
                return false
            }
        }
        return sessionManagement.isSessionValid()
    }


}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SmartKisanTheme {
        Greeting("Android")
    }
}