package com.appdev.smartkisan

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.appdev.smartkisan.Utils.SessionManagement
import com.appdev.smartkisan.Utils.SessionManagement.isSessionValid
import com.appdev.smartkisan.ui.navigation.NavGraph
import com.appdev.smartkisan.ui.theme.SmartKisanTheme
import dagger.hilt.android.AndroidEntryPoint
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toLocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var supabaseClient: SupabaseClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val notInitialLaunch = SessionManagement.isOnboardingCompleted(this)
        val userType = SessionManagement.getUserType(this)
        val userId = SessionManagement.getUserId(this)
        lifecycleScope.launch {
            val isSessionValid = refreshSessionIfNeeded()
            setContent {
                SmartKisanTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        NavGraph(notInitialLaunch, userType, userId, isSessionValid)
                    }
                }
            }
        }

    }

    private suspend fun refreshSessionIfNeeded(): Boolean {
        if (!isSessionValid(this) && SessionManagement.getRefreshToken(this) != null) {
            try {
                val getRefreshToken = SessionManagement.getRefreshToken(this)

                val session =
                    supabaseClient.auth.refreshSession(getRefreshToken!!)

                SessionManagement.saveSession(
                    context = this,
                    accessToken = session.accessToken,
                    refreshToken = session.refreshToken,
                    expiresAt = session.expiresAt.epochSeconds,
                    userId = session.user!!.id,
                    userEmail = session.user!!.email ?: ""
                )
                return true
            } catch (e: Exception) {
                SessionManagement.clearSession(this)
                return false
            }
        }
        return isSessionValid(this)
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