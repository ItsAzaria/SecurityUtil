package me.moe.securityutil.dataclasses

import kotlinx.serialization.Serializable
import me.jakejmattson.discordkt.dsl.Data

@Serializable
data class Configuration(val webhooks: MutableList<Webhooks> = mutableListOf(),
                         var totalCommandsExecuted: Int = 0) : Data() {
                            //
                         }


@Serializable
data class Webhooks(val url: String, val time: Long, val userID: ULong)