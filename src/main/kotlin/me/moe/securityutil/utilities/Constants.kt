package me.moe.securityutil.utilities

object Constants {
    val BOT_OWNER = System.getenv("BOT_OWNER") ?: "345541952500006912"
    val DEFAULT_PREFIX = System.getenv("BOT_PREFIX") ?: "su!"
    val GITHUB_TOKEN = System.getenv("GITHUB_TOKEN")?: null

    val MFA_TOKEN_REGEX = "/((mfa\\.)?[a-z0-9_-]{20,})/gi".toRegex()
    val TOKEN_REGEX = "([a-z0-9_-]{23,28})\\.([a-z0-9_-]{6,7})\\.([a-z0-9_-]{27})".toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE))
    val WEBHOOK_REGEX = "https?:\\/\\/(?:ptb.|canary.|www.|)discord(?:app)?.com\\/api\\/webhooks\\/([0-9]{17,20})\\/([A-Za-z0-9\\.\\-\\_]{60,68})".toRegex(setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE))

    val GISTPREFIX = "Your token was found on discord via https://github.com/ItsAzaria/SecurityUtil. " +
            "I uploaded it to this gist so it would be picked up by " +
            "https://docs.github.com/en/code-security/secret-scanning/about-secret-scanning and safely deleted by discord"
}

enum class ListenerTypes {
    Webhook,
    Token
}