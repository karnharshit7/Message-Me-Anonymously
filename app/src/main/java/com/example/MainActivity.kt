package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.ReplyModal
import com.example.ui.components.ReportModal
import com.example.ui.screens.AdminPanelScreen
import com.example.ui.screens.AuthOnboardingScreen
import com.example.ui.screens.DiscoverScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.InboxScreen
import com.example.ui.screens.LandingScreen
import com.example.ui.screens.LegalScreen
import com.example.ui.screens.ProfileSettingsScreen
import com.example.ui.screens.SendMessageScreen
import com.example.ui.screens.ShareStoryCardScreen
import com.example.ui.theme.MmaPink
import com.example.ui.theme.MmaPurple
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MmaViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MmaAppRoot()
            }
        }
    }
}

@Composable
fun MmaAppRoot(
    viewModel: MmaViewModel = viewModel()
) {
    val currentUsername by viewModel.currentUsername.collectAsState()
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    val activeMessages by viewModel.activeMessages.collectAsState()
    val archivedMessages by viewModel.archivedMessages.collectAsState()
    val unreadCount by viewModel.unreadCount.collectAsState()
    val discoverableUsers by viewModel.discoverableUsers.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val blockedSenders by viewModel.blockedSenders.collectAsState()
    val repliesMap by viewModel.repliesMap.collectAsState()

    val targetProfileUsername by viewModel.targetProfileUsername.collectAsState()
    val activeReplyMessage by viewModel.activeReplyMessage.collectAsState()
    val activeSharePayload by viewModel.activeSharePayload.collectAsState()
    val activeReportMessage by viewModel.activeReportMessage.collectAsState()
    val userFeedback by viewModel.userFeedbackMessage.collectAsState()

    val adminSettings by viewModel.adminSettings.collectAsState()
    val adminReports by viewModel.adminReports.collectAsState()
    val reportedMessages by viewModel.reportedMessages.collectAsState()
    val totalUsers by viewModel.totalUsersCount.collectAsState()
    val totalMessages by viewModel.totalMessagesCount.collectAsState()
    val totalBlocked by viewModel.totalBlockedCount.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var legalScreenTitle by remember { mutableStateOf<String?>(null) }

    // Handle feedback notifications
    LaunchedEffect(userFeedback) {
        userFeedback?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearFeedback()
        }
    }

    // Determine target recipient entity for Send screen
    val targetRecipientUser = remember(targetProfileUsername, allUsers) {
        allUsers.firstOrNull { it.username.equals(targetProfileUsername, ignoreCase = true) }
    }

    // Back handling
    BackHandler(enabled = currentScreen != "home" && currentScreen != "landing") {
        when (currentScreen) {
            "send_message", "share_card", "admin", "auth" -> {
                if (currentUsername != null) {
                    viewModel.navigateTo("home")
                } else {
                    viewModel.navigateTo("landing")
                }
            }
            "inbox", "discover", "settings" -> viewModel.navigateTo("home")
            else -> {
                if (legalScreenTitle != null) {
                    legalScreenTitle = null
                } else if (currentUsername != null) {
                    viewModel.navigateTo("home")
                } else {
                    viewModel.navigateTo("landing")
                }
            }
        }
    }

    val showBottomBar = currentUsername != null &&
            (currentScreen == "home" || currentScreen == "inbox" || currentScreen == "discover" || currentScreen == "settings")

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MmaPurple
                ) {
                    NavigationBarItem(
                        selected = (currentScreen == "home"),
                        onClick = { viewModel.navigateTo("home") },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MmaPink,
                            indicatorColor = MmaPurple.copy(alpha = 0.15f)
                        )
                    )

                    NavigationBarItem(
                        selected = (currentScreen == "inbox"),
                        onClick = { viewModel.navigateTo("inbox") },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (unreadCount > 0) {
                                        Badge(containerColor = MmaPink) {
                                            Text(unreadCount.toString(), color = Color.White)
                                        }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.ChatBubble, contentDescription = "Inbox")
                            }
                        },
                        label = { Text("Inbox") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MmaPink,
                            indicatorColor = MmaPurple.copy(alpha = 0.15f)
                        )
                    )

                    NavigationBarItem(
                        selected = (currentScreen == "discover"),
                        onClick = { viewModel.navigateTo("discover") },
                        icon = { Icon(Icons.Default.Explore, contentDescription = "Discover") },
                        label = { Text("Discover") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MmaPink,
                            indicatorColor = MmaPurple.copy(alpha = 0.15f)
                        )
                    )

                    NavigationBarItem(
                        selected = (currentScreen == "settings"),
                        onClick = { viewModel.navigateTo("settings") },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                        label = { Text("Profile") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MmaPink,
                            indicatorColor = MmaPurple.copy(alpha = 0.15f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Legal sub-screen overlay
            if (legalScreenTitle != null) {
                LegalScreen(
                    title = legalScreenTitle!!,
                    onBack = { legalScreenTitle = null }
                )
            } else {
                when (currentScreen) {
                    "landing" -> {
                        LandingScreen(
                            onGetStarted = { viewModel.navigateTo("auth") },
                            onSendAnonymousMessage = { username ->
                                viewModel.openSendScreenFor(username)
                            },
                            onOpenLegal = { title ->
                                legalScreenTitle = title
                            }
                        )
                    }

                    "auth" -> {
                        AuthOnboardingScreen(
                            onCompleteAuth = { username ->
                                viewModel.loginAs(username)
                            },
                            onRegisterNew = { username, displayName, bio, avatar, email, onSuccess, onError ->
                                viewModel.registerNewUser(
                                    username,
                                    displayName,
                                    bio,
                                    avatar,
                                    email,
                                    onSuccess,
                                    onError
                                )
                            },
                            onBack = {
                                if (currentUsername != null) viewModel.navigateTo("home") else viewModel.navigateTo("landing")
                            }
                        )
                    }

                    "home" -> {
                        HomeScreen(
                            user = currentUser,
                            messages = activeMessages,
                            unreadCount = unreadCount,
                            onNavigateToInbox = { viewModel.navigateTo("inbox") },
                            onOpenStoryShare = { msg ->
                                viewModel.openStoryCardGenerator(msg, repliesMap[msg.id])
                            },
                            onShareCountIncrement = {
                                viewModel.incrementShareCount()
                            }
                        )
                    }

                    "inbox" -> {
                        InboxScreen(
                            activeMessages = activeMessages,
                            archivedMessages = archivedMessages,
                            unreadCount = unreadCount,
                            repliesMap = repliesMap,
                            onReply = { msg -> viewModel.startReply(msg) },
                            onShare = { msg, reply -> viewModel.openStoryCardGenerator(msg, reply) },
                            onToggleRead = { id, isRead -> viewModel.toggleMessageRead(id, isRead) },
                            onToggleArchive = { id, isArchived -> viewModel.toggleMessageArchive(id, isArchived) },
                            onDelete = { id -> viewModel.deleteMessage(id) },
                            onReport = { msg -> viewModel.startReport(msg) },
                            onBlock = { id -> viewModel.blockSender(id) }
                        )
                    }

                    "discover" -> {
                        DiscoverScreen(
                            users = discoverableUsers,
                            currentUsername = currentUsername,
                            onOpenSendScreen = { username ->
                                viewModel.openSendScreenFor(username)
                            }
                        )
                    }

                    "settings" -> {
                        ProfileSettingsScreen(
                            user = currentUser,
                            blockedSenders = blockedSenders,
                            allUsers = allUsers,
                            onUpdateProfile = { updated -> viewModel.updateUserProfile(updated) },
                            onUnblockSender = { id -> viewModel.unblockSender(id) },
                            onSwitchAccount = { u -> viewModel.switchAccount(u) },
                            onLogout = { viewModel.logout() },
                            onDeleteAccount = { viewModel.deleteCurrentAccount() },
                            onOpenAdmin = { viewModel.navigateTo("admin") },
                            onOpenLegal = { title -> legalScreenTitle = title }
                        )
                    }

                    "send_message" -> {
                        SendMessageScreen(
                            targetUsername = targetProfileUsername,
                            recipientUser = targetRecipientUser,
                            onBack = {
                                if (currentUsername != null) viewModel.navigateTo("home") else viewModel.navigateTo("landing")
                            },
                            onSendMessage = { recipient, content, prompt, onResult ->
                                viewModel.sendAnonymousMessage(recipient, content, prompt, onResult)
                            },
                            onGetYourOwnLink = {
                                viewModel.navigateTo("auth")
                            }
                        )
                    }

                    "share_card" -> {
                        val payload = activeSharePayload
                        if (payload != null) {
                            ShareStoryCardScreen(
                                message = payload.first,
                                initialReply = payload.second,
                                user = currentUser,
                                onBack = { viewModel.closeStoryCardGenerator() },
                                onSaveReply = { reply ->
                                    viewModel.submitReply(payload.first.id, reply)
                                }
                            )
                        } else {
                            viewModel.navigateTo("inbox")
                        }
                    }

                    "admin" -> {
                        AdminPanelScreen(
                            totalUsers = totalUsers,
                            totalMessages = totalMessages,
                            totalBlocked = totalBlocked,
                            reports = adminReports,
                            reportedMessages = reportedMessages,
                            allUsers = allUsers,
                            adminSettings = adminSettings,
                            onBack = { viewModel.navigateTo("settings") },
                            onResolveReport = { id, status -> viewModel.resolveReport(id, status) },
                            onUpdateMessageModeration = { id, status -> viewModel.updateMessageModeration(id, status) },
                            onSaveSettings = { settings -> viewModel.updateAdminSettings(settings) }
                        )
                    }

                    else -> {
                        HomeScreen(
                            user = currentUser,
                            messages = activeMessages,
                            unreadCount = unreadCount,
                            onNavigateToInbox = { viewModel.navigateTo("inbox") },
                            onOpenStoryShare = { msg ->
                                viewModel.openStoryCardGenerator(msg, repliesMap[msg.id])
                            },
                            onShareCountIncrement = {
                                viewModel.incrementShareCount()
                            }
                        )
                    }
                }
            }

            // Global Reply Dialog
            activeReplyMessage?.let { msg ->
                ReplyModal(
                    messageContent = msg.content,
                    initialReply = repliesMap[msg.id] ?: "",
                    onDismiss = { viewModel.cancelReply() },
                    onSubmit = { replyText ->
                        viewModel.submitReply(msg.id, replyText)
                    }
                )
            }

            // Global Report Dialog
            activeReportMessage?.let { msg ->
                ReportModal(
                    messageContent = msg.content,
                    onDismiss = { viewModel.cancelReport() },
                    onSubmit = { category, notes ->
                        viewModel.submitReport(msg.id, category, notes)
                    }
                )
            }
        }
    }
}
