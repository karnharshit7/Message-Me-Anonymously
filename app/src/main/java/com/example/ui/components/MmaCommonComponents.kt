package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Stars
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MmaMidnightGradient
import com.example.ui.theme.MmaMinimalGradient
import com.example.ui.theme.MmaNeonGradient
import com.example.ui.theme.MmaOceanGradient
import com.example.ui.theme.MmaPink
import com.example.ui.theme.MmaPrimaryGradient
import com.example.ui.theme.MmaPurple
import com.example.ui.theme.MmaSunsetGradient

object MmaThemes {
    fun getGradient(themeName: String): Brush {
        return when (themeName.lowercase()) {
            "sunset" -> MmaSunsetGradient
            "ocean" -> MmaOceanGradient
            "neon" -> MmaNeonGradient
            "midnight" -> MmaMidnightGradient
            "minimal" -> MmaMinimalGradient
            else -> MmaPrimaryGradient
        }
    }

    fun getAccentColor(themeName: String): Color {
        return when (themeName.lowercase()) {
            "sunset" -> Color(0xFFFF5E62)
            "ocean" -> Color(0xFF00C6FF)
            "neon" -> Color(0xFF00F2FE)
            "midnight" -> Color(0xFF818CF8)
            "minimal" -> Color(0xFF94A3B8)
            else -> MmaPurple
        }
    }
}

@Composable
fun MmaHeaderLogo(
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Glowing brand mark
        Box(
            modifier = Modifier
                .size(if (compact) 36.dp else 44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MmaPrimaryGradient),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ChatBubbleOutline,
                contentDescription = "MMA Logo",
                tint = Color.White,
                modifier = Modifier.size(if (compact) 20.dp else 24.dp)
            )
        }

        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "MMA",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MmaPurple.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "ANON",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MmaPink,
                        fontSize = 9.sp
                    )
                }
            }
            if (!compact) {
                Text(
                    text = "Say it. Stay anonymous.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun MmaGradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    gradient: Brush = MmaPrimaryGradient,
    icon: ImageVector? = null,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    testTag: String = "mma_gradient_button"
) {
    Box(
        modifier = modifier
            .testTag(testTag)
            .height(52.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(if (enabled) gradient else Brush.linearGradient(listOf(Color.Gray, Color.DarkGray)))
            .clickable(enabled = enabled && !isLoading, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun MmaAvatar(
    avatarUrl: String,
    name: String,
    modifier: Modifier = Modifier,
    size: Int = 54,
    borderBrush: Brush = MmaPrimaryGradient
) {
    val initial = name.firstOrNull()?.uppercase() ?: "M"
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(borderBrush)
            .padding(2.5.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        val avatarIcon = when (avatarUrl) {
            "avatar_1" -> Icons.Rounded.Stars
            "avatar_2" -> Icons.Rounded.Favorite
            "avatar_3" -> Icons.Rounded.Psychology
            else -> null
        }

        if (avatarIcon != null) {
            Icon(
                imageVector = avatarIcon,
                contentDescription = "$name avatar",
                tint = MmaPurple,
                modifier = Modifier.size((size * 0.55).dp)
            )
        } else {
            Text(
                text = initial,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = (size * 0.42).sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun SocialShareRow(
    shareUrl: String,
    shareTitle: String,
    modifier: Modifier = Modifier,
    onShareCountIncrement: () -> Unit = {}
) {
    val context = LocalContext.current
    var copied by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Share link anywhere",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Quick action 1: Copy Link
            SocialPillButton(
                label = if (copied) "Copied!" else "Copy Link",
                icon = if (copied) Icons.Default.Done else Icons.Default.ContentCopy,
                color = MmaPurple,
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("MMA Link", shareUrl))
                    copied = true
                    Toast.makeText(context, "Link copied to clipboard!", Toast.LENGTH_SHORT).show()
                }
            )

            // Quick action 2: Native Share Sheet
            SocialPillButton(
                label = "Share",
                icon = Icons.Default.Share,
                color = MmaPink,
                onClick = {
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "$shareTitle\n\nSend me an anonymous message 👀\nNo name. No judgment.\n$shareUrl"
                        )
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, "Share your MMA link")
                    context.startActivity(shareIntent)
                    onShareCountIncrement()
                }
            )

            // Quick action 3: WhatsApp
            SocialPillButton(
                label = "WhatsApp",
                icon = Icons.Rounded.Send,
                color = Color(0xFF25D366),
                onClick = {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        putExtra(Intent.EXTRA_TEXT, "$shareTitle\nSend me an anonymous message: $shareUrl")
                        type = "text/plain"
                        setPackage("com.whatsapp")
                    }
                    try {
                        context.startActivity(intent)
                        onShareCountIncrement()
                    } catch (e: Exception) {
                        // Fallback to general share chooser
                        val chooser = Intent.createChooser(intent, "Share via WhatsApp")
                        context.startActivity(chooser)
                    }
                }
            )

            // Quick action 4: Instagram Story prompt text
            SocialPillButton(
                label = "Instagram",
                icon = Icons.Rounded.Stars,
                color = Color(0xFFE1306C),
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("Instagram Sticker Link", shareUrl))
                    Toast.makeText(context, "Link copied! Paste into your Instagram Story link sticker", Toast.LENGTH_LONG).show()
                    val launchIntent = context.packageManager.getLaunchIntentForPackage("com.instagram.android")
                    if (launchIntent != null) {
                        context.startActivity(launchIntent)
                    }
                    onShareCountIncrement()
                }
            )
        }
    }
}

@Composable
fun SocialPillButton(
    label: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f))
                .border(1.dp, color.copy(alpha = 0.4f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 11.sp
        )
    }
}

@Composable
fun ReportModal(
    messageContent: String,
    onDismiss: () -> Unit,
    onSubmit: (category: String, notes: String) -> Unit
) {
    val categories = listOf(
        "Harassment",
        "Bullying",
        "Threat",
        "Hate",
        "Sexual content",
        "Spam",
        "Scam",
        "Self-harm related",
        "Other"
    )
    var selectedCategory by remember { mutableStateOf(categories.first()) }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.WarningAmber,
                    contentDescription = null,
                    tint = MmaPink
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Report Message", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column {
                Text(
                    text = "Why are you reporting this anonymous message?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(8.dp)
                ) {
                    Text(
                        text = "\"$messageContent\"",
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                categories.take(5).forEach { cat ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedCategory = cat }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (selectedCategory == cat),
                            onClick = { selectedCategory = cat }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = cat, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Optional details") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(selectedCategory, notes) },
                colors = ButtonDefaults.buttonColors(containerColor = MmaPink)
            ) {
                Text("Submit Report", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ReplyModal(
    messageContent: String,
    initialReply: String = "",
    onDismiss: () -> Unit,
    onSubmit: (replyText: String) -> Unit
) {
    var replyText by remember { mutableStateOf(initialReply) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Reply to Anonymous Message", fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MmaPrimaryGradient)
                        .padding(12.dp)
                ) {
                    Text(
                        text = messageContent,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedTextField(
                    value = replyText,
                    onValueChange = { replyText = it },
                    placeholder = { Text("Type your response...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MmaPurple,
                        cursorColor = MmaPurple
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(replyText) },
                enabled = replyText.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = MmaPurple)
            ) {
                Text("Save Reply", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
