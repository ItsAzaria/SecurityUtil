package me.moe.securityutil.commands.utility

import dev.kord.common.kColor
import kotlinx.coroutines.DelicateCoroutinesApi
import me.jakejmattson.discordkt.arguments.UrlArg
import me.jakejmattson.discordkt.commands.commands
import me.jakejmattson.discordkt.extensions.addField
import me.jakejmattson.discordkt.extensions.addInlineField
import me.moe.securityutil.dataclasses.Configuration
import me.moe.securityutil.dataclasses.Webhooks
import me.moe.securityutil.embeds.createWebhookInfoEmbed
import me.moe.securityutil.services.GistService
import me.moe.securityutil.services.WebhookService
import me.moe.securityutil.utilities.Constants
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
