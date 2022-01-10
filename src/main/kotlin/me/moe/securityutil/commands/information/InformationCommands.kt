package me.moe.securityutil.commands.information

import me.jakejmattson.discordkt.arguments.UserArg
import me.jakejmattson.discordkt.commands.commands
import me.moe.securityutil.embeds.createUserInfoEmbed

fun informationCommands() = commands("Information") {

    slash("UserInfo") {
        description = "Displays information about the given user."
        execute(UserArg("user")) {
            val (user) = args

            respond(false) {
                createUserInfoEmbed(user)
            }
        }
    }


}