package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.MmaGradientButton
import com.example.ui.components.MmaHeaderLogo
import com.example.ui.theme.MmaPink
import com.example.ui.theme.MmaPrimaryGradient
import com.example.ui.theme.MmaPurple

@Composable
fun LandingScreen(
    onGetStarted: () -> Unit,
    onSendAnonymousMessage: (String) -> Unit,
    onOpenLegal: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    var targetUsernameInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(bottom = 32.dp)
    ) {
        // Top App Bar / Brand Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MmaHeaderLogo(compact = true)

            Button(
                onClick = onGetStarted,
                colors = ButtonDefaults.buttonColors(containerColor = MmaPurple),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("Sign In", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Hero Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Tagline chip
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(MmaPurple.copy(alpha = 0.15f))
                    .border(1.dp, MmaPurple.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Say it. Stay anonymous. 🤫",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MmaPink
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Message Me Anonymously",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 32.sp,
                    lineHeight = 38.sp
                ),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Get honest messages from your friends, completely anonymously.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Primary CTAs
            MmaGradientButton(
                text = "Get Started",
                onClick = onGetStarted,
                icon = Icons.Default.ArrowForward,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Direct Send Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Have a friend's link or username?",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = targetUsernameInput,
                            onValueChange = { targetUsernameInput = it.filter { ch -> ch.isLetterOrDigit() || ch == '_' } },
                            placeholder = { Text("e.g. harshit") },
                            prefix = { Text("@") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (targetUsernameInput.isNotBlank()) {
                                    onSendAnonymousMessage(targetUsernameInput.trim().lowercase())
                                } else {
                                    onSendAnonymousMessage("harshit")
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MmaPurple)
                        ) {
                            Text("Send")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        // 3 Simple Steps
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "How it works in 3 steps",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(16.dp))

            StepItem(
                number = "1",
                title = "Create your profile",
                description = "Choose your username and get your personalized anonymous messaging link in seconds."
            )
            Spacer(modifier = Modifier.height(12.dp))
            StepItem(
                number = "2",
                title = "Share your personal link",
                description = "Paste on your Instagram Story, Snapchat, WhatsApp, TikTok bio, or X."
            )
            Spacer(modifier = Modifier.height(12.dp))
            StepItem(
                number = "3",
                title = "Receive anonymous messages",
                description = "Read unvarnished truths and answer them with shareable response cards."
            )
        }

        Spacer(modifier = Modifier.height(36.dp))

        // Features Grid / Cards
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "Built for honesty & safety",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(14.dp))

            FeatureItem(
                icon = Icons.Default.Lock,
                title = "Completely anonymous messaging",
                subtitle = "No account required for senders. No tracking or identity revealing to recipients."
            )
            Spacer(modifier = Modifier.height(10.dp))
            FeatureItem(
                icon = Icons.Default.Link,
                title = "Personal profile link",
                subtitle = "Unique URL (messagemeanonymous.com/u/you) ready to share anywhere."
            )
            Spacer(modifier = Modifier.height(10.dp))
            FeatureItem(
                icon = Icons.Default.Share,
                title = "Share anywhere",
                subtitle = "Instagram Stories, Snapchat, WhatsApp, TikTok, Facebook, Messenger & X."
            )
            Spacer(modifier = Modifier.height(10.dp))
            FeatureItem(
                icon = Icons.Default.Reply,
                title = "Reply and share",
                subtitle = "Generate custom-styled story cards with your answers to share on socials."
            )
            Spacer(modifier = Modifier.height(10.dp))
            FeatureItem(
                icon = Icons.Default.Tune,
                title = "Message controls",
                subtitle = "Choose who can message you or pause anonymous messaging anytime."
            )
            Spacer(modifier = Modifier.height(10.dp))
            FeatureItem(
                icon = Icons.Default.Block,
                title = "Report and block",
                subtitle = "Instant blocking and comprehensive reporting for malicious or abusive senders."
            )
            Spacer(modifier = Modifier.height(10.dp))
            FeatureItem(
                icon = Icons.Default.Security,
                title = "Safe community tools",
                subtitle = "Automated anti-abuse, rate limits, toxic pattern filtering, and moderation."
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Footer & Legal
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Message Me Anonymously (MMA)",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Say it. Stay anonymous.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TextButton(onClick = { onOpenLegal("About") }) { Text("About") }
                TextButton(onClick = { onOpenLegal("Privacy Policy") }) { Text("Privacy Policy") }
                TextButton(onClick = { onOpenLegal("Terms of Service") }) { Text("Terms") }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TextButton(onClick = { onOpenLegal("Community Guidelines") }) { Text("Guidelines") }
                TextButton(onClick = { onOpenLegal("Safety Center") }) { Text("Safety") }
                TextButton(onClick = { onOpenLegal("Contact") }) { Text("Contact") }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "© 2026 Message Me Anonymously. All rights reserved.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun StepItem(
    number: String,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MmaPrimaryGradient),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun FeatureItem(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MmaPurple.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MmaPurple,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
