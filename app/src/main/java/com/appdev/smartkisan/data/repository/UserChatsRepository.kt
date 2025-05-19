package com.appdev.smartkisan.data.repository

import android.net.Uri
import android.util.Log
import com.appdev.smartkisan.Utils.MessageStatus
import com.appdev.smartkisan.Utils.ResultState
import com.appdev.smartkisan.domain.model.ChatMateData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserChatsRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val supabaseClient: SupabaseClient,
    private val firestore: FirebaseFirestore,
    private val repository: Repository // Your existing repository for Supabase image upload functions
) {

    companion object {
        private const val TAG = "UserChatsRepository"
        private const val CHATS_COLLECTION = "user_chats"
        private const val CHAT_METADATA_COLLECTION = "chat_metadata"

        // Bucket and path for user chat images
        private const val USER_CHAT_BUCKET_ID = "userchatimages"
        private const val USER_CHAT_FOLDER_PATH = "public/xe5uxn_1"
    }

    fun getCurrentUserId(): String? {
        return supabaseClient.auth.currentUserOrNull()?.id
    }

    fun sendMessage(
        myName: String,
        myImage: String,
        receiverId: String,
        receiverName: String,
        receiverProfilePic: String?,
        messageContent: String,
        imageUris: List<Uri> = emptyList(),
        imageBytes: List<ByteArray> = emptyList(),
        tempMessageId: String,
    ): Flow<ResultState<Pair<com.appdev.smartkisan.domain.model.ChatMessage,String>>> = flow {
        try {
            emit(ResultState.Loading)

            val currentUserId = getCurrentUserId()
            if (currentUserId == null) {
                emit(ResultState.Failure(Exception("User not authenticated")))
                return@flow
            }


            // Create a chat room ID by sorting the two user IDs alphabetically
            // This ensures the same chat room ID regardless of who initiates the chat
            val chatRoomId = if (currentUserId < receiverId) {
                "${currentUserId}_${receiverId}"
            } else {
                "${receiverId}_${currentUserId}"
            }

            // Upload images to Supabase if present
            val imageUrls = mutableListOf<String>()
            if (imageUris.isNotEmpty() && imageBytes.isNotEmpty() && imageUris.size == imageBytes.size) {
                for (i in imageUris.indices) {
                    try {
                        val imageUrl = repository.imageUploading(
                            imageUri = imageUris[i],
                            imageBytes = imageBytes[i],
                            folderPath = USER_CHAT_FOLDER_PATH,
                            bucketId = USER_CHAT_BUCKET_ID
                        )
                        imageUrls.add(imageUrl)
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to upload image: ${e.message}")
                        // Continue with other images even if one fails
                    }
                }
            }

            // Generate Firestore document reference to get unique message ID
            val messageRef = firestore.collection(CHATS_COLLECTION)
                .document(chatRoomId)
                .collection("messages")
                .document()

            val messageId = messageRef.id

            val message = com.appdev.smartkisan.domain.model.ChatMessage(
                messageId = messageId,
                senderID = currentUserId,
                message = messageContent,
                timeStamp = System.currentTimeMillis(),
                status = MessageStatus.SENT.toString(),
                imageUrls = imageUrls
            )

            messageRef.set(message).await()


            // Update metadata for both users
            updateChatMetadata(
                chatRoomId,
                currentUserId,
                receiverId,
                message,
                receiverName,
                receiverProfilePic,
                myName,
                myImage
            )

            emit(ResultState.Success(Pair(message,tempMessageId)))
        } catch (e: Exception) {
            Log.e(TAG, "Error sending message: ${e.message}", e)
            emit(ResultState.Failure(e))
        }
    }

    private suspend fun updateChatMetadata(
        chatRoomId: String,
        currentUserId: String,
        receiverId: String,
        lastMessage: com.appdev.smartkisan.domain.model.ChatMessage,
        receiverName: String,
        receiverProfilePic: String?,
        myName: String,
        myImage: String
    ) {
        val currentTime = System.currentTimeMillis()

        // Update metadata for current user
        val currentUserMetadata = hashMapOf(
            "chatRoomId" to chatRoomId,
            "partnerId" to receiverId,
            "lastMessage" to lastMessage.message,
            "lastMessageTime" to currentTime,
            "unreadCount" to 0,
            "receiverName" to receiverName,
            "receiverImage" to receiverProfilePic,
            "updatedAt" to FieldValue.serverTimestamp()
        )

        // Use set with merge to create document if it doesn't exist
        firestore.collection(CHAT_METADATA_COLLECTION)
            .document(currentUserId)
            .collection("chats")
            .document(receiverId)
            .set(currentUserMetadata)
            .await()

        // Create receiver metadata document if it doesn't exist
        val receiverMetadataRef = firestore.collection(CHAT_METADATA_COLLECTION)
            .document(receiverId)
            .collection("chats")
            .document(currentUserId)

        val receiverMetadataDoc = receiverMetadataRef.get().await()
        val currentUnreadCount = if (receiverMetadataDoc.exists()) {
            (receiverMetadataDoc.getLong("unreadCount") ?: 0) + 1
        } else {
            1
        }

        // Update metadata for receiver
        val receiverMetadata = hashMapOf(
            "chatRoomId" to chatRoomId,
            "partnerId" to currentUserId,
            "lastMessage" to lastMessage.message,
            "lastMessageTime" to currentTime,
            "unreadCount" to currentUnreadCount,
            "receiverName" to myName,
            "receiverImage" to myImage,
            "updatedAt" to FieldValue.serverTimestamp()
        )

        // Use set instead of update to create if not exists
        receiverMetadataRef.set(receiverMetadata).await()
    }

    fun loadMessages(receiverId: String): Flow<ResultState<List<com.appdev.smartkisan.domain.model.ChatMessage>>> = callbackFlow {
        try {
            // Only emit Loading once at the beginning
            send(ResultState.Loading)

            val currentUserId = getCurrentUserId()
            if (currentUserId == null) {
                send(ResultState.Failure(Exception("User not authenticated")))
                close()
                return@callbackFlow
            }

            // Determine chat room ID
            val chatRoomId = if (currentUserId < receiverId) {
                "${currentUserId}_${receiverId}"
            } else {
                "${receiverId}_${currentUserId}"
            }

            // Check if metadata document exists before updating
            val metadataRef = firestore.collection(CHAT_METADATA_COLLECTION)
                .document(currentUserId)
                .collection("chats")
                .document(receiverId)
            val metadataDoc = metadataRef.get().await()
            if (metadataDoc.exists()) {
                // Only reset unread count if the document exists
                metadataRef.update("unreadCount", 0).await()
            }

            // Register listener for messages
            val chatRef = firestore.collection(CHATS_COLLECTION)
                .document(chatRoomId)
                .collection("messages")
                .orderBy("timeStamp", Query.Direction.ASCENDING)

            val listenerRegistration = chatRef.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(ResultState.Failure(Exception(error.message)))
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val messageList = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(com.appdev.smartkisan.domain.model.ChatMessage::class.java)
                    }

                    // Mark messages from receiver as read
                    snapshot.documents.forEach { doc ->
                        val message = doc.toObject(com.appdev.smartkisan.domain.model.ChatMessage::class.java)
                        if (message?.senderID == receiverId && message.status == MessageStatus.SENT.toString()) {
                            doc.reference.update("status", MessageStatus.READ.toString())
                        }
                    }

                    // Send success state with the messages, don't send Loading again
                    trySend(ResultState.Success(messageList))
                } else {
                    // If snapshot is null but there's no error, just send an empty list
                    trySend(ResultState.Success(emptyList()))
                }
            }

            // This is critical - must include awaitClose to avoid memory leaks
            awaitClose {
                listenerRegistration.remove()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading messages: ${e.message}", e)
            trySend(ResultState.Failure(e))
            close(e)
        }
    }

    fun getRecentChats(): Flow<ResultState<List<ChatMateData>>> = callbackFlow {
        try {
            trySend(ResultState.Loading)

            val currentUserId = getCurrentUserId()
            if (currentUserId == null) {
                trySend(ResultState.Failure(Exception("User not authenticated")))
                close()
                return@callbackFlow
            }

            val recentChatsRef = firestore.collection(CHAT_METADATA_COLLECTION)
                .document(currentUserId)
                .collection("chats")
                .orderBy("lastMessageTime", Query.Direction.DESCENDING)

            val listenerRegistration = recentChatsRef.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(ResultState.Failure(Exception(error.message)))
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val chatsList = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(ChatMateData::class.java)
                    }
                    trySend(ResultState.Success(chatsList))
                }
            }

            // Must include awaitClose to avoid memory leaks
            awaitClose {
                listenerRegistration.remove()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting recent chats: ${e.message}", e)
            trySend(ResultState.Failure(e))
            close(e)
        }
    }

    // Delete a chat conversation
    fun deleteChat(receiverId: String): Flow<ResultState<Boolean>> = callbackFlow {
        try {
            trySend(ResultState.Loading)

            val currentUserId = getCurrentUserId()
            if (currentUserId == null) {
                trySend(ResultState.Failure(Exception("User not authenticated")))
                close()
                return@callbackFlow
            }

            // Determine chat room ID
            val chatRoomId = if (currentUserId < receiverId) {
                "${currentUserId}_${receiverId}"
            } else {
                "${receiverId}_${currentUserId}"
            }

            // Check if metadata document exists before deleting
            val metadataRef = firestore.collection(CHAT_METADATA_COLLECTION)
                .document(currentUserId)
                .collection("chats")
                .document(receiverId)

            val metadataDoc = metadataRef.get().await()
            if (metadataDoc.exists()) {
                // Only delete if document exists
                metadataRef.delete().await()
            }

            trySend(ResultState.Success(true))

            // Must include awaitClose
            awaitClose { }
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting chat: ${e.message}", e)
            trySend(ResultState.Failure(e))
            close(e)
        }
    }
}