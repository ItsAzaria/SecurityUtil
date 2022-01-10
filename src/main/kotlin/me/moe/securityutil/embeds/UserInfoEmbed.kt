package me.moe.securityutil.embeds

import dev.kord.common.kColor
import dev.kord.core.entity.User
import dev.kord.rest.builder.message.EmbedBuilder
import me.moe.securityutil.extensions.pfp
import me.moe.securityutil.extensions.timeDescriptor
import java.awt.Color

fun EmbedBuilder.createUserInfoEmbed(user: User) {
    title = "User information"
    color = Color.cyan.kColor

    thumbnail {
        url = user.pfp()
    }

    field {
        name = "**Username**"
        value = user.tag
        inline = true
    }

    field {
        name = "**Avatar**"
        value = "[[Link]](${user.pfp()}?size=4096)\n[[Search]](https://www.google.com/searchbyimage?&image_url=${user.pfp()})"
        inline = true
    }


    field {
        name = "**Data**"
        value = "```\n" +
                "[Username]\n" +
                "${user.tag}\n\n" +

                "[IsBot]\n" +
                "${user.isBot}\n\n" +

                "[Created At]\n" +
                "${user.id.timeDescriptor()}\n\n" +

                "[User ID]\n" +
                "${user.id}" +

                "```".trimMargin()

        inline = false
    }
}