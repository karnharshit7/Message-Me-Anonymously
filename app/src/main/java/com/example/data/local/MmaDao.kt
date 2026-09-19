package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MmaDao {

    // --- USERS ---
    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE isDiscoverable = 1 AND isBanned = 0 AND isSuspended = 0 ORDER BY profileViews DESC")
    fun getDiscoverableUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    fun observeUserByUsername(username: String): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("DELETE FROM users WHERE username = :username")
    suspend fun deleteUser(username: String)

    @Query("UPDATE users SET profileViews = profileViews + 1 WHERE username = :username")
    suspend fun incrementProfileViews(username: String)

    @Query("UPDATE users SET sharesCount = sharesCount + 1 WHERE username = :username")
    suspend fun incrementSharesCount(username: String)

    @Query("SELECT COUNT(*) FROM users")
    fun getUserCount(): Flow<Int>

    // --- MESSAGES ---
    @Query("SELECT * FROM messages WHERE recipientUsername = :username AND isDeleted = 0 ORDER BY createdAt DESC")
    fun getMessagesForUser(username: String): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE recipientUsername = :username AND isDeleted = 0 AND isArchived = 0 ORDER BY createdAt DESC")
    fun getActiveMessagesForUser(username: String): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE recipientUsername = :username AND isDeleted = 0 AND isArchived = 1 ORDER BY createdAt DESC")
    fun getArchivedMessagesForUser(username: String): Flow<List<MessageEntity>>

    @Query("SELECT COUNT(*) FROM messages WHERE recipientUsername = :username AND isDeleted = 0 AND isRead = 0")
    fun getUnreadCount(username: String): Flow<Int>

    @Query("SELECT * FROM messages WHERE id = :id LIMIT 1")
    suspend fun getMessageById(id: Long): MessageEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity): Long

    @Query("UPDATE messages SET isRead = :isRead WHERE id = :id")
    suspend fun setMessageRead(id: Long, isRead: Boolean)

    @Query("UPDATE messages SET isArchived = :isArchived WHERE id = :id")
    suspend fun setMessageArchived(id: Long, isArchived: Boolean)

    @Query("UPDATE messages SET isDeleted = 1 WHERE id = :id")
    suspend fun deleteMessage(id: Long)

    @Query("UPDATE messages SET isReported = 1, reportCount = reportCount + 1 WHERE id = :id")
    suspend fun flagMessageReported(id: Long)

    @Query("SELECT COUNT(*) FROM messages WHERE senderToken = :senderToken AND recipientUsername = :recipientUsername AND createdAt > :sinceTimestamp")
    suspend fun getRecentMessageCountFromSender(senderToken: String, recipientUsername: String, sinceTimestamp: Long): Int

    @Query("SELECT * FROM messages WHERE senderToken = :senderToken AND recipientUsername = :recipientUsername AND content = :content AND createdAt > :sinceTimestamp LIMIT 1")
    suspend fun findRecentDuplicate(senderToken: String, recipientUsername: String, content: String, sinceTimestamp: Long): MessageEntity?

    @Query("SELECT COUNT(*) FROM messages")
    fun getTotalMessageCount(): Flow<Int>

    @Query("SELECT * FROM messages WHERE isReported = 1 ORDER BY createdAt DESC")
    fun getReportedMessages(): Flow<List<MessageEntity>>

    @Query("UPDATE messages SET moderationStatus = :status WHERE id = :id")
    suspend fun updateMessageModerationStatus(id: Long, status: String)

    // --- REPLIES ---
    @Query("SELECT * FROM message_replies WHERE messageId = :messageId ORDER BY createdAt DESC LIMIT 1")
    fun getReplyForMessage(messageId: Long): Flow<ReplyEntity?>

    @Query("SELECT * FROM message_replies ORDER BY createdAt DESC")
    fun getAllReplies(): Flow<List<ReplyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReply(reply: ReplyEntity): Long

    // --- REPORTS ---
    @Query("SELECT * FROM message_reports ORDER BY createdAt DESC")
    fun getAllReports(): Flow<List<ReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: ReportEntity): Long

    @Query("UPDATE message_reports SET status = :status WHERE id = :id")
    suspend fun updateReportStatus(id: Long, status: String)

    @Query("SELECT COUNT(*) FROM message_reports WHERE status = 'PENDING'")
    fun getPendingReportCount(): Flow<Int>

    // --- BLOCKED SENDERS ---
    @Query("SELECT * FROM blocked_senders WHERE userUsername = :userUsername ORDER BY blockedAt DESC")
    fun getBlockedSendersForUser(userUsername: String): Flow<List<BlockedSenderEntity>>

    @Query("SELECT COUNT(*) FROM blocked_senders WHERE userUsername = :userUsername AND senderToken = :senderToken")
    suspend fun isSenderBlocked(userUsername: String, senderToken: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun blockSender(blockedSender: BlockedSenderEntity): Long

    @Query("DELETE FROM blocked_senders WHERE id = :id")
    suspend fun unblockSenderById(id: Long)

    @Query("DELETE FROM blocked_senders WHERE userUsername = :userUsername AND senderToken = :senderToken")
    suspend fun unblockSender(userUsername: String, senderToken: String)

    @Query("SELECT COUNT(*) FROM blocked_senders")
    fun getTotalBlockedSendersCount(): Flow<Int>

    // --- ADMIN SETTINGS ---
    @Query("SELECT * FROM admin_settings WHERE id = 1 LIMIT 1")
    fun getAdminSettings(): Flow<AdminSettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAdminSettings(settings: AdminSettingsEntity)
}
