package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MmaPink
import com.example.ui.theme.MmaPurple

@Composable
fun LegalScreen(
    title: String,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    val content = when (title) {
        "Privacy Policy" -> """
# Privacy Policy for Message Me Anonymously (MMA)

Last updated: September 2026

## 1. Our Anonymity Commitment
Message Me Anonymously is built on a strict privacy-first foundation. We do not display, sell, or reveal sender identities to message recipients.

## 2. Information We Store
- **Profile Data**: Your chosen username, display name, public bio, avatar preference, and account preferences.
- **Messages**: Anonymous messages received on your link and any public replies you choose to share.
- **Safety Signals**: To prevent abuse and spam, our automated system calculates salted cryptographic device hashes (sender tokens) used strictly for rate limiting and sender blocking. Senders do not have their names, emails, or personal profiles attached to messages.

## 3. Data Deletion
You retain complete control of your data. You may delete any message from your inbox at any time, unblock or block senders, and permanently delete your account and all associated messages from the Settings tab.
        """.trimIndent()

        "Terms of Service" -> """
# Terms of Service

## 1. Acceptance of Terms
By accessing or using Message Me Anonymously (MMA), you agree to be bound by these Terms of Service.

## 2. Age Requirement
You must be at least 13 years of age (or the legal age of consent in your jurisdiction) to use MMA.

## 3. Acceptable Use
You agree not to use MMA to transmit any prohibited material, including but not limited to:
- Threats of violence or bodily harm
- Hate speech or unlawful discrimination
- Sexual harassment or non-consensual imagery
- Fraudulent spam, phishing schemes, or commercial advertising

## 4. Termination & Moderation
We reserve the right to suspend or remove accounts and block senders who violate community standards or endanger other users.
        """.trimIndent()

        "Community Guidelines" -> """
# Community Guidelines

“Say it. Stay anonymous.”

MMA is a place for candid compliments, friendly questions, and genuine expressions. To keep it fun and safe for everyone, we enforce a zero-tolerance policy against toxicity:

1. **Zero Tolerance for Harassment**: Anonymous messaging is not a shield for cruelty. Bullying, personal attacks, and intimidation are strictly prohibited.
2. **No Hate Speech**: We prohibit attacks against individuals or groups based on race, ethnicity, religion, sexual orientation, gender identity, disability, or nationality.
3. **No Threats or Self-Harm**: Any messages promoting violence or self-harm are immediately blocked and reported to safety monitors.
4. **No Spam or Malicious Links**: Links to external phishing or promotional sites are blocked.
        """.trimIndent()

        "Safety Center" -> """
# MMA Safety Center 🛡️

Your emotional well-being and safety come first.

### How to protect yourself on MMA:
- **Instant Sender Blocking**: Tap the three dots on any message and choose "Block Sender". They will never be able to send you a message again.
- **Report Violations**: Report any offensive messages so our moderation system can review them and take immediate protective action.
- **Pause Messages Anytime**: If you ever feel overwhelmed, simply toggle "Pause anonymous messages" in your Settings.
- **Filter Controls**: You can change your settings so only selected circles can interact with your profile.

### Need Help?
If you or someone you know is going through a tough time, free support is available 24/7:
- National Crisis Hotline: 988 (Call or Text)
- Crisis Text Line: Text HOME to 741741
        """.trimIndent()

        "About" -> """
# About Message Me Anonymously (MMA)

Message Me Anonymously is the next-generation social Q&A and anonymous messaging platform designed with modern mobile design, safety tools, and custom story card sharing.

### Core Pillars:
- **Youthful, Premium Interface**: Smooth rounded cards, soft gradients, and high contrast.
- **Story Studio**: Beautiful, customizable answer cards designed for Instagram Stories, Snapchat, and WhatsApp.
- **Proactive Safety**: Server-side anti-spam, duplicate prevention, and zero-tolerance automated content moderation.
        """.trimIndent()

        else -> """
# Contact Support

Have a question, feedback, or need assistance with your MMA account?

Email our team: support@messagemeanonymous.com
Safety reports: safety@messagemeanonymous.com

We typically respond within 24 hours.
        """.trimIndent()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.Shield, contentDescription = null, tint = MmaPurple)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(20.dp)
            ) {
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        lineHeight = 22.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
