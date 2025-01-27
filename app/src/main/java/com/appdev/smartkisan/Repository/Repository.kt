package com.appdev.smartkisan.Repository

import android.net.Uri
import android.util.Log
import com.appdev.smartkisan.Room.Dao.UserInfoDao
import com.appdev.smartkisan.Utils.ResultState
import com.appdev.smartkisan.data.UserEntity
import com.google.firebase.auth.FirebaseAuth
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Phone
import io.github.jan.supabase.auth.user.UserSession
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class Repository @Inject constructor(
    val firebaseAuth: FirebaseAuth,
    val supabaseClient: SupabaseClient,
    val userInfoDao: UserInfoDao
) {
    private val profileImageBucketId = "profileImages"
    private val profileImageFolderPath = "public/1cp17k1_1"


    suspend fun refreshUserInfo(accessToken: String?, saveNewToken: () -> Unit) {
        withContext(Dispatchers.IO) {
            if (accessToken != null) {
                supabaseClient.auth.retrieveUser(accessToken)
                supabaseClient.auth.refreshCurrentSession()
                saveNewToken()
            }
        }
    }


    suspend fun fetchUserInfo(): Flow<ResultState<UserEntity>> = flow {
        getCurrentUserId()?.let { uid ->
            emit(ResultState.Loading)
            try {
                val user = supabaseClient.from("users").select {
                    filter {
                        eq("id", uid)
                    }
                }.decodeSingle<UserEntity>()
                emit(ResultState.Success(user))
            } catch (e: Exception) {
                emit(ResultState.Failure(e))
            }
        }
    }

    fun signUpUserWithSupaBase(phoneNumber: String): Flow<ResultState<String>> =
        flow {
            emit(ResultState.Loading)
            try {
                val result = supabaseClient.auth.signUpWith(Phone) {
                    phone = phoneNumber
                    password = "default"
                }
                if (result != null) {
                    emit(ResultState.Success("Sign-up successful"))
                }
            } catch (e: Exception) {
                emit(ResultState.Failure(e))
            }
        }

    fun verifyOtp(phoneNumber: String, otp: String): Flow<ResultState<String>> =
        flow {
            emit(ResultState.Loading)

            try {
                supabaseClient.auth.verifyPhoneOtp(
                    type = OtpType.Phone.SMS,
                    phone = phoneNumber,
                    token = otp
                )
                emit(ResultState.Success("OTP verified successfully"))
            } catch (e: Exception) {
                emit(ResultState.Failure(e))
            }
        }

    suspend fun imageUploading(imageUri: Uri, imageBytes: ByteArray): String {
        return withContext(Dispatchers.IO) {
            try {
                val extension = when {
                    // Try to get from Uri
                    imageUri.lastPathSegment?.contains(".") == true ->
                        imageUri.lastPathSegment?.substringAfterLast('.')?.lowercase()
                    // Fallback to detecting from bytes
                    else -> when {
                        imageBytes.size >= 2 && imageBytes[0] == 0xFF.toByte() && imageBytes[1] == 0xD8.toByte() -> "jpg"
                        imageBytes.size >= 8 && String(
                            imageBytes.take(8).toByteArray()
                        ) == "PNG\r\n\u001a\n" -> "png"

                        else -> "jpg" // Default fallback
                    }
                } ?: "jpg"

                // Validate extension
                val safeExtension = when (extension.lowercase()) {
                    "jpg", "jpeg", "png", "gif" -> extension
                    else -> "jpg"
                }
                val fileName = "${UUID.randomUUID()}.$safeExtension"
                val fullPath = "$profileImageFolderPath/$fileName"
                val bucket = supabaseClient.storage.from(profileImageBucketId)
                bucket.upload(
                    path = fullPath,
                    data = imageBytes
                ) {
                    upsert = false
                }
                bucket.publicUrl(fullPath)
            } catch (e: Exception) {
                throw Exception("Failed to upload image: ${e.message}")
            }
        }
    }


    fun insertUser(
        userEntity: UserEntity,
        imageByteArray: ByteArray?,
        imageUri: Uri?
    ): Flow<ResultState<String>> =
        flow {
            getCurrentUserId()?.let { uid ->
                emit(ResultState.Loading)
                try {
                    val imageUrl = if (imageByteArray != null && imageUri != null) {
                        imageUploading(imageUri, imageByteArray)
                    } else {
                        ""
                    }
                    userEntity.imageUrl = imageUrl
                    userEntity.id = uid
                    supabaseClient.from("users").insert(userEntity)
                    emit(ResultState.Success("Profile created successfully"))
                } catch (e: Exception) {
                    Log.e("SupabaseRepository", "Profile creation failed: ${e.message}", e)
                    emit(ResultState.Failure(e))
                }
            }
        }

    private fun getCurrentUserId(): String? {
        return supabaseClient.auth.currentUserOrNull()?.id
    }

}