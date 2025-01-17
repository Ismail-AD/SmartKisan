package com.appdev.smartkisan.Repository

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.appdev.smartkisan.Utils.ResultState
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Phone
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class Repository @Inject constructor(
    val firebaseAuth: FirebaseAuth,
    val supabaseClient: SupabaseClient
) {
    private lateinit var omVerificationCode: String


    fun signUpUserWithSupaBase(phoneNumber: String): Flow<ResultState<String>> =
        flow {
            Log.d("SupabaseRepository", "Starting signUpUserWithSupabase with phone: $phoneNumber")
            emit(ResultState.Loading)
            Log.d("SupabaseRepository", "ResultState.Loading emitted")

            try {
                val result = supabaseClient.auth.signUpWith(Phone) {
                    phone = phoneNumber
                    password = "default"
                }
                if (result != null) {
                    emit(ResultState.Success("Sign-up successful"))
                }
            } catch (e: Exception) {
                Log.e("SupabaseRepository", "Exception during sign-up: ${e.message}", e)
                emit(ResultState.Failure(e))
            }
        }

    fun verifyOtp(phoneNumber: String, otp: String): Flow<ResultState<String>> =
        flow {
            emit(ResultState.Loading)
            try {
                supabaseClient.auth.verifyPhoneOtp(type = OtpType.Phone.SMS, phone = phoneNumber, token = otp)
                emit(ResultState.Success("OTP verified successfully"))
            } catch (e: Exception) {
                Log.e("SupabaseRepository", "OTP verification failed: ${e.message}", e)
                emit(ResultState.Failure(e))
            }
        }


    fun signUpUser(phone: String, activity: Activity): Flow<ResultState<String>> = callbackFlow {
        Log.d("Repository", "Starting signUpUser with phone: $phone")
        trySend(ResultState.Loading)
        Log.d("Repository", "ResultState.Loading sent")

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                Log.d("Repository", "Verification completed with credential: $p0")
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                Log.e("Repository", "Verification failed with exception: ${p0.message}", p0)
                trySend(ResultState.Failure(p0))
            }

            override fun onCodeSent(
                verificationCode: String,
                p1: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(verificationCode, p1)
                Log.d("Repository", "OTP sent successfully. Verification code: $verificationCode")
                trySend(ResultState.Success("OTP Sent Successfully"))
                omVerificationCode = verificationCode
            }
        }

        Log.d("Repository", "Building PhoneAuthOptions")
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()

        Log.d("Repository", "Starting phone number verification")
        PhoneAuthProvider.verifyPhoneNumber(options)

        awaitClose {
            Log.d("Repository", "CallbackFlow closed")
            close()
        }
    }

    fun signWithCredential(otp: String): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        val credential = PhoneAuthProvider.getCredential(omVerificationCode, otp)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener {
                if (it.isSuccessful)
                    trySend(ResultState.Success("otp verified"))
            }.addOnFailureListener {
                trySend(ResultState.Failure(it))
            }
        awaitClose {
            close()
        }
    }

}