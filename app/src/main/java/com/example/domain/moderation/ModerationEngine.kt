package com.example.domain.moderation

data class ModerationResult(
    val isAllowed: Boolean,
    val status: String, // "APPROVED", "FLAGGED", "BLOCKED"
    val reason: String? = null,
    val detectedCategory: String? = null
)

object ModerationEngine {

    private val BANNED_PATTERNS = listOf(
        // Threats / Violence
        Regex("(?i)\\b(kill\\s+your?self|kys|die\\s+bitch|murder\\s+you|cut\\s+your|bomb\\s+threat|slit\\s+your|stalk\\s+you)\\b"),
        // Hate Speech / Extreme Slurs
        Regex("(?i)\\b(nigger|nigga|faggot|kike|chink|spic|tranny|retard)\\b"),
        // Severe Harassment & Threats
        Regex("(?i)\\b(i\\s+will\\s+find\\s+you|i\\s+know\\s+where\\s+you\\s+live|you\\s+should\\s+be\\s+dead)\\b")
    )

    private val SUSPICIOUS_PATTERNS = listOf(
        // Bullying / Mild Harassment
        Regex("(?i)\\b(ugly\\s+fat|nobody\\s+likes\\s+you|loser|kill\\s+yourself|worthless|drop\\s+dead|disgusting\\s+freak)\\b"),
        // Scam / Phishing / Unsafe links
        Regex("(?i)(https?://|www\\.|bit\\.ly|t\\.me|tinyurl|discord\\.gg|cash\\.app|crypto|telegram)"),
        // Sexual Harassment / Explicit terms
        Regex("(?i)\\b(send\\s+nudes|naked\\s+pics|sex\\s+chat|onlyfans|horny\\s+dm)\\b")
    )

    private val PROFANITY_USERNAMES = listOf(
        "admin", "root", "support", "moderator", "official", "help",
        "fuck", "shit", "bitch", "asshole", "nigger", "faggot", "whore", "slut"
    )

    fun validateUsername(username: String): Pair<Boolean, String?> {
        val trimmed = username.trim().lowercase()
        if (trimmed.length < 3) return Pair(false, "Username must be at least 3 characters")
        if (trimmed.length > 20) return Pair(false, "Username cannot exceed 20 characters")
        if (!trimmed.matches(Regex("^[a-zA-Z0-9_]+$"))) {
            return Pair(false, "Only letters, numbers, and underscores are allowed")
        }
        if (PROFANITY_USERNAMES.any { trimmed.contains(it) }) {
            return Pair(false, "Username is reserved or contains restricted words")
        }
        return Pair(true, null)
    }

    fun evaluateContent(content: String, sensitivity: String = "HIGH"): ModerationResult {
        val trimmed = content.trim()
        if (trimmed.isEmpty()) {
            return ModerationResult(isAllowed = false, status = "BLOCKED", reason = "Message cannot be empty")
        }
        if (trimmed.length > 500) {
            return ModerationResult(isAllowed = false, status = "BLOCKED", reason = "Message exceeds 500 character limit")
        }

        // 1. Check for immediate zero-tolerance threats & hate
        for (pattern in BANNED_PATTERNS) {
            if (pattern.containsMatchIn(trimmed)) {
                return ModerationResult(
                    isAllowed = false,
                    status = "BLOCKED",
                    reason = "This message violates our safety guidelines (threat or prohibited hate speech)",
                    detectedCategory = "Threat / Prohibited Content"
                )
            }
        }

        // 2. Check for suspicious patterns (links, harassment, explicit content)
        for (pattern in SUSPICIOUS_PATTERNS) {
            if (pattern.containsMatchIn(trimmed)) {
                if (sensitivity == "HIGH") {
                    return ModerationResult(
                        isAllowed = false,
                        status = "BLOCKED",
                        reason = "External links, scam promotions, and harassment are prohibited",
                        detectedCategory = "Suspicious / Harassment"
                    )
                } else {
                    // Under lower sensitivity, allow but flag for review
                    return ModerationResult(
                        isAllowed = true,
                        status = "FLAGGED",
                        reason = "Message flagged for moderation review",
                        detectedCategory = "Under Review"
                    )
                }
            }
        }

        return ModerationResult(isAllowed = true, status = "APPROVED")
    }
}
