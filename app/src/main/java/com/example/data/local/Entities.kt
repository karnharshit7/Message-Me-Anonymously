package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["username"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val displayName: String,
    val bio: String = "Ask me anything anonymously 👀",
    val avatarUrl: String = "avatar_1",
    val email: String = "",
    val theme: String = "Purple", // Midnight, Sunset, Ocean, Purple, Minimal, Neon
    val messagingControl: String = "Everyone", // Everyone, Friends only, Followers only, Nobody
    val isPaused: Boolean = false,
    val isDiscoverable: Boolean = true,
    val isSuspended: Boolean = false,
    val isBanned: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val profileViews: Int = 124,
    val sharesCount: Int = 18
)

@Entity(
    tableName = "messages",
    indices = [Index(value = ["recipientUsername"]), Index(value = ["senderToken"])]
)
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val recipientUsername: String,
    val content: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val isArchived: Boolean = false,
    val isDeleted: Boolean = false,
    val senderToken: String, // Secure hashed identifier for rate-limiting & blocking, never shown to user
    val moderationStatus: String = "APPROVED", // APPROVED, FLAGGED, BLOCKED
    val isReported: Boolean = false,
    val reportCount: Int = 0,
    val promptQuestion: String = "Ask me anything"
)

@Entity(
    tableName = "message_replies"
)
data class ReplyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val messageId: Long,
    val replyText: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "message_reports"
)
data class ReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val messageId: Long,
    val reportedUsername: String,
    val category: String, // Harassment, Bullying, Threat, Hate, Sexual content, Spam, Scam, Self-harm related, Other
    val notes: String = "",
    val messageContentPreview: String = "",
    val status: String = "PENDING", // PENDING, RESOLVED, DISMISSED
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "blocked_senders",
    indices = [Index(value = ["userUsername", "senderToken"], unique = true)]
)
data class BlockedSenderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userUsername: String,
    val senderToken: String,
    val blockedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "admin_settings"
)
data class AdminSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val rateLimitPerHour: Int = 10,
    val maxCharacterLimit: Int = 500,
    val moderationSensitivity: String = "HIGH",
    val isMaintenanceMode: Boolean = false
)
