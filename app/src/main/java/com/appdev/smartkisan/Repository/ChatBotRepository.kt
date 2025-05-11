package com.appdev.smartkisan.Repository

import android.net.Uri
import android.util.Log
import com.appdev.smartkisan.Utils.ResultState
import com.appdev.smartkisan.data.BotChatMessage
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

class ChatBotRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val supabaseClient: SupabaseClient,
    private val repository: Repository  // Your existing repository for Supabase image upload functions
) {
    companion object {
        private const val CHAT_COLLECTION = "bot_chats"
        private const val CHAT_DATES_COLLECTION = "bot_chat_dates" // New collection to track dates
        const val PAGE_SIZE = 10
        private const val TAG = "BotChatRepository"

        // Bucket and path for bot chat images
        private const val BOT_CHAT_BUCKET_ID = "botchatimages"
        private const val BOT_CHAT_FOLDER_PATH = "public/1plmml3_1"
    }

    // Get the current user ID from the repository
    private fun getCurrentUserId(): String? {
        return repository.getCurrentUserId()
    }

    // Format date as YYYY-MM-DD for querying by date
    private fun getFormattedDate(timestamp: Long = System.currentTimeMillis()): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    // Save a chat message with optional images
    suspend fun saveMessage(
        message: String,
        role: String,
        imageUris: List<Uri> = emptyList(),
        imageBytes: List<ByteArray> = emptyList(),
    ): Flow<ResultState<BotChatMessage>> = flow {
        try {
            emit(ResultState.Loading)

            val userId = getCurrentUserId()
            if (userId == null) {
                emit(ResultState.Failure(Exception("User not authenticated")))
                return@flow
            }

            // Upload images to Supabase if present
            val imageUrls = mutableListOf<String>()
            if (imageUris.isNotEmpty() && imageBytes.isNotEmpty() && imageUris.size == imageBytes.size) {
                // We have matching Uris and byte arrays
                for (i in imageUris.indices) {
                    try {
                        val imageUrl = repository.imageUploading(
                            imageUri = imageUris[i],
                            imageBytes = imageBytes[i],  // Pass the specific byte array for this URI
                            folderPath = BOT_CHAT_FOLDER_PATH,
                            bucketId = BOT_CHAT_BUCKET_ID
                        )
                        imageUrls.add(imageUrl)
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to upload image: ${e.message}")
                        // Continue with other images even if one fails
                    }
                }
            }


            val currentTime = System.currentTimeMillis()
            val formattedDate = getFormattedDate(currentTime)

            val messageRef = firestore.collection(CHAT_COLLECTION)
                .document(userId)
                .collection(formattedDate)
                .document()

            val messageId = messageRef.id

            val chatMessage = BotChatMessage(
                id = messageId,
                message = message,
                role = role,
                timestamp = currentTime,
                date = formattedDate,
                imageUrls = imageUrls
            )

            messageRef.set(chatMessage).await()

            // Check if this date has already been saved
            val dateDocRef = firestore.collection(CHAT_DATES_COLLECTION)
                .document(userId)
                .collection("dates")
                .document(formattedDate)

            val dateSnapshot = dateDocRef.get().await()

            // Only set the date if it doesn't exist yet
            if (!dateSnapshot.exists()) {
                val dateData = mapOf(
                    "timestamp" to currentTime,
                    "dateString" to formattedDate
                )
                dateDocRef.set(dateData).await()
            }


            emit(ResultState.Success(chatMessage))
        } catch (e: Exception) {
            Log.e(TAG, "Error saving message: ${e.message}", e)
            emit(ResultState.Failure(e))
        }
    }

    // Get messages for today
    suspend fun getTodayMessages(): Flow<ResultState<List<BotChatMessage>>> = flow {
        try {
            emit(ResultState.Loading)

            val userId = getCurrentUserId()
            if (userId == null) {
                emit(ResultState.Failure(Exception("User not authenticated")))
                return@flow
            }

            val today = getFormattedDate()

            // Direct query to today's collection
            val messages = firestore.collection(CHAT_COLLECTION)
                .document(userId)
                .collection(today)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .await()
                .toObjects(BotChatMessage::class.java)

            emit(ResultState.Success(messages))
        } catch (e: Exception) {
            Log.e(TAG, "Error getting today's messages: ${e.message}", e)
            emit(ResultState.Failure(e))
        }
    }

    // Get messages for a specific date
    suspend fun getMessagesByDate(date: String): Flow<ResultState<List<BotChatMessage>>> = flow {
        try {
            emit(ResultState.Loading)

            val userId = getCurrentUserId()
            if (userId == null) {
                emit(ResultState.Failure(Exception("User not authenticated")))
                return@flow
            }

            // Direct query to the date's collection
            val messages = firestore.collection(CHAT_COLLECTION)
                .document(userId)
                .collection(date)  // Query directly on the date's collection
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .await()
                .toObjects(BotChatMessage::class.java)

            emit(ResultState.Success(messages))
        } catch (e: Exception) {
            Log.e(TAG, "Error getting messages for date $date: ${e.message}", e)
            emit(ResultState.Failure(e))
        }
    }

    // Load more messages (pagination)
    suspend fun loadMoreMessages(
        date: String,
        lastTimestamp: Long
    ): Flow<ResultState<List<BotChatMessage>>> = flow {
        try {
            emit(ResultState.Loading)

            val userId = getCurrentUserId()
            if (userId == null) {
                emit(ResultState.Failure(Exception("User not authenticated")))
                return@flow
            }

            val messages = firestore.collection(CHAT_COLLECTION)
                .document(userId)
                .collection(date)  // Query directly on the date's collection
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .startAfter(lastTimestamp)
                .limit(PAGE_SIZE.toLong())
                .get()
                .await()
                .toObjects(BotChatMessage::class.java)

            emit(ResultState.Success(messages))
        } catch (e: Exception) {
            Log.e(TAG, "Error loading more messages: ${e.message}", e)
            emit(ResultState.Failure(e))
        }
    }

    // Get all dates that have chat messages - UPDATED METHOD
    suspend fun getAvailableChatDates(): Flow<ResultState<List<String>>> = flow {
        try {
            emit(ResultState.Loading)

            val userId = getCurrentUserId()
            if (userId == null) {
                emit(ResultState.Failure(Exception("User not authenticated")))
                return@flow
            }

            // Query the dates collection instead of listing collections
            val datesSnapshot = firestore.collection(CHAT_DATES_COLLECTION)
                .document(userId)
                .collection("dates")
                .orderBy("timestamp", Query.Direction.DESCENDING) // Get newest dates first
                .get()
                .await()

            val dates = datesSnapshot.documents.mapNotNull { it.getString("dateString") }

            emit(ResultState.Success(dates))
        } catch (e: Exception) {
            Log.e(TAG, "Error getting available chat dates: ${e.message}", e)
            emit(ResultState.Failure(e))
        }
    }

    // Delete a date's messages and its record from dates collection
    suspend fun deleteChatDate(date: String): Flow<ResultState<Boolean>> = flow {
        try {
            emit(ResultState.Loading)

            val userId = getCurrentUserId()
            if (userId == null) {
                emit(ResultState.Failure(Exception("User not authenticated")))
                return@flow
            }

            // Get all messages for the date
            val messagesRef = firestore.collection(CHAT_COLLECTION)
                .document(userId)
                .collection(date)

            // Delete in batches of 500 (Firestore limit)
            val batchSize = 500
            var messagesDeleted = 0

            do {
                val messagesToDelete = messagesRef
                    .limit(batchSize.toLong())
                    .get()
                    .await()

                if (messagesToDelete.isEmpty) {
                    break
                }

                val batch = firestore.batch()
                messagesToDelete.documents.forEach { doc ->
                    batch.delete(doc.reference)
                }

                batch.commit().await()
                messagesDeleted += messagesToDelete.size()
            } while (messagesToDelete.size() >= batchSize)

            // Now delete the date reference
            firestore.collection(CHAT_DATES_COLLECTION)
                .document(userId)
                .collection("dates")
                .document(date)
                .delete()
                .await()

            emit(ResultState.Success(true))
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting chat date $date: ${e.message}", e)
            emit(ResultState.Failure(e))
        }
    }
}