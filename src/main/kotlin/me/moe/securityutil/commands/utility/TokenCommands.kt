package me.moe.securityutil.commands.utility

import dev.kord.common.kColor
import kotlinx.coroutines.DelicateCoroutinesApi
import me.jakejmattson.discordkt.arguments.AnyArg
import me.jakejmattson.discordkt.arguments.EveryArg
import me.jakejmattson.discordkt.commands.commands
import me.jakejmattson.discordkt.extensions.toSnowflake
import me.moe.securityutil.embeds.createUserInfoEmbed
import me.moe.securityutil.services.GistService
import me.moe.securityutil.services.TokenService
import me.moe.securityutil.utilities.Constants
import java.awt.Color
import java.util.*

@DelicateCoroutinesApi
fun tokenCommands(gistService: GistService, tokenService: TokenService) = commands("Tokens") {

    slash("TokenNuke") {
        description = "Nukes a discord bot or user token."
        execute(EveryArg) {
            val uploadToken = args.first

            if (Constants.MFA_TOKEN_REGEX.matches(uploadToken) || Constants.TOKEN_REGEX.matches(uploadToken)) {
                respond(false) {
                    color = Color.green.kColor
                    title = "Success"
                    description = "Uploaded tokens to GitHub Gist for removal"
                }

                gistService.postGist(uploadToken)

                return@execute
            }

            respond(false) {
                color = Color.red.kColor
                title = "Error"
                description = "Specified text does not include any tokens"
            }
        }
    }

    slash("TokenInfo") {
        description = "Returns information retrieved from a token"
        execute(AnyArg) {
            val token = args.first
            val idSegment = token.split(".").first()
            val decodedID = Base64.getDecoder().   decode(idSegment)
            val userID = String(decodedID).toSnowflake()
            val user = discord.kord.getUser(userID)

            if (user == null) {
                respond(false) {
                    color = Color.red.kColor
                    title = "User not found"
                    description = "User with ID $userID was not found"
                }

                return@execute
            }

            var tokenValid = false
            if (user.isBot)
                tokenValid = tokenService.testBotToken(args.first)

            respond(false) {
                createUserInfoEmbed(user, tokenValid)
            }
        }
    }
}
