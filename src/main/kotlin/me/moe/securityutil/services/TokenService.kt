package me.moe.securityutil.services

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.fuel.gson.responseObject
import me.jakejmattson.discordkt.Discord
import me.jakejmattson.discordkt.annotations.Service
import me.moe.securityutil.dataclasses.Configuration

@Service
class TokenService() {

    fun testBotToken(token: String): Boolean {
        val (_, response, result) = Fuel.get("https://discord.com/api/v9/users/@me")
            .header(Headers.USER_AGENT, "Application-Name (https://github.com/ItsAzaria/SecurityUtil)")
            .header(Headers.CONTENT_TYPE, "application/json")
            .header(Headers.AUTHORIZATION, "Bot $token")
            .response()

        result.fold(
            success = {
                if (response.statusCode == 200) {
                    return true
                }
            },

            failure = {
                return false
            }
        )

        return false
    }

}