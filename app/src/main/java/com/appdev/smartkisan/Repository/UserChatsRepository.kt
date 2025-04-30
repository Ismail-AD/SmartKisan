package com.appdev.smartkisan.Repository

import android.util.Log
import com.appdev.smartkisan.Utils.MessageStatus
import com.appdev.smartkisan.Utils.ResultState
import com.appdev.smartkisan.Utils.SessionManagement
import com.appdev.smartkisan.data.ChatMateData
import com.appdev.smartkisan.data.ChatMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.cancel


class UserChatsRepository @Inject constructor(
    val firebaseAuth: FirebaseAuth,
    val supabaseClient: SupabaseClient,
    private val database: FirebaseDatabase,
) {

    companion object {
        private const val ERROR_MESSAGE = "An error occurred"
        private const val CHAT_NODE = "Chats"
        private const val CHAT_METADATA_NODE = "Chat_Metadata"
    }

    fun getCurrentUserId(): String? {
        return supabaseClient.auth.currentUserOrNull()?.id
    }


    fun sendMessage(
        myName: String, myImage: String,
        receiverId: String,
        receiverName: String,
        receiverProfilePic: String?,
        messageContent: String
    ): Flow<ResultState<Boolean>> = flow {
        try {
            emit(ResultState.Loading)

            // Generate a unique ID for the message
            val messageId = UUID.randomUUID().toString()
            getCurrentUserId()?.let { currentUserId ->
                // Create a chat room ID by sorting the two user IDs alphabetically
                // This ensures the same chat room ID regardless of who initiates the chat
                val chatRoomId = if (currentUserId < receiverId) {
                    "${currentUserId}_${receiverId}"
                } else {
                    "${receiverId}_${currentUserId}"
                }

                // Create the message object
                val message = ChatMessage(
                    messageId = messageId,
                    senderID = currentUserId,
                    message = messageContent,
                    timeStamp = System.currentTimeMillis(),
                    status = MessageStatus.SENT.toString()
                )

                // Save the message in the chat room
                val chatRef = database.reference
                    .child(CHAT_NODE)
                    .child(chatRoomId)
                    .child(messageId)

                chatRef.setValue(message).await()

                // Update metadata for both users for easy access to recent chats
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

                emit(ResultState.Success(true))
            }

        } catch (e: Exception) {
            Log.e("ChatRepository", "Error sending message: ${e.message}", e)
            emit(ResultState.Failure(e))
        }
    }


    private suspend fun updateChatMetadata(
        chatRoomId: String,
        currentUserId: String,
        receiverId: String,
        lastMessage: ChatMessage,
        receiverName: String,
        receiverProfilePic: String?,
        myName: String, myImage: String
    ) {
        // Update metadata for current user
        database.reference
            .child(CHAT_METADATA_NODE)
            .child(currentUserId)
            .child(receiverId)
            .updateChildren(
                mapOf(
                    "chatRoomId" to chatRoomId,
                    "partnerId" to receiverId,
                    "lastMessage" to lastMessage.message,
                    "lastMessageTime" to lastMessage.timeStamp,
                    "unreadCount" to 0,
                    "receiverName" to receiverName,
                    "receiverImage" to receiverProfilePic
                )
            ).await()

        // Update metadata for receiver with unread count incremented
        val receiverMetadataRef = database.reference
            .child(CHAT_METADATA_NODE)
            .child(receiverId)
            .child(currentUserId)

        // Get current unread count for receiver and increment
        val currentUnreadCount =
            receiverMetadataRef.child("unreadCount").get().await().getValue(Int::class.java) ?: 0

        receiverMetadataRef.updateChildren(
            mapOf(
                "chatRoomId" to chatRoomId,
                "partnerId" to currentUserId,
                "lastMessage" to lastMessage.message,
                "lastMessageTime" to lastMessage.timeStamp,
                "unreadCount" to currentUnreadCount + 1,
                "receiverName" to myName,
                "receiverImage" to myImage
            )
        ).await()
    }

    fun loadMessages(receiverId: String): Flow<ResultState<List<ChatMessage>>> = callbackFlow {
        try {
            trySendBlocking(ResultState.Loading)

            getCurrentUserId()?.let { currentUserId ->


                // Determine chat room ID
                val chatRoomId = if (currentUserId < receiverId) {
                    "${currentUserId}_${receiverId}"
                } else {
                    "${receiverId}_${currentUserId}"
                }

                // Reset unread count for current user
                database.reference
                    .child(CHAT_METADATA_NODE)
                    .child(currentUserId)
                    .child(receiverId)
                    .child("unreadCount")
                    .setValue(0)

                // Reference to the chat messages
                val chatRef = database.reference
                    .child(CHAT_NODE)
                    .child(chatRoomId)

                val listener = chatRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val messageList = arrayListOf<ChatMessage>()
                        val unreadMessageIds = mutableListOf<String>()

                        snapshot.children.forEach { messageSnapshot ->
                            val message = messageSnapshot.getValue(ChatMessage::class.java)
                            if (message != null) {
                                messageList.add(message)

                                // If message is from the other person and not read yet, mark to update
                                if (message.senderID == receiverId &&
                                    message.status == MessageStatus.SENT.toString()
                                ) {
                                    unreadMessageIds.add(messageSnapshot.key ?: "")
                                }
                            }
                        }

                        // Update message status to READ for all unread messages
                        unreadMessageIds.forEach { messageId ->
                            chatRef.child(messageId)
                                .child("status")
                                .setValue(MessageStatus.READ.toString())
                        }

                        // Sort messages by timestamp
                        messageList.sortBy { it.timeStamp }
                        trySendBlocking(ResultState.Success(messageList))
                    }

                    override fun onCancelled(error: DatabaseError) {
                        trySendBlocking(ResultState.Failure(Exception(error.message)))
                    }
                })

                awaitClose {
                    chatRef.removeEventListener(listener)
                    channel.close()
                    cancel()
                }
            }
        } catch (e: Exception) {
            Log.e("ChatRepository", "Error loading messages: ${e.message}", e)
            trySendBlocking(ResultState.Failure(e))
        }
    }

    fun getRecentChats(): Flow<ResultState<List<ChatMateData>>> = callbackFlow {
        try {
            trySendBlocking(ResultState.Loading)


            getCurrentUserId()?.let { currentUserId ->
                val recentChatsRef = database.reference
                    .child(CHAT_METADATA_NODE)
                    .child(currentUserId)

                val listener = recentChatsRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val chatsList = arrayListOf<ChatMateData>()

                        snapshot.children.forEach { chatSnapshot ->
                            val chatMetadata = chatSnapshot.getValue(ChatMateData::class.java)
                            if (chatMetadata != null) {
                                chatsList.add(chatMetadata)
                            }
                        }

                        // Sort by most recent message
                        chatsList.sortByDescending { it.lastMessageTime }
                        trySendBlocking(ResultState.Success(chatsList))
                    }

                    override fun onCancelled(error: DatabaseError) {
                        trySendBlocking(ResultState.Failure(Exception(error.message)))
                    }
                })

                awaitClose {
                    recentChatsRef.removeEventListener(listener)
                    channel.close()
                    cancel()
                }

            }
        } catch (e: Exception) {
            Log.e("ChatRepository", "Error getting recent chats: ${e.message}", e)
            trySendBlocking(ResultState.Failure(e))
        }
    }


}