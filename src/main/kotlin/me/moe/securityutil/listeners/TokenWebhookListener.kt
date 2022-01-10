package me.moe.securityutil.listeners

import dev.kord.core.entity.Message
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.event.message.MessageUpdateEvent
import dev.kord.rest.request.KtorRequestException
import kotlinx.coroutines.DelicateCoroutinesApi
import me.jakejmattson.discordkt.Discord
import me.jakejmattson.discordkt.dsl.listeners
import me.moe.securityutil.conversations.createDeleteConversation
import me.moe.securityutil.utilities.Constants
import me.moe.securityutil.utilities.ListenerTypes

@DelicateCoroutinesApi
fun alertListener() = listeners {
    on<MessageCreateEvent> {
        guildId ?: return@on
        val message = message
        if (message.content.startsWith(Constants.DEFAULT_PREFIX)) return@on
        if (message.author?.isBot == true) return@on


        handlePotentialWebhook(message, discord)
        handlePotentialToken(message, discord)
    }

    on<MessageUpdateEvent> {
        val message = getMessageOrNull() ?: return@on
        if (message.content.startsWith(Constants.DEFAULT_PREFIX)) return@on
        if (message.author?.isBot == true) return@on
        message.getGuildOrNull() ?: return@on

        handlePotentialWebhook(message, discord)
        handlePotentialToken(message,discord)
    }
}

@DelicateCoroutinesApi
private suspend fun handlePotentialWebhook(message: Message, discord: Discord) {
    val match = Constants.WEBHOOK_REGEX.find(message.content) ?: return

    try {
        message.delete("Contained a webhook")
    } catch (_: KtorRequestException) { }

    createDeleteConversation(message, ListenerTypes.Webhook, discord).startPublicly(discord, message.author!!, message.channel.asChannel())

}

@DelicateCoroutinesApi
private suspend fun handlePotentialToken(message: Message, discord: Discord) {
    val match = Constants.TOKEN_REGEX.matches(message.content) || Constants.MFA_TOKEN_REGEX.matches(message.content)

    if (!match)
        return

    try {
        message.delete("Contained a token")
    } catch (_: KtorRequestException) { }

    createDeleteConversation(message, ListenerTypes.Token, discord).startPublicly(discord, message.author!!, message.channel.asChannel())
}