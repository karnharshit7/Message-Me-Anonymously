package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
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
import com.example.data.local.AdminSettingsEntity
import com.example.data.local.MessageEntity
import com.example.data.local.ReportEntity
import com.example.data.local.UserEntity
import com.example.ui.components.MmaAvatar
import com.example.ui.theme.MmaPink
import com.example.ui.theme.MmaPurple

@Composable
fun AdminPanelScreen(
    totalUsers: Int,
    totalMessages: Int,
    totalBlocked: Int,
    reports: List<ReportEntity>,
    reportedMessages: List<MessageEntity>,
    allUsers: List<UserEntity>,
    adminSettings: AdminSettingsEntity?,
    onBack: () -> Unit,
    onResolveReport: (reportId: Long, status: String) -> Unit,
    onUpdateMessageModeration: (messageId: Long, status: String) -> Unit,
    onSaveSettings: (AdminSettingsEntity) -> Unit
) {
    val scrollState = rememberScrollState()
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Reports & Moderation, 1: Anti-Spam & Settings, 2: Users

    // Local mutable state for settings
    var rateLimit by remember(adminSettings) { mutableStateOf(adminSettings?.rateLimitPerHour?.toFloat() ?: 10f) }
    var sensitivity by remember(adminSettings) { mutableStateOf(adminSettings?.moderationSensitivity ?: "HIGH") }
    var isMaintenance by remember(adminSettings) { mutableStateOf(adminSettings?.isMaintenanceMode ?: false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = MmaPink)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "MMA Admin Center",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)
                )
                Text(
                    text = "Safety, Content Moderation & Anti-Spam",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Stats Overview
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AdminStatBox(modifier = Modifier.weight(1f), label = "Users", value = totalUsers.toString())
            AdminStatBox(modifier = Modifier.weight(1f), label = "Messages", value = totalMessages.toString())
            AdminStatBox(modifier = Modifier.weight(1f), label = "Reports", value = reports.count { it.status == "PENDING" }.toString())
            AdminStatBox(modifier = Modifier.weight(1f), label = "Blocked", value = totalBlocked.toString())
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MmaPurple
        ) {
            Tab(
                selected = (selectedTab == 0),
                onClick = { selectedTab = 0 },
                text = { Text("Reports (${reports.size})", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = (selectedTab == 1),
                onClick = { selectedTab = 1 },
                text = { Text("Settings", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = (selectedTab == 2),
                onClick = { selectedTab = 2 },
                text = { Text("Users", fontWeight = FontWeight.Bold) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedTab) {
            0 -> {
                // Reports & Moderation Queue
                Text(
                    text = "Pending User Reports",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (reports.isEmpty()) {
                    Text(
                        text = "No reports submitted. All safe!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    reports.forEach { report ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(MmaPink.copy(alpha = 0.2f))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = report.category.uppercase(),
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MmaPink
                                        )
                                    }
                                    Text(
                                        text = "Status: ${report.status}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (report.status == "PENDING") MmaPink else Color.Green
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Target profile: @${report.reportedUsername}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                                )

                                Text(
                                    text = "\"${report.messageContentPreview}\"",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                if (report.notes.isNotBlank()) {
                                    Text(
                                        text = "Reporter note: ${report.notes}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    OutlinedButton(
                                        onClick = { onResolveReport(report.id, "DISMISSED") },
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Dismiss")
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = {
                                            onResolveReport(report.id, "RESOLVED")
                                            onUpdateMessageModeration(report.messageId, "BLOCKED")
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MmaPink),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Block Message")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // Anti-Spam & System Settings
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Rate Limiting",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Max anonymous messages per hour per sender token: ${rateLimit.toInt()}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Slider(
                            value = rateLimit,
                            onValueChange = { rateLimit = it },
                            valueRange = 3f..30f,
                            steps = 26
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Moderation Filter Sensitivity",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("LOW", "MEDIUM", "HIGH").forEach { level ->
                                val isSel = (sensitivity == level)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSel) MmaPurple else MaterialTheme.colorScheme.surfaceVariant)
                                        .clickable { sensitivity = level }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = level,
                                        color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Maintenance Mode", fontWeight = FontWeight.Bold)
                                Text("Temporarily pause anonymous messaging globally", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = isMaintenance,
                                onCheckedChange = { isMaintenance = it }
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                onSaveSettings(
                                    AdminSettingsEntity(
                                        rateLimitPerHour = rateLimit.toInt(),
                                        maxCharacterLimit = 500,
                                        moderationSensitivity = sensitivity,
                                        isMaintenanceMode = isMaintenance
                                    )
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MmaPurple),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Save Anti-Spam & Moderation Rules")
                        }
                    }
                }
            }

            2 -> {
                // User Management
                Text(
                    text = "All Registered Users",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(10.dp))

                allUsers.forEach { u ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            MmaAvatar(avatarUrl = u.avatarUrl, name = u.displayName, size = 44)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(u.displayName, fontWeight = FontWeight.Bold)
                                Text("@${u.username} • ${u.messagingControl}", style = MaterialTheme.typography.bodySmall, color = MmaPink)
                                Text("${u.profileViews} views • ${u.sharesCount} shares", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun AdminStatBox(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                color = MmaPurple
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp
            )
        }
    }
}
