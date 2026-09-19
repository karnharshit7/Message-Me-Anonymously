package com.example.data.repository

import android.content.Context
import com.example.data.local.AdminSettingsEntity
import com.example.data.local.BlockedSenderEntity
import com.example.data.local.MessageEntity
import com.example.data.local.MmaDao
import com.example.data.local.ReplyEntity
import com.example.data.local.ReportEntity
import com.example.data.local.UserEntity
import com.example.domain.moderation.ModerationEngine
import com.example.domain.moderation.ModerationResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.security.MessageDigest
import java.util.UUID

sealed class SendMessageResult {
    data class Success(val messageId: Long) : SendMessageResult()
    data class Error(val message: String) : SendMessageResult()
}

class MmaRepository(
    private val dao: MmaDao,
    private val context: Context
) {
    private val prefs = context.getSharedPreferences("mma_device_prefs", Context.MODE_PRIVATE)

    // A secure, anonymized sender identifier stored locally per device/session
    val senderToken: String
        get() {
            var token = prefs.getString("sender_anon_token", null)
            if (token == null) {
                val raw = UUID.randomUUID().toString() + "_" + System.currentTimeMillis()
                token = hashToken(raw)
                prefs.edit().putString("sender_anon_token", token).apply()
            }
            return token
        }

    private fun hashToken(input: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest(input.toByteArray())
        return bytes.take(16).joinToString("") { "%02x".format(it) }
    }

    // --- INITIAL SEEDING ---
    suspend fun seedInitialDataIfNeeded() {
        val existingHarshit = dao.getUserByUsername("harshit")
        if (existingHarshit == null) {
            // Seed User 1: Harshit (from prompt example)
            val harshit = UserEntity(
                username = "harshit",
                displayName = "Harshit",
                bio = "Ask me anything anonymously 👀",
                avatarUrl = "avatar_1",
                email = "harshit@example.com",
                theme = "Purple",
                messagingControl = "Everyone",
                isPaused = false,
                isDiscoverable = true,
                profileViews = 342,
                sharesCount = 29
            )
            dao.insertUser(harshit)

            // Seed User 2: Maya (for easy User A <-> User B switching!)
            val maya = UserEntity(
                username = "maya_dev",
                displayName = "Maya Chen",
                bio = "Say what's on your mind 💭 Be honest!",
                avatarUrl = "avatar_2",
                email = "maya@example.com",
                theme = "Sunset",
                messagingControl = "Everyone",
                isPaused = false,
                isDiscoverable = true,
                profileViews = 189,
                sharesCount = 14
            )
            dao.insertUser(maya)

            // Seed User 3: Alex (Neon theme)
            val alex = UserEntity(
                username = "alex_sky",
                displayName = "Alex Vance",
                bio = "Tell me secrets or ask questions 🌙",
                avatarUrl = "avatar_3",
                email = "alex@example.com",
                theme = "Neon",
                messagingControl = "Everyone",
                isPaused = false,
                isDiscoverable = true,
                profileViews = 275,
                sharesCount = 38
            )
            dao.insertUser(alex)

            // Seed realistic sample messages for Harshit (Section 34)
            val sampleMessages = listOf(
                MessageEntity(
                    recipientUsername = "harshit",
                    content = "Honestly, you're one of the nicest people I know.",
                    createdAt = System.currentTimeMillis() - 1000 * 60 * 35, // 35 mins ago
                    senderToken = hashToken("seed_sender_1"),
                    promptQuestion = "What do you really think about me?"
                ),
                MessageEntity(
                    recipientUsername = "harshit",
                    content = "You seem really confident. What's your secret?",
                    createdAt = System.currentTimeMillis() - 1000 * 60 * 120, // 2 hrs ago
                    senderToken = hashToken("seed_sender_2"),
                    promptQuestion = "Ask me anything"
                ),
                MessageEntity(
                    recipientUsername = "harshit",
                    content = "What is your biggest goal for this year?",
                    createdAt = System.currentTimeMillis() - 1000 * 60 * 60 * 6, // 6 hrs ago
                    senderToken = hashToken("seed_sender_3"),
                    promptQuestion = "Give me honest advice"
                ),
                MessageEntity(
                    recipientUsername = "harshit",
                    content = "Describe me in 3 words: brilliant, chill, mysterious 😉",
                    createdAt = System.currentTimeMillis() - 1000 * 60 * 60 * 24, // 1 day ago
                    senderToken = hashToken("seed_sender_4"),
                    promptQuestion = "Describe me in 3 words"
                )
            )

            sampleMessages.forEach { msg ->
                val id = dao.insertMessage(msg)
                // Add sample reply for the second message
                if (msg.content.contains("confident")) {
                    dao.insertReply(
                        ReplyEntity(
                            messageId = id,
                            replyText = "Honestly, I just try not to overthink everything.",
                            createdAt = System.currentTimeMillis() - 1000 * 60 * 90
                        )
                    )
                }
            }

            // Seed admin settings
            dao.insertOrUpdateAdminSettings(
                AdminSettingsEntity(
                    rateLimitPerHour = 10,
                    maxCharacterLimit = 500,
                    moderationSensitivity = "HIGH",
                    isMaintenanceMode = false
                )
            )
        }
    }

    // --- USER PROFILE & AUTH ---
    fun getAllUsers(): Flow<List<UserEntity>> = dao.getAllUsers()
    fun getDiscoverableUsers(): Flow<List<UserEntity>> = dao.getDiscoverableUsers()
    suspend fun getUserByUsername(username: String): UserEntity? = dao.getUserByUsername(username)
    fun observeUser(username: String): Flow<UserEntity?> = dao.observeUserByUsername(username)

    suspend fun registerUser(
        username: String,
        displayName: String,
        bio: String,
        avatarUrl: String,
        email: String
    ): Result<UserEntity> {
        val validation = ModerationEngine.validateUsername(username)
        if (!validation.first) {
            return Result.failure(IllegalArgumentException(validation.second ?: "Invalid username"))
        }

        val existing = dao.getUserByUsername(username)
        if (existing != null) {
            return Result.failure(IllegalArgumentException("That username is already taken."))
        }

        val newUser = UserEntity(
            username = username.trim().lowercase(),
            displayName = displayName.trim().ifEmpty { username },
            bio = bio.trim().ifEmpty { "Ask me anything anonymously 👀" },
            avatarUrl = avatarUrl,
            email = email.trim()
        )
        dao.insertUser(newUser)
        return Result.success(newUser)
    }

    suspend fun updateUserProfile(user: UserEntity) {
        dao.updateUser(user)
    }

    suspend fun deleteUserAccount(username: String) {
        dao.deleteUser(username)
    }

    suspend fun incrementViews(username: String) {
        dao.incrementProfileViews(username)
    }

    suspend fun incrementShares(username: String) {
        dao.incrementSharesCount(username)
    }

    // --- MESSAGING SYSTEM ---
    fun getMessagesForUser(username: String): Flow<List<MessageEntity>> = dao.getActiveMessagesForUser(username)
    fun getArchivedMessagesForUser(username: String): Flow<List<MessageEntity>> = dao.getArchivedMessagesForUser(username)
    fun getUnreadCount(username: String): Flow<Int> = dao.getUnreadCount(username)

    suspend fun sendMessage(
        recipientUsername: String,
        content: String,
        promptQuestion: String
    ): SendMessageResult {
        val trimmed = content.trim()
        if (trimmed.isEmpty()) {
            return SendMessageResult.Error("Please write a message before sending.")
        }

        // 1. Fetch recipient
        val recipient = dao.getUserByUsername(recipientUsername)
            ?: return SendMessageResult.Error("This profile doesn't exist or is no longer available.")

        if (recipient.isBanned || recipient.isSuspended) {
            return SendMessageResult.Error("This account is currently unavailable.")
        }

        if (recipient.isPaused) {
            return SendMessageResult.Error("This user isn't accepting anonymous messages right now.")
        }

        if (recipient.messagingControl == "Nobody") {
            return SendMessageResult.Error("This user has disabled anonymous messages.")
        }

        val currentSenderToken = senderToken

        // 2. Check if sender is blocked by recipient
        val isBlocked = dao.isSenderBlocked(recipientUsername, currentSenderToken) > 0
        if (isBlocked) {
            // Silently block or friendly message as requested in Section 11
            return SendMessageResult.Error("Messages from this sender have been blocked.")
        }

        // 3. Check rate limit
        val adminSettings = dao.getAdminSettings().first() ?: AdminSettingsEntity()
        val oneHourAgo = System.currentTimeMillis() - (1000 * 60 * 60)
        val recentCount = dao.getRecentMessageCountFromSender(currentSenderToken, recipientUsername, oneHourAgo)
        if (recentCount >= adminSettings.rateLimitPerHour) {
            return SendMessageResult.Error("Please wait a little before sending another message.")
        }

        // 4. Duplicate message check (within last 3 minutes)
        val threeMinutesAgo = System.currentTimeMillis() - (1000 * 60 * 3)
        val duplicate = dao.findRecentDuplicate(currentSenderToken, recipientUsername, trimmed, threeMinutesAgo)
        if (duplicate != null) {
            return SendMessageResult.Error("You've already sent this message recently.")
        }

        // 5. Content moderation
        val moderation = ModerationEngine.evaluateContent(trimmed, adminSettings.moderationSensitivity)
        if (!moderation.isAllowed) {
            return SendMessageResult.Error(moderation.reason ?: "Message blocked due to safety guidelines.")
        }

        // 6. Save message securely
        val message = MessageEntity(
            recipientUsername = recipientUsername,
            content = trimmed,
            senderToken = currentSenderToken,
            moderationStatus = moderation.status,
            promptQuestion = promptQuestion.ifEmpty { "Ask me anything" }
        )
        val insertedId = dao.insertMessage(message)
        return SendMessageResult.Success(insertedId)
    }

    suspend fun markMessageRead(id: Long, isRead: Boolean) = dao.setMessageRead(id, isRead)
    suspend fun archiveMessage(id: Long, isArchived: Boolean) = dao.setMessageArchived(id, isArchived)
    suspend fun deleteMessage(id: Long) = dao.deleteMessage(id)

    suspend fun blockSenderOfMessage(messageId: Long, currentUsername: String): Boolean {
        val message = dao.getMessageById(messageId) ?: return false
        dao.blockSender(
            BlockedSenderEntity(
                userUsername = currentUsername,
                senderToken = message.senderToken
            )
        )
        return true
    }

    suspend fun reportMessage(
        messageId: Long,
        category: String,
        notes: String = ""
    ): Boolean {
        val message = dao.getMessageById(messageId) ?: return false
        dao.flagMessageReported(messageId)
        dao.insertReport(
            ReportEntity(
                messageId = messageId,
                reportedUsername = message.recipientUsername,
                category = category,
                notes = notes,
                messageContentPreview = message.content.take(100)
            )
        )
        return true
    }

    // --- REPLIES ---
    fun getReplyForMessage(messageId: Long): Flow<ReplyEntity?> = dao.getReplyForMessage(messageId)
    fun getAllRepliesFlow(): Flow<List<ReplyEntity>> = dao.getAllReplies()

    suspend fun sendReply(messageId: Long, replyText: String): Long {
        return dao.insertReply(
            ReplyEntity(
                messageId = messageId,
                replyText = replyText.trim()
            )
        )
    }

    // --- BLOCKED SENDERS & SAFETY ---
    fun getBlockedSenders(username: String): Flow<List<BlockedSenderEntity>> = dao.getBlockedSendersForUser(username)
    suspend fun unblockSender(id: Long) = dao.unblockSenderById(id)

    // --- ADMIN ---
    fun getAdminReports(): Flow<List<ReportEntity>> = dao.getAllReports()
    fun getReportedMessages(): Flow<List<MessageEntity>> = dao.getReportedMessages()
    fun getTotalUsersCount(): Flow<Int> = dao.getUserCount()
    fun getTotalMessagesCount(): Flow<Int> = dao.getTotalMessageCount()
    fun getTotalBlockedCount(): Flow<Int> = dao.getTotalBlockedSendersCount()
    fun getAdminSettings(): Flow<AdminSettingsEntity?> = dao.getAdminSettings()

    suspend fun updateAdminSettings(settings: AdminSettingsEntity) = dao.insertOrUpdateAdminSettings(settings)
    suspend fun updateReportStatus(id: Long, status: String) = dao.updateReportStatus(id, status)
    suspend fun updateMessageModeration(messageId: Long, status: String) = dao.updateMessageModerationStatus(messageId, status)
}
