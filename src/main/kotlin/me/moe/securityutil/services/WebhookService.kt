package me.moe.securityutil.services

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.fuel.gson.responseObject
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.createEmbed
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import me.jakejmattson.discordkt.Discord
import me.jakejmattson.discordkt.annotations.Service
import me.jakejmattson.discordkt.extensions.addInlineField
import me.moe.securityutil.dataclasses.Configuration
import java.nio.charset.Charset
import java.util.*

@DelicateCoroutinesApi
@Service
class WebhookService(private val configuration: Configuration, private val discord: Discord) {

    @Serializable
    data class DiscordWebhook(val id: String,
                              val name: String?,
                              val avatar: String?,
                              val channel_id: String?,
                              val guild_id: String?,
                              val success: Boolean = false)

    fun getWebhook(url: String): DiscordWebhook? {
        val (_, response, result) = Fuel.get(url)
            .header(Headers.USER_AGENT, "Application-Name (https://github.com/ItsAzaria/SecurityUtil)")
            .header(Headers.CONTENT_TYPE, "application/json")
            .responseObject<DiscordWebhook>()

        result.fold(
            success = {
                return it
            },

            failure = {
                if (response.statusCode == 404) {
                    return null
                }

                throw(it.exception)
            }
        )
    }

    @Serializable
    data class WebhookResponse(val message: String, val code: Int, val success: Boolean = false)

    fun deleteWebhook(url: String): WebhookResponse {
        val (_, response, result) = Fuel.delete(url)
            .header(Headers.USER_AGENT, "Application-Name (https://github.com/ItsAzaria/SecurityUtil)")
            .header(Headers.CONTENT_TYPE, "application/json")
            .responseObject<WebhookResponse>()

        if (response.statusCode == 204)
            return WebhookResponse("Webhook Deleted", 204, true)

        result.fold(
            success = {
                return it
            },
            failure = {

                if(response.statusCode == 404) {
                    return WebhookResponse(response.data.toString(Charset.defaultCharset()), response.statusCode, false)
                }

                throw(it.exception)
            }
        )
    }


    suspend fun removeScheduledWebhooks() {
        val timeNow = Date().time

        configuration.webhooks.forEach {
            if (it.time < timeNow) {
                try {
                    val deletedWebhook = deleteWebhook(it.url)

                    val user = discord.kord.getUser(Snowflake(it.userID)) ?: return@forEach
                    val dmChannel = user.getDmChannelOrNull() ?: return@forEach

                    dmChannel.createEmbed {
                        title = "Deleted the embed you requested"

                        addInlineField("HTTP Response", "```" +
                                "[Response Code]\n${deletedWebhook.code}\n\n" +
                                "[Response Message]\n${deletedWebhook.message}" +
                                "```")
                    }

                } catch (exception: Exception) {
                    // nothing
                }

                configuration.webhooks.remove(it)
                configuration.save()
            }
        }

        GlobalScope.launch {
            delay(300000) // 5 minutes
            removeScheduledWebhooks()
        }

    }
}