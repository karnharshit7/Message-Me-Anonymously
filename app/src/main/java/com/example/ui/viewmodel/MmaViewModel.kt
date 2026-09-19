package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AdminSettingsEntity
import com.example.data.local.AppDatabase
import com.example.data.local.BlockedSenderEntity
import com.example.data.local.MessageEntity
import com.example.data.local.ReplyEntity
import com.example.data.local.ReportEntity
import com.example.data.local.UserEntity
import com.example.data.repository.MmaRepository
import com.example.data.repository.SendMessageResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class MmaViewModel(application: Application) : AndroidViewModel(application) {

    val repository: MmaRepository

    // Currently logged-in username (or null if guest / landing)
    private val _currentUsername = MutableStateFlow<String?>("harshit")
    val currentUsername: StateFlow<String?> = _currentUsername.asStateFlow()

    // Navigation sub-route or screen selection
    private val _currentScreen = MutableStateFlow<String>("home") // landing, home, inbox, discover, profile, settings, send_message, share_card, admin
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    // Profile being targeted for sending an anonymous message
    private val _targetProfileUsername = MutableStateFlow<String>("harshit")
    val targetProfileUsername: StateFlow<String> = _targetProfileUsername.asStateFlow()

    // Active message for reply dialog
    private val _activeReplyMessage = MutableStateFlow<MessageEntity?>(null)
    val activeReplyMessage: StateFlow<MessageEntity?> = _activeReplyMessage.asStateFlow()

    // Active message & reply for story/card share generator
    private val _activeSharePayload = MutableStateFlow<Pair<MessageEntity, String?>?>(null)
    val activeSharePayload: StateFlow<Pair<MessageEntity, String?>?> = _activeSharePayload.asStateFlow()

    // Active message for reporting dialog
    private val _activeReportMessage = MutableStateFlow<MessageEntity?>(null)
    val activeReportMessage: StateFlow<MessageEntity?> = _activeReportMessage.asStateFlow()

    // UI Feedback Banner / Snackbar
    private val _userFeedbackMessage = MutableStateFlow<String?>(null)
    val userFeedbackMessage: StateFlow<String?> = _userFeedbackMessage.asStateFlow()

    // Reply mapping by messageId
    private val _repliesMap = MutableStateFlow<Map<Long, String>>(emptyMap())
    val repliesMap: StateFlow<Map<Long, String>> = _repliesMap.asStateFlow()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = MmaRepository(db.mmaDao(), application)

        viewModelScope.launch {
            repository.seedInitialDataIfNeeded()
            loadAllReplies()
        }
    }

    private fun loadAllReplies() {
        viewModelScope.launch {
            repository.getAllRepliesFlow().collect { replies ->
                val map = replies.associate { it.messageId to it.replyText }
                _repliesMap.value = map
            }
        }
    }

    // Current User observation
    val currentUser: StateFlow<UserEntity?> = _currentUsername
        .flatMapLatest { username ->
            if (username != null) repository.observeUser(username) else flowOf(null)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Messages for current user
    val activeMessages: StateFlow<List<MessageEntity>> = _currentUsername
        .flatMapLatest { username ->
            if (username != null) repository.getMessagesForUser(username) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val archivedMessages: StateFlow<List<MessageEntity>> = _currentUsername
        .flatMapLatest { username ->
            if (username != null) repository.getArchivedMessagesForUser(username) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadCount: StateFlow<Int> = _currentUsername
        .flatMapLatest { username ->
            if (username != null) repository.getUnreadCount(username) else flowOf(0)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val discoverableUsers: StateFlow<List<UserEntity>> = repository.getDiscoverableUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUsers: StateFlow<List<UserEntity>> = repository.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val blockedSenders: StateFlow<List<BlockedSenderEntity>> = _currentUsername
        .flatMapLatest { username ->
            if (username != null) repository.getBlockedSenders(username) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminSettings: StateFlow<AdminSettingsEntity?> = repository.getAdminSettings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val adminReports: StateFlow<List<ReportEntity>> = repository.getAdminReports()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val reportedMessages: StateFlow<List<MessageEntity>> = repository.getReportedMessages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalUsersCount: StateFlow<Int> = repository.getTotalUsersCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalMessagesCount: StateFlow<Int> = repository.getTotalMessagesCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalBlockedCount: StateFlow<Int> = repository.getTotalBlockedCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Navigation & Screen control
    fun navigateTo(screen: String) {
        _currentScreen.value = screen
    }

    fun openSendScreenFor(username: String) {
        _targetProfileUsername.value = username
        _currentScreen.value = "send_message"
    }

    fun switchAccount(username: String) {
        _currentUsername.value = username
        showFeedback("Switched to @$username")
    }

    fun logout() {
        _currentUsername.value = null
        _currentScreen.value = "landing"
        showFeedback("Logged out")
    }

    fun loginAs(username: String) {
        _currentUsername.value = username
        _currentScreen.value = "home"
        showFeedback("Welcome back, @$username!")
    }

    fun registerNewUser(
        username: String,
        displayName: String,
        bio: String,
        avatarUrl: String,
        email: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.registerUser(username, displayName, bio, avatarUrl, email)
            result.onSuccess { user ->
                _currentUsername.value = user.username
                _currentScreen.value = "home"
                showFeedback("Welcome to MMA, @${user.username}!")
                onSuccess()
            }.onFailure { ex ->
                onError(ex.message ?: "Failed to create account.")
            }
        }
    }

    fun sendAnonymousMessage(
        recipientUsername: String,
        content: String,
        promptQuestion: String,
        onComplete: (SendMessageResult) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.sendMessage(recipientUsername, content, promptQuestion)
            if (result is SendMessageResult.Success) {
                repository.incrementViews(recipientUsername)
            }
            onComplete(result)
        }
    }

    fun toggleMessageRead(messageId: Long, isRead: Boolean) {
        viewModelScope.launch {
            repository.markMessageRead(messageId, isRead)
        }
    }

    fun toggleMessageArchive(messageId: Long, isArchived: Boolean) {
        viewModelScope.launch {
            repository.archiveMessage(messageId, isArchived)
            showFeedback(if (isArchived) "Message archived" else "Message unarchived")
        }
    }

    fun deleteMessage(messageId: Long) {
        viewModelScope.launch {
            repository.deleteMessage(messageId)
            showFeedback("Message deleted")
        }
    }

    fun startReply(message: MessageEntity) {
        _activeReplyMessage.value = message
    }

    fun cancelReply() {
        _activeReplyMessage.value = null
    }

    fun submitReply(messageId: Long, replyText: String) {
        viewModelScope.launch {
            repository.sendReply(messageId, replyText)
            _activeReplyMessage.value = null
            showFeedback("Reply saved!")
        }
    }

    fun openStoryCardGenerator(message: MessageEntity, replyText: String? = null) {
        _activeSharePayload.value = Pair(message, replyText)
        _currentScreen.value = "share_card"
    }

    fun closeStoryCardGenerator() {
        _activeSharePayload.value = null
        _currentScreen.value = "inbox"
    }

    fun startReport(message: MessageEntity) {
        _activeReportMessage.value = message
    }

    fun cancelReport() {
        _activeReportMessage.value = null
    }

    fun submitReport(messageId: Long, category: String, notes: String) {
        viewModelScope.launch {
            repository.reportMessage(messageId, category, notes)
            _activeReportMessage.value = null
            showFeedback("Thanks. We’ll review this message.")
        }
    }

    fun blockSender(messageId: Long) {
        viewModelScope.launch {
            val user = _currentUsername.value ?: return@launch
            repository.blockSenderOfMessage(messageId, user)
            showFeedback("Messages from this sender have been blocked.")
        }
    }

    fun unblockSender(blockedSenderId: Long) {
        viewModelScope.launch {
            repository.unblockSender(blockedSenderId)
            showFeedback("Sender unblocked")
        }
    }

    fun updateUserProfile(updatedUser: UserEntity) {
        viewModelScope.launch {
            repository.updateUserProfile(updatedUser)
            showFeedback("Profile updated successfully")
        }
    }

    fun deleteCurrentAccount() {
        viewModelScope.launch {
            val username = _currentUsername.value ?: return@launch
            repository.deleteUserAccount(username)
            _currentUsername.value = null
            _currentScreen.value = "landing"
            showFeedback("Your account has been deleted.")
        }
    }

    fun incrementShareCount() {
        viewModelScope.launch {
            val username = _currentUsername.value ?: return@launch
            repository.incrementShares(username)
        }
    }

    // Admin operations
    fun updateAdminSettings(settings: AdminSettingsEntity) {
        viewModelScope.launch {
            repository.updateAdminSettings(settings)
            showFeedback("System settings saved")
        }
    }

    fun resolveReport(reportId: Long, status: String) {
        viewModelScope.launch {
            repository.updateReportStatus(reportId, status)
            showFeedback("Report marked as $status")
        }
    }

    fun updateMessageModeration(messageId: Long, status: String) {
        viewModelScope.launch {
            repository.updateMessageModeration(messageId, status)
            showFeedback("Message moderation set to $status")
        }
    }

    fun showFeedback(msg: String) {
        _userFeedbackMessage.value = msg
    }

    fun clearFeedback() {
        _userFeedbackMessage.value = null
    }
}
