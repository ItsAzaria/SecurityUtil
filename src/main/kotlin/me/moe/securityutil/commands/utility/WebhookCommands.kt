package me.moe.securityutil.commands.utility

import com.github.kittinunf.fuel.httpGet
import dev.kord.common.kColor
import dev.kord.core.behavior.edit
import dev.kord.core.behavior.interaction.InteractionResponseBehavior
import dev.kord.core.behavior.interaction.edit
import dev.kord.core.behavior.interaction.followUp
import dev.kord.rest.builder.message.modify.embed
import kotlinx.coroutines.DelicateCoroutinesApi
import me.jakejmattson.discordkt.arguments.*
import me.jakejmattson.discordkt.commands.commands
import me.jakejmattson.discordkt.extensions.addField
import me.jakejmattson.discordkt.extensions.addInlineField
import me.moe.securityutil.dataclasses.Configuration
import me.moe.securityutil.dataclasses.Webhooks
import me.moe.securityutil.embeds.createWebhookInfoEmbed
import me.moe.securityutil.services.GistService
import me.moe.securityutil.services.WebhookService
import me.moe.securityutil.utilities.Constants
import com.github.kittinunf.result.Result
import dev.kord.core.entity.Message
import java.awt.Color
import java.sql.Time
import java.util.*

@DelicateCoroutinesApi
fun webhookCommands(webhookService: WebhookService, configuration: Configuration) = commands("Webhook") {

    slash("WebhookInfo") {
        description = "Gives information from a webhook"
        execute(UrlArg) {
            val url = args.first

            if (!Constants.WEBHOOK_REGEX.matches(url)) {
                respond {
                    color = Color.RED.kColor
                    description = "Webhook doesn't match regex"
                }

                return@execute
            }

            val webhookData = webhookService.getWebhook(url)

            if (webhookData == null) {
                respond {
                    color = Color.ORANGE.kColor
                    description = "Webhook deleted."
                    addInlineField("Code", "404")
                }
            } else {
                respond {
                    createWebhookInfoEmbed(null, webhookData, discord)
                }
            }
        }
    }

    slash("WebhookDelete") {
        description = "Deletes a webhook"
        execute(UrlArg) {
            val url = args.first

            if (!Constants.WEBHOOK_REGEX.matches(url)) {
                respond {
                    color = Color.RED.kColor
                    description = "Webhook doesn't match regex"
                }

                return@execute
            }

            val webhookData = webhookService.getWebhook(url)
            val webhookDelete = webhookService.deleteWebhook(url)

            if (!webhookDelete.success || webhookData == null) {
                respond {
                    color = Color.orange.kColor

                    addField("Code", webhookDelete.code.toString())
                    addField("Response", webhookDelete.message)
                }
            } else {
                respond(false) {
                    createWebhookInfoEmbed(webhookDelete, webhookData, discord)
                }
            }
        }
    }

    command("BulkWebhookDelete") {
        description = "Deletes lots of webhooks"
        execute(EveryArg.optional("")) {
            val msg = respond("Deleting webhooks...")

            val webhooks: String

            if (args.first.isNotEmpty()) {
                webhooks = args.first
            } else {
                val attachment = message.attachments.firstOrNull()
                if (attachment == null) {
                    respond("No file or webhooks specified.")
                    return@execute
                }

                if (!attachment.filename.contains(".txt")) {
                    respond("Only text (`.txt`) files are accepted.")
                    return@execute
                }

                webhooks = getFile(attachment.url) ?: return@execute
            }

            if (webhooks.isEmpty() || webhooks.isBlank()) {
                respond("File or attachment cannot be blank.")
                return@execute
            }


            val matches = Constants.WEBHOOK_REGEX.findAll(webhooks)

            var alreadyGone = 0
            var deleted = 0

            matches.forEach {
                try {
                    val response = webhookService.deleteWebhook(it.value)
                    if (response.success) deleted++ else alreadyGone++
                } catch (exception: Exception) {
                    alreadyGone++
                }

            }

            updateEmbed(msg.first(), deleted, alreadyGone)
        }
    }



    slash("WebhookDeleteTimer") {
        description = "Deletes a webhook in the next few hours"
        execute(UrlArg) {
            val url = args.first

            if (!Constants.WEBHOOK_REGEX.matches(url)) {
                respond {
                    color = Color.RED.kColor
                    description = "Webhook doesn't match regex"
                }

                return@execute
            }

            val webhook = webhookService.getWebhook(url)

            if (webhook == null) {
                respond(false) {
                    color = Color.ORANGE.kColor
                    description = "Webhook deleted."
                    addInlineField("Code", "404")
                }

                return@execute
            }

            val twoHoursFromNow = Date().time + 2 * 60 * 60 * 1000

            configuration.webhooks.add(Webhooks(url, twoHoursFromNow, author.id.value))
            configuration.save()

            respond(false) {
                createWebhookInfoEmbed(null, webhook, discord, true)
            }
        }
    }
}

private suspend fun updateEmbed(msg: Message, deleted: Int, alreadyGone: Int) {
    msg.edit {
        content = null
        embed {
            title = "Bulk Webhook Nuker"
            field {
                name = "Deleted"
                value = deleted.toString()
                inline = true
            }

            field {
                name = "Already Deleted"
                value = alreadyGone.toString()
                inline = true
            }
        }
    }
}

private fun getFile(url: String): String? {
    val (_, _, result) = url
        .httpGet()
        .responseString()

    return when (result) {
        is Result.Success -> {
            return result.get()
        }
        is Result.Failure -> {
            null
        }
    }
}