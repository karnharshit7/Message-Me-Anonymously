package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material.icons.rounded.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.MmaAvatar
import com.example.ui.components.MmaGradientButton
import com.example.ui.components.MmaHeaderLogo
import com.example.ui.theme.MmaPink
import com.example.ui.theme.MmaPurple

@Composable
fun AuthOnboardingScreen(
    onCompleteAuth: (username: String) -> Unit,
    onRegisterNew: (
        username: String,
        displayName: String,
        bio: String,
        avatarUrl: String,
        email: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Sign Up / Onboard, 1: Quick Log In

    // Onboarding Form States
    var currentStep by remember { mutableIntStateOf(1) } // 1: Username, 2: Avatar & Name, 3: Bio, 4: Ready
    var username by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("Ask me anything anonymously 👀") }
    var selectedAvatar by remember { mutableStateOf("avatar_1") }
    var email by remember { mutableStateOf("") }
    var ageConfirmed by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // Quick Login states
    var loginUsername by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        // Top Back Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            MmaHeaderLogo(compact = true)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tabs: Sign Up vs Login
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MmaPurple
        ) {
            Tab(
                selected = (selectedTab == 0),
                onClick = { selectedTab = 0 },
                text = { Text("Create Account", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = (selectedTab == 1),
                onClick = { selectedTab = 1 },
                text = { Text("Log In", fontWeight = FontWeight.Bold) }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (selectedTab == 0) {
            // ONBOARDING WIZARD (Steps 1 - 4)
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "onboarding_step"
            ) { step ->
                when (step) {
                    1 -> {
                        // Step 1: Choose your username
                        Column {
                            Text(
                                text = "Step 1 of 4",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MmaPurple
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Choose your username",
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "This will be your personal link for anonymous messages.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            OutlinedTextField(
                                value = username,
                                onValueChange = {
                                    username = it.filter { ch -> ch.isLetterOrDigit() || ch == '_' }.lowercase()
                                    errorMessage = null
                                },
                                label = { Text("Username") },
                                placeholder = { Text("e.g. harshit") },
                                prefix = { Text("@") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )

                            Text(
                                text = "messagemeanonymous.com/u/${username.ifEmpty { "your_name" }}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MmaPink,
                                modifier = Modifier.padding(top = 6.dp, start = 4.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Email (Private)") },
                                placeholder = { Text("you@example.com") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Age Verification Checkbox (Section 23 Age Safety)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = ageConfirmed,
                                    onCheckedChange = { ageConfirmed = it }
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "I confirm that I am at least 13 years old and agree to the Community Guidelines.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            if (errorMessage != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = errorMessage!!,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            MmaGradientButton(
                                text = "Next Step",
                                onClick = {
                                    if (username.length < 3) {
                                        errorMessage = "Username must be at least 3 characters."
                                        return@MmaGradientButton
                                    }
                                    if (!ageConfirmed) {
                                        errorMessage = "You must be at least 13 years old to use MMA."
                                        return@MmaGradientButton
                                    }
                                    displayName = username.replaceFirstChar { it.uppercase() }
                                    currentStep = 2
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    2 -> {
                        // Step 2: Add profile picture and Display Name
                        Column {
                            Text(
                                text = "Step 2 of 4",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MmaPurple
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Choose your avatar",
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "Pick an avatar and enter how your name should display.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // Avatar selector row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                listOf("avatar_1", "avatar_2", "avatar_3", "avatar_text").forEach { av ->
                                    val isSelected = (selectedAvatar == av)
                                    Box(
                                        modifier = Modifier
                                            .size(68.dp)
                                            .clip(CircleShape)
                                            .border(
                                                width = if (isSelected) 3.dp else 1.dp,
                                                color = if (isSelected) MmaPink else Color.Gray.copy(alpha = 0.4f),
                                                shape = CircleShape
                                            )
                                            .clickable { selectedAvatar = av },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        MmaAvatar(
                                            avatarUrl = av,
                                            name = displayName.ifEmpty { "M" },
                                            size = 60
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            OutlinedTextField(
                                value = displayName,
                                onValueChange = { displayName = it },
                                label = { Text("Display Name") },
                                placeholder = { Text("e.g. Harshit K.") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Row(modifier = Modifier.fillMaxWidth()) {
                                OutlinedButton(
                                    onClick = { currentStep = 1 },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(26.dp)
                                ) {
                                    Text("Back")
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                MmaGradientButton(
                                    text = "Continue",
                                    onClick = { currentStep = 3 },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    3 -> {
                        // Step 3: Write a short bio
                        Column {
                            Text(
                                text = "Step 3 of 4",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MmaPurple
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Write a short bio",
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "Let friends know what to ask or say.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            OutlinedTextField(
                                value = bio,
                                onValueChange = { bio = it },
                                label = { Text("Bio") },
                                placeholder = { Text("Ask me anything anonymously 👀") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 3,
                                maxLines = 4,
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Quick bio suggestions
                            Text(
                                text = "Quick suggestions:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            listOf(
                                "Ask me anything anonymously 👀",
                                "Send honest feedback or confessions ✨",
                                "Tell me something you've never said before 💭"
                            ).forEach { suggestion ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { bio = suggestion }
                                        .padding(vertical = 4.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                ) {
                                    Text(
                                        text = suggestion,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }

                            if (errorMessage != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = errorMessage!!,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Row(modifier = Modifier.fillMaxWidth()) {
                                OutlinedButton(
                                    onClick = { currentStep = 2 },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(26.dp)
                                ) {
                                    Text("Back")
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                MmaGradientButton(
                                    text = "Finish Setup",
                                    isLoading = isLoading,
                                    onClick = {
                                        isLoading = true
                                        onRegisterNew(
                                            username,
                                            displayName,
                                            bio,
                                            selectedAvatar,
                                            email,
                                            {
                                                isLoading = false
                                                currentStep = 4
                                            },
                                            { err ->
                                                isLoading = false
                                                errorMessage = err
                                            }
                                        )
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    4 -> {
                        // Step 4: Your anonymous link is ready!
                        val profileUrl = "https://messagemeanonymous.com/u/$username"
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(MmaPurple.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MmaPink,
                                    modifier = Modifier.size(40.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Your anonymous link is ready! 🎉",
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Share it with your friends on Instagram, Snapchat, or WhatsApp.",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Your link:",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = profileUrl,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MmaPurple,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Row(modifier = Modifier.fillMaxWidth()) {
                                OutlinedButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("MMA Link", profileUrl))
                                        Toast.makeText(context, "Link copied!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(26.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Copy Link")
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Button(
                                    onClick = {
                                        val sendIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(
                                                Intent.EXTRA_TEXT,
                                                "Send me an anonymous message 👀\nNo name. No judgment.\n$profileUrl"
                                            )
                                            type = "text/plain"
                                        }
                                        context.startActivity(Intent.createChooser(sendIntent, "Share your MMA link"))
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = MmaPurple),
                                    shape = RoundedCornerShape(26.dp)
                                ) {
                                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Share Link")
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            MmaGradientButton(
                                text = "Go to Dashboard",
                                onClick = { onCompleteAuth(username) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        } else {
            // LOG IN TAB
            Column {
                Text(
                    text = "Welcome back",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Sign in to access your anonymous inbox.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = loginUsername,
                    onValueChange = { loginUsername = it.filter { ch -> ch.isLetterOrDigit() || ch == '_' }.lowercase() },
                    label = { Text("Username") },
                    placeholder = { Text("e.g. harshit") },
                    prefix = { Text("@") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                MmaGradientButton(
                    text = "Log In",
                    onClick = {
                        if (loginUsername.isNotBlank()) {
                            onCompleteAuth(loginUsername.trim())
                        } else {
                            onCompleteAuth("harshit")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = "Or quick-switch to demo accounts:",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))

                listOf(
                    Pair("harshit", "Harshit (Default Profile with 4 messages)"),
                    Pair("maya_dev", "Maya Chen (@maya_dev)"),
                    Pair("alex_sky", "Alex Vance (@alex_sky)")
                ).forEach { (u, label) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCompleteAuth(u) }
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            MmaAvatar(avatarUrl = "avatar_1", name = u, size = 38)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Tap to login as @$u",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MmaPurple
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
