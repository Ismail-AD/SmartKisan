package com.appdev.smartkisan.data.repository

import android.net.Uri
import android.util.Log
import com.appdev.smartkisan.data.local.db.Dao.UserInfoDao
import com.appdev.smartkisan.Utils.ResultState
import com.appdev.smartkisan.domain.model.UserEntity
import com.google.firebase.auth.FirebaseAuth
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserSession
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
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

    private val productImageBucketId = "productImages"
    private val productImageFolderPath = "public/cel5c7_0"

    fun getAllSellersWithLocations(): Flow<ResultState<List<com.appdev.smartkisan.domain.model.SellerMetaData>>> =
        flow {
            emit(ResultState.Loading)
            try {
                val sellerMetaDataList = supabaseClient
                    .from("sellersData")
                    .select()
                    .decodeList<com.appdev.smartkisan.domain.model.SellerMetaData>()
                    .filter { it.latitude != 0.0 && it.longitude != 0.0 }

                emit(ResultState.Success(sellerMetaDataList))
            } catch (e: Exception) {
                Log.e("Repository", "Failed to fetch sellers metadata: ${e.message}")
                emit(ResultState.Failure(e))
            }
        }

    fun getMedicineProductsForDisease(diseaseName: String): Flow<ResultState<List<com.appdev.smartkisan.domain.model.Product>>> =
        flow {
            emit(ResultState.Loading)
            try {
                val allProducts = supabaseClient.from("products").select {
                    filter {
                        eq("category", "Medicine")
                    }
                }.decodeList<com.appdev.smartkisan.domain.model.Product>()

                val matchingProducts = allProducts.filter { product ->
                    product.targetPestsOrDiseases?.contains(diseaseName) == true ||
                            product.targetPestsOrDiseases?.any { target ->
                                diseaseName.contains(target.trim(), ignoreCase = true)
                            } == true
                }

                emit(ResultState.Success(matchingProducts))
            } catch (e: Exception) {
                Log.e(
                    "SupabaseRepository",
                    "Failed to fetch disease-specific products: ${e.message}",
                    e
                )
                emit(ResultState.Failure(e))
            }
        }


    // Add this function to fetch a specific user by ID
    fun fetchUserById(userId: String): Flow<ResultState<UserEntity>> = flow {
        emit(ResultState.Loading)
        try {
            val user = supabaseClient.from("users").select {
                filter {
                    eq("id", userId)
                }
            }.decodeSingle<UserEntity>()
            emit(ResultState.Success(user))
        } catch (e: Exception) {
            Log.e("Repository", "Failed to fetch user by ID: ${e.message}")
            emit(ResultState.Failure(e))
        }
    }

    fun getAllProducts(): Flow<ResultState<List<com.appdev.smartkisan.domain.model.Product>>> =
        flow {
            getCurrentUserId()?.let { uid ->
                emit(ResultState.Loading)
                try {
                    val listOfProducts = supabaseClient.from("products").select()
                        .decodeList<com.appdev.smartkisan.domain.model.Product>()
                    emit(ResultState.Success(listOfProducts))
                } catch (e: Exception) {
                    Log.e("SupabaseRepository", "Product retrieval failed: ${e.message}", e)
                    emit(ResultState.Failure(e))
                }
            }
        }

    fun getSellersProfile(): Flow<ResultState<List<UserEntity>>> = flow {
        emit(ResultState.Loading)
        try {
            val response = supabaseClient
                .from("users")
                .select {
                    filter {
                        eq("role", "Seller")
                    }
                }.decodeList<UserEntity>()

            emit(ResultState.Success(response))
        } catch (e: Exception) {
            Log.e("SupabaseRepository", "Failed to fetch sellers: ${e.message}", e)
            emit(ResultState.Failure(e))
        }
    }

    fun getProducts(): Flow<ResultState<List<com.appdev.smartkisan.domain.model.Product>>> = flow {
        getCurrentUserId()?.let { uid ->
            emit(ResultState.Loading)
            try {
                val listOfProducts = supabaseClient.from("products").select {
                    filter {
                        eq("creatorId", uid)
                    }
                }.decodeList<com.appdev.smartkisan.domain.model.Product>()
                emit(ResultState.Success(listOfProducts))
            } catch (e: Exception) {
                Log.e("SupabaseRepository", "Product retrieval failed: ${e.message}", e)
                emit(ResultState.Failure(e))
            }
        }
    }

    fun getRecentProducts(): Flow<ResultState<List<com.appdev.smartkisan.domain.model.Product>>> =
        flow {
            getCurrentUserId()?.let { uid ->
                emit(ResultState.Loading)
                try {
                    val listOfProducts = supabaseClient.from("products").select {
                        filter {
                            eq("creatorId", uid)
                        }
                        order(
                            "updateTime",
                            order = Order.DESCENDING
                        ) // ensure "created_at" exists in your table
                        limit(3)
                    }.decodeList<com.appdev.smartkisan.domain.model.Product>()
                    emit(ResultState.Success(listOfProducts))
                } catch (e: Exception) {
                    Log.e("SupabaseRepository", "Product retrieval failed: ${e.message}", e)
                    emit(ResultState.Failure(e))
                }
            }
        }

    fun deleteProduct(productId: Long): Flow<ResultState<String>> = flow {
        getCurrentUserId()?.let { uid ->
            emit(ResultState.Loading)
            try {
                supabaseClient.from("products").delete {
                    filter {
                        eq("id", productId)
                        eq("creatorId", uid)
                    }
                }
                emit(ResultState.Success("Product deleted Successfully!"))
            } catch (e: Exception) {
                Log.e("SupabaseRepository", "Product deletion failed: ${e.message}", e)
                emit(ResultState.Failure(e))
            }
        }
    }

    // Updated updateProduct method to include all product properties
    fun updateProduct(
        product: com.appdev.smartkisan.domain.model.Product, imageByteArrays: List<ByteArray?>?,
        imageUris: List<Uri?>?
    ): Flow<ResultState<String>> = flow {
        getCurrentUserId()?.let { uid ->
            emit(ResultState.Loading)
            try {
                val imageUrls = mutableListOf<String>()
                if (imageByteArrays != null) {
                    for (i in imageByteArrays.indices) {
                        val byteArray = imageByteArrays[i]
                        val uri = imageUris?.getOrNull(i)
                        if (byteArray != null && uri != null) {
                            val imageUrl = imageUploading(
                                uri,
                                byteArray,
                                productImageFolderPath,
                                productImageBucketId
                            )
                            imageUrls.add(imageUrl)
                        }
                    }
                }
                if (imageUrls.isEmpty()) {
                    imageUrls.addAll(product.imageUrls)
                }
                supabaseClient.from("products").update(
                    {
                        set("creatorId", uid)
                        set("category", product.category)
                        set("name", product.name)
                        set("brandName", product.brandName)
                        set("price", product.price)
                        set("discountPrice", product.discountPrice)
                        set("imageUrls", imageUrls)
                        set("description", product.description)
                        set("quantity", product.quantity)
                        set("weightOrVolume", product.weightOrVolume)
                        set("updateTime", System.currentTimeMillis().toString())
                        set("unit", product.unit)

                        // Set category-specific attributes based on product category
                        when (product.category) {
                            "Seeds" -> {
                                set("germinationRate", product.germinationRate)
                                set("plantingSeason", product.plantingSeason)
                                set("daysToHarvest", product.daysToHarvest)
                            }

                            "Fertilizers" -> {
                                set("applicationMethod", product.applicationMethod)
                            }

                            "Medicine" -> {
                                set("targetPestsOrDiseases", product.targetPestsOrDiseases)
                            }
                        }
                    }
                ) {
                    filter {
                        eq("id", product.id)
                        eq("creatorId", uid)
                    }
                }

                emit(ResultState.Success("Product Updated Successfully!"))
            } catch (e: Exception) {
                Log.e("SupabaseRepository", "Product update failed: ${e.localizedMessage}", e)
                emit(ResultState.Failure(e))
            }
        }
    }

    // Updated addProduct method with proper category-specific attributes handling
    fun addProduct(
        product: com.appdev.smartkisan.domain.model.Product,
        imageByteArrays: List<ByteArray?>,
        imageUris: List<Uri?>
    ): Flow<ResultState<String>> =
        flow {
            getCurrentUserId()?.let { uid ->
                emit(ResultState.Loading)
                try {
                    val imageUrls = mutableListOf<String>()

                    for (i in imageByteArrays.indices) {
                        val byteArray = imageByteArrays[i]
                        val uri = imageUris.getOrNull(i)
                        if (byteArray != null && uri != null) {
                            val imageUrl = imageUploading(
                                uri,
                                byteArray,
                                productImageFolderPath,
                                productImageBucketId
                            )
                            imageUrls.add(imageUrl)
                        }
                    }

                    // Create a clean product object with proper category-specific attributes
                    val productWithImages = product.copy(
                        imageUrls = imageUrls,
                        creatorId = uid,
                        updateTime = System.currentTimeMillis().toString(),
                        // Keep only relevant category-specific attributes based on product category
                        germinationRate = if (product.category == "Seeds") product.germinationRate else null,
                        plantingSeason = if (product.category == "Seeds") product.plantingSeason else null,
                        daysToHarvest = if (product.category == "Seeds") product.daysToHarvest else null,
                        applicationMethod = if (product.category == "Fertilizers") product.applicationMethod else null,
                        targetPestsOrDiseases = if (product.category == "Medicine") product.targetPestsOrDiseases else null
                    )

                    supabaseClient.from("products").insert(productWithImages)
                    emit(ResultState.Success("Product added successfully"))
                } catch (e: Exception) {
                    Log.e("SupabaseRepository", "Product addition failed: ${e.message}", e)
                    emit(ResultState.Failure(e))
                }
            }
        }

    suspend fun refreshUserInfo(accessToken: String?, saveNewToken: () -> Unit) {
        withContext(Dispatchers.IO) {
            if (accessToken != null) {
//                supabaseClient.auth.retrieveUser(accessToken)
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

    fun fetchSellerMetaData(): Flow<ResultState<com.appdev.smartkisan.domain.model.SellerMetaData>> =
        flow {
            getCurrentUserId()?.let { userId ->
                emit(ResultState.Loading)
                try {
                    val sellerMetaData = try {
                        supabaseClient
                            .from("sellersData")
                            .select {
                                filter {
                                    eq("id", userId)
                                }
                            }
                            .decodeSingle<com.appdev.smartkisan.domain.model.SellerMetaData>()
                    } catch (e: NoSuchElementException) {
                        // If no data is found, insert an empty record with just the id
                        val emptySellerData =
                            com.appdev.smartkisan.domain.model.SellerMetaData(id = userId)

                        // Insert the empty record into the database
                        supabaseClient
                            .from("sellersData")
                            .insert(emptySellerData)

                        // Return the empty object we just inserted
                        emptySellerData
                    }

                    emit(ResultState.Success(sellerMetaData))
                } catch (e: Exception) {
                    Log.e("SellerRepository", "Failed to fetch seller meta data: ${e.message}", e)
                    emit(ResultState.Failure(e))
                }
            } ?: emit(ResultState.Failure(Exception("User not authenticated")))
        }


    fun updateUsername(username: String): Flow<ResultState<String>> = flow {
        emit(ResultState.Loading)
        try {
            val userId = getCurrentUserId()
            if (userId == null) {
                emit(ResultState.Failure(Exception("User not authenticated")))
                return@flow
            }

            supabaseClient
                .from("users")
                .update({
                    set("name", username)
                }) {
                    filter {
                        eq("id", userId)
                    }
                }

            emit(ResultState.Success(username))
        } catch (e: Exception) {
            Log.e("SellerRepository", "Failed to update username: ${e.message}", e)
            emit(ResultState.Failure(e))
        }
    }

    fun logout(): Flow<ResultState<Boolean>> = flow {
        emit(ResultState.Loading)
        try {
            // Sign out from Supabase
            // SignOutScope.GLOBAL will invalidate all session tokens for this user
            supabaseClient.auth.signOut()
            emit(ResultState.Success(true))
        } catch (e: Exception) {
            emit(ResultState.Failure(e))
        }
    }

    // Update shop name
    fun updateShopName(shopName: String): Flow<ResultState<String>> = flow {
        emit(ResultState.Loading)
        try {
            val userId = getCurrentUserId()
            if (userId == null) {
                emit(ResultState.Failure(Exception("User not authenticated")))
                return@flow
            }

            supabaseClient
                .from("sellersData")
                .update({
                    set("shopName", shopName)
                }) {
                    filter {
                        eq("id", userId)
                    }
                }

            emit(ResultState.Success(shopName))
        } catch (e: Exception) {
            Log.e("SellerRepository", "Failed to update shop name: ${e.message}", e)
            emit(ResultState.Failure(e))
        }
    }

    fun updateContact(contact: String): Flow<ResultState<String>> = flow {
        emit(ResultState.Loading)
        try {
            val userId = getCurrentUserId()
            if (userId == null) {
                emit(ResultState.Failure(Exception("User not authenticated")))
                return@flow
            }

            supabaseClient
                .from("sellersData")
                .update({
                    set("contact", contact)
                }) {
                    filter {
                        eq("id", userId)
                    }
                }

            emit(ResultState.Success(contact))
        } catch (e: Exception) {
            Log.e("SellerRepository", "Failed to update contact: ${e.message}", e)
            emit(ResultState.Failure(e))
        }
    }

    // Update password
    fun updatePassword(
        email: String,
        currentPassword: String,
        newPassword: String
    ): Flow<ResultState<Unit>> = flow {
        emit(ResultState.Loading)
        try {
            try {
                supabaseClient.auth.signInWith(Email) {
                    this.email = email
                    this.password = currentPassword
                }

                supabaseClient.auth.updateUser {
                    password = newPassword
                }

                emit(ResultState.Success(Unit))
            } catch (e: Exception) {
                emit(ResultState.Failure(Exception("Current password is incorrect")))
            }
        } catch (e: Exception) {
            emit(ResultState.Failure(e))
        }
    }

    // Update shop location
    fun updateShopLocation(lat: Double, long: Double): Flow<ResultState<String>> = flow {
        emit(ResultState.Loading)
        try {
            val userId = getCurrentUserId()
            if (userId == null) {
                emit(ResultState.Failure(Exception("User not authenticated")))
                return@flow
            }

            supabaseClient
                .from("sellersData")
                .update({
                    set("latitude", lat)
                    set("longitude", long)
                }) {
                    filter {
                        eq("id", userId)
                    }
                }

            emit(ResultState.Success("Shop location updated successfully"))
        } catch (e: Exception) {
            Log.e("SellerRepository", "Failed to update shop location: ${e.message}", e)
            emit(ResultState.Failure(e))
        }
    }

    // Update profile image
    fun updateProfileImage(imageUri: Uri, imageByteArray: ByteArray): Flow<ResultState<String>> =
        flow {
            emit(ResultState.Loading)
            try {
                val userId = getCurrentUserId()
                if (userId == null) {
                    emit(ResultState.Failure(Exception("User not authenticated")))
                    return@flow
                }

                // Upload image to storage
                val imageUrl = imageUploading(
                    imageUri,
                    imageByteArray,
                    profileImageFolderPath,
                    profileImageBucketId
                )

                Log.d("ID", "${userId}")
                // Update image URL in database
                supabaseClient
                    .from("users")
                    .update({
                        set("imageurl", imageUrl)
                    }) {
                        filter {
                            eq("id", userId)
                        }
                    }

                emit(ResultState.Success(imageUrl))
            } catch (e: Exception) {
                Log.e("SellerRepository", "Failed to update profile image: ${e.message}", e)
                emit(ResultState.Failure(e))
            }
        }

    fun loginUser(myEmail: String, _mpassword: String): Flow<ResultState<UserSession?>> =
        flow {
            emit(ResultState.Loading)
            try {
                supabaseClient.auth.signInWith(Email) {
                    email = myEmail
                    password = _mpassword
                }
                emit(ResultState.Success(supabaseClient.auth.currentSessionOrNull()))
            } catch (e: Exception) {
                emit(ResultState.Failure(e))
            }
        }

    fun signUpUserWithEmail(email: String, password: String): Flow<ResultState<String>> =
        flow {
            emit(ResultState.Loading)
            try {
                val result = supabaseClient.auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                }
                if (result != null) {
                    emit(ResultState.Success("Otp has been sent on e-mail"))
                }
            } catch (e: Exception) {
                emit(ResultState.Failure(e))
            }
        }

    fun verifyEmailAndSignIn(email: String, otp: String): Flow<ResultState<UserSession?>> =
        flow {
            emit(ResultState.Loading)
            try {
                supabaseClient.auth.verifyEmailOtp(
                    type = OtpType.Email.EMAIL,
                    email = email,
                    token = otp
                )
                emit(ResultState.Success(supabaseClient.auth.currentSessionOrNull()))
            } catch (e: Exception) {
                emit(ResultState.Failure(e))
            }
        }

    fun requestPasswordReset(email: String): Flow<ResultState<Boolean>> = flow {
        emit(ResultState.Loading)
        try {
            supabaseClient.auth.resetPasswordForEmail(email = email)
            emit(ResultState.Success(true))
        } catch (e: Exception) {
            emit(ResultState.Failure(e))
        }
    }

    // Update this function in your repository class
    fun updateUserPassword(
        newPassword: String,
        resetToken: String?,
        refreshToken: String? = null,
        expiresIn: Int? = null,
        tokenType: String,
        type: String
    ): Flow<ResultState<Boolean>> = flow {
        emit(ResultState.Loading)
        try {
            if (resetToken != null) {
                // For password reset flow with recovery token

                if (refreshToken != null && expiresIn != null) {
                    supabaseClient.auth.importSession(
                        session = UserSession(
                            accessToken = resetToken,
                            refreshToken = refreshToken,
                            expiresIn = expiresIn.toLong(), tokenType = tokenType, type = type
                        )
                    )
                }
                Log.d("CHLAZ", "${resetToken}\n${refreshToken}")

                // Now that we have a session with the recovery token, update the password
                supabaseClient.auth.updateUser {
                    password = newPassword
                }
                emit(ResultState.Success(true))
            } else {
                // If the user is already logged in and wants to change password
                supabaseClient.auth.updateUser {
                    password = newPassword
                }
                emit(ResultState.Success(true))
            }
        } catch (e: Exception) {
            // Log the error for debugging
            Log.e("AuthRepository", "Password update failed", e)
            emit(ResultState.Failure(e))
        }
    }

    // Optional: Add a password reset function
    fun sendPasswordResetEmail(email: String): Flow<ResultState<String>> =
        flow {
            emit(ResultState.Loading)
            try {
                supabaseClient.auth.resetPasswordForEmail(email)
                emit(ResultState.Success("Password reset email sent"))
            } catch (e: Exception) {
                emit(ResultState.Failure(e))
            }
        }

    suspend fun imageUploading(
        imageUri: Uri,
        imageBytes: ByteArray,
        folderPath: String,
        bucketId: String
    ): String {
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
                val fullPath = "$folderPath/$fileName"
                val bucket = supabaseClient.storage.from(bucketId)
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
                        imageUploading(
                            imageUri,
                            imageByteArray,
                            profileImageFolderPath,
                            profileImageBucketId
                        )
                    } else {
                        ""
                    }
                    userEntity.imageUrl = imageUrl
                    userEntity.id = uid
                    supabaseClient.from("users").insert(userEntity)
                    emit(ResultState.Success(imageUrl))
                } catch (e: Exception) {
                    Log.e("SupabaseRepository", "Profile creation failed: ${e.message}", e)
                    emit(ResultState.Failure(e))
                }
            }
        }

    fun getCurrentUserId(): String? {
        return supabaseClient.auth.currentUserOrNull()?.id
    }
}