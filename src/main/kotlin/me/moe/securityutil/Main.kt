package me.moe.securityutil

import com.github.kittinunf.fuel.Fuel
import dev.kord.common.annotation.KordPreview
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.gateway.Intents
import dev.kord.gateway.PrivilegedIntent
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import me.jakejmattson.discordkt.dsl.bot
import me.jakejmattson.discordkt.extensions.descriptor
import me.jakejmattson.discordkt.extensions.idDescriptor
import me.jakejmattson.discordkt.extensions.pfpUrl
import me.moe.securityutil.dataclasses.Configuration
import me.moe.securityutil.dataclasses.Permissions
import me.moe.securityutil.services.BotStatsService
import me.moe.securityutil.services.GistService
import me.moe.securityutil.services.WebhookService
import me.moe.securityutil.utilities.Constants
import java.awt.Color

@DelicateCoroutinesApi
@PrivilegedIntent
@KordPreview
suspend fun main(args: Array<String>) {

    val token = System.getenv("BOT_TOKEN") ?: null
    require(token != null) { "Expected the bot token as an environment variable" }


    bot(token) {

        val configuration = data("data/config.json") { Configuration() }

        prefix {
            Constants.DEFAULT_PREFIX
        }

        configure {
            allowMentionPrefix = true
            generateCommandDocs = false
            commandReaction = null
            theme = Color.CYAN
            entitySupplyStrategy = EntitySupplyStrategy.cacheWithRestFallback
            intents = Intents.all
            permissions(Permissions.EVERYONE)
        }

        mentionEmbed {
            val self = it.discord.kord.getSelf()
            val statsService = it.discord.getInjectionObjects(BotStatsService::class)

            val memoryUsage = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()

            color = it.discord.configuration.theme
            title = "SecurityUtil"
            description = "*A simple bot to provide various useful security utilities*"

            thumbnail {
                url = self.pfpUrl
            }

            field {
                name = "Prefix"
                value = it.prefix()
                inline = true
            }

            field {
                name = "Ping"
                value = statsService.ping
                inline = true
            }

            field {
                val versions = it.discord.versions

                name = "Bot Info"
                value = "```" +
                        "Version: 1.0.0\n" +
                        "DiscordKt: ${versions.library}\n" +
                        "Kord: ${versions.kord}\n" +
                        "Kotlin: ${versions.kotlin}" +
                        "```"
            }

            field {
                name = "Uptime"
                value = statsService.uptime
                inline = true
            }

            field {
                name = "Source"
                value = "[[GitHub]](https://github.com/ItsAzaria/SecurityUtil)"
                inline = true
            }
        }

        onStart {
            println("Started ${this.kord.getSelf().idDescriptor()}")

            val (gistService, webhookService) = this.getInjectionObjects(GistService::class, WebhookService::class)
            gistService.removeGists()
            webhookService.removeScheduledWebhooks()
        }
    }
}