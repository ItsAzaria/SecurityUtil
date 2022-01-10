package me.moe.securityutil.services

import me.jakejmattson.discordkt.Discord
import me.jakejmattson.discordkt.annotations.Service
import me.jakejmattson.discordkt.extensions.toTimeString
import me.moe.securityutil.dataclasses.Configuration
import java.util.*

@Service
class BotStatsService(private val discord: Discord) {
    private var startTime: Date = Date()

    val uptime: String
        get() = ((Date().time - startTime.time) / 1000).toTimeString()

    val ping: String
        get() = "${discord.kord.gateway.averagePing}"
}