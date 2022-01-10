package me.moe.securityutil.embeds

import dev.kord.common.kColor
import dev.kord.core.entity.Icon
import dev.kord.rest.builder.message.EmbedBuilder
import kotlinx.coroutines.DelicateCoroutinesApi
import me.jakejmattson.discordkt.Discord
import me.jakejmattson.discordkt.extensions.addField
import me.jakejmattson.discordkt.extensions.addInlineField
import me.jakejmattson.discordkt.extensions.toSnowflake
import me.moe.securityutil.extensions.descriptor
import me.moe.securityutil.services.WebhookService.*
import java.awt.Color

@DelicateCoroutinesApi
suspend fun EmbedBuilder.createWebhookInfoEmbed(webhookDelete: WebhookResponse?, webhookData: DiscordWebhook, discord: Discord, toDelete: Boolean = false) {

    color = Color.green.kColor

    if (toDelete)
        title = "Scheduled webhook deletion 2 hours from now."


    // Avatar data
    if (webhookData.avatar != null) {
        val webhookIcon = Icon.UserAvatar(webhookData.id.toSnowflake(), webhookData.avatar, discord.kord)

        thumbnail {
            url = webhookIcon.url + "?size=4096"
        }
    }

    if (webhookDelete != null) {
        addInlineField("HTTP Response", "```" +
                "[Response Code]\n${webhookDelete.code}\n\n" +
                "[Response Message]\n${webhookDelete.message}" +
                "```")
    }


    var webhookInfo = ""
    // Name data
    if (webhookData.name != null)
        webhookInfo += "[Webhook Name]\n${webhookData.name}\n\n"

    // Channel data
    if (webhookData.channel_id != null)
        webhookInfo += "[Webhook Channel]\n${webhookData.channel_id.toSnowflake().descriptor()}"

    if (webhookInfo.isNotEmpty())
        addInlineField("Webhook Data", "```" +
                webhookInfo +
                "```")

    if (webhookData.guild_id != null) {
        var guildData = ""

        // Guild data
        val guildId = webhookData.guild_id.toSnowflake()

        guildData += "[Guild]\n${guildId.descriptor()}\n\n"

        // Guild preview data
        val guildPreview = discord.kord.getGuildPreviewOrNull(guildId)

        if (guildPreview != null) {

            guildData += "[Guild Name]\n${guildPreview.name}\n\n"
            guildData += "[Member Count]\n${guildPreview.approximateMemberCount}\n\n"
            guildData += "[Presence Count]\n${guildPreview.approximatePresenceCount}\n\n"

            if (guildPreview.description != null)
                guildData += "[Description]\n${guildPreview.description}"
        }

        addField("Guild Data", "```" +
                guildData +
                "```")
    }
}