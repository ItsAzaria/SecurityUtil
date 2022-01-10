package me.moe.securityutil.conversations

import com.github.kittinunf.fuel.core.FuelError
import dev.kord.common.kColor
import dev.kord.core.entity.Message
import dev.kord.x.emoji.Emojis
import kotlinx.coroutines.DelicateCoroutinesApi
import me.jakejmattson.discordkt.Discord
import me.jakejmattson.discordkt.conversations.conversation
import me.jakejmattson.discordkt.extensions.addField
import me.moe.securityutil.embeds.createWebhookInfoEmbed
import me.moe.securityutil.services.GistService
import me.moe.securityutil.services.WebhookService
import me.moe.securityutil.utilities.Constants
import me.moe.securityutil.utilities.ListenerTypes
import java.awt.Color

@DelicateCoroutinesApi
fun createDeleteConversation(message: Message, type: ListenerTypes, discord: Discord) = conversation {
    val (gistService, webhookService) = discord.getInjectionObjects(GistService::class, WebhookService::class)

    val shouldDelete = promptButton<Boolean> {
        embed {
            color = Color.red.kColor

            title = "Spotted message with a ${type.name.lowercase()}"
            description = "Careful! You just posted a ${type.name.lowercase()}... " +
                    "I tried to delete it but other users may have access to it. " +
                    "Would you like me to safely delete the ${type.name.lowercase()} for you?"

            field {
                value = """
                ${Emojis.whiteCheckMark.unicode} - Yes, please delete this.
                ${Emojis.x.unicode} - No, I'll delete it myself.
            """.trimIndent()
            }
        }

        buttons {
            button("Yes", Emojis.whiteCheckMark, true)
            button("No", Emojis.x, false)
        }
    }

    channel.getMessage(previousBotMessageId).delete()

    if (shouldDelete) {
        if (type == ListenerTypes.Token) {
            try {
                respond {
                    color = Color.green.kColor
                    title = "Success"
                    description = "Uploaded tokens to GitHub Gist for removal"
                }
                gistService.postGist(message.content)

            } catch (err: FuelError) {
                println(err.localizedMessage)

                respond {
                    color = Color.red.kColor
                    title = "Error!"
                    description = "Go poke moe about this error"
                }
            }
        }

        if (type == ListenerTypes.Webhook) {
            try {
                val webhookUrl = Constants.WEBHOOK_REGEX.find(message.content)!!.value
                val webhookData = webhookService.getWebhook(webhookUrl)
                val webhookDelete = webhookService.deleteWebhook(webhookUrl)

                if (!webhookDelete.success || webhookData == null) {
                    respond {
                        color = Color.orange.kColor

                        addField("Code", webhookDelete.code.toString())
                        addField("Response", webhookDelete.message)
                    }
                } else {
                    respond {
                        createWebhookInfoEmbed(webhookDelete, webhookData, discord)
                    }
                }



            } catch (err: FuelError) {
                println(err.localizedMessage)

                respond {
                    color = Color.red.kColor
                    title = "Error!"
                    description = "Go poke moe about this error"
                }
            }
        }
    }

}
