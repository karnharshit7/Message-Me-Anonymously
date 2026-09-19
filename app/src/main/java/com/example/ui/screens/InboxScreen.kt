package com.example.ui.screens

import android.text.format.DateUtils
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.MarkEmailUnread
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Unarchive
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.MessageEntity
import com.example.ui.theme.MmaPink
import com.example.ui.theme.MmaPrimaryGradient
import com.example.ui.theme.MmaPurple

@Composable
fun InboxScreen(
    activeMessages: List<MessageEntity>,
    archivedMessages: List<MessageEntity>,
    unreadCount: Int,
    repliesMap: Map<Long, String>,
    onReply: (MessageEntity) -> Unit,
    onShare: (MessageEntity, reply: String?) -> Unit,
    onToggleRead: (messageId: Long, isRead: Boolean) -> Unit,
    onToggleArchive: (messageId: Long, isArchived: Boolean) -> Unit,
    onDelete: (messageId: Long) -> Unit,
    onReport: (MessageEntity) -> Unit,
    onBlock: (messageId: Long) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: All, 1: Unread, 2: Archived

    val displayedMessages = when (selectedTab) {
        0 -> activeMessages
        1 -> activeMessages.filter { !it.isRead }
        2 -> archivedMessages
        else -> activeMessages
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Inbox Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Anonymous Inbox",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "$unreadCount unread • ${activeMessages.size} total messages",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MmaPurple
        ) {
            Tab(
                selected = (selectedTab == 0),
                onClick = { selectedTab = 0 },
                text = { Text("All (${activeMessages.size})", fontWeight = FontWeight.SemiBold) }
            )
            Tab(
                selected = (selectedTab == 1),
                onClick = { selectedTab = 1 },
                text = { Text("Unread ($unreadCount)", fontWeight = FontWeight.SemiBold) }
            )
            Tab(
                selected = (selectedTab == 2),
                onClick = { selectedTab = 2 },
                text = { Text("Archived (${archivedMessages.size})", fontWeight = FontWeight.SemiBold) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (displayedMessages.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(MmaPurple.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("💌", fontSize = 28.sp)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (selectedTab == 2) "No archived messages" else "No messages here yet",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (selectedTab == 2) "Messages you archive will show up here." else "Share your personal MMA link on Instagram or Snapchat to receive honest messages!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                items(displayedMessages, key = { it.id }) { message ->
                    val reply = repliesMap[message.id]
                    MessageCard(
                        message = message,
                        replyText = reply,
                        onReply = { onReply(message) },
                        onShare = { onShare(message, reply) },
                        onToggleRead = { onToggleRead(message.id, !message.isRead) },
                        onToggleArchive = { onToggleArchive(message.id, !message.isArchived) },
                        onDelete = { onDelete(message.id) },
                        onReport = { onReport(message) },
                        onBlock = { onBlock(message.id) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun MessageCard(
    message: MessageEntity,
    replyText: String?,
    onReply: () -> Unit,
    onShare: () -> Unit,
    onToggleRead: () -> Unit,
    onToggleArchive: () -> Unit,
    onDelete: () -> Unit,
    onReport: () -> Unit,
    onBlock: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    val timeAgo = DateUtils.getRelativeTimeSpanString(
        message.createdAt,
        System.currentTimeMillis(),
        DateUtils.MINUTE_IN_MILLIS
    ).toString()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (!message.isRead) 1.5.dp else 0.5.dp,
                color = if (!message.isRead) MmaPurple.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                shape = RoundedCornerShape(18.dp)
            ),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (!message.isRead) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Anonymous badge + Time + Menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MmaPurple.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "ANONYMOUS",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MmaPink,
                            fontSize = 10.sp
                        )
                    }

                    if (!message.isRead) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(MmaPurple)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = timeAgo,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Box {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More actions",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(if (message.isRead) "Mark as Unread" else "Mark as Read") },
                                leadingIcon = {
                                    Icon(
                                        if (message.isRead) Icons.Default.MarkEmailUnread else Icons.Default.MarkEmailRead,
                                        contentDescription = null
                                    )
                                },
                                onClick = {
                                    menuExpanded = false
                                    onToggleRead()
                                }
                            )

                            DropdownMenuItem(
                                text = { Text(if (message.isArchived) "Unarchive" else "Archive") },
                                leadingIcon = {
                                    Icon(
                                        if (message.isArchived) Icons.Default.Unarchive else Icons.Default.Archive,
                                        contentDescription = null
                                    )
                                },
                                onClick = {
                                    menuExpanded = false
                                    onToggleArchive()
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Report Message") },
                                leadingIcon = { Icon(Icons.Default.Flag, contentDescription = null, tint = MmaPink) },
                                onClick = {
                                    menuExpanded = false
                                    onReport()
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Block Sender") },
                                leadingIcon = { Icon(Icons.Default.Block, contentDescription = null, tint = Color.Red) },
                                onClick = {
                                    menuExpanded = false
                                    onBlock()
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Delete Message") },
                                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red) },
                                onClick = {
                                    menuExpanded = false
                                    onDelete()
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Message Content
            Text(
                text = message.content,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 17.sp,
                    lineHeight = 24.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            // Reply Preview if exists
            if (!replyText.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.background)
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = "Your Reply:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MmaPurple
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = replyText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons (Reply, Share, Delete, Report)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = onReply) {
                        Icon(Icons.Default.Reply, contentDescription = null, modifier = Modifier.size(16.dp), tint = MmaPurple)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (replyText != null) "Edit Reply" else "Reply", color = MmaPurple, fontWeight = FontWeight.Bold)
                    }

                    TextButton(onClick = onShare) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp), tint = MmaPink)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Share Story", color = MmaPink, fontWeight = FontWeight.Bold)
                    }
                }

                IconButton(onClick = onToggleRead) {
                    Icon(
                        imageVector = if (message.isRead) Icons.Default.MarkEmailUnread else Icons.Default.MarkEmailRead,
                        contentDescription = "Toggle read state",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
