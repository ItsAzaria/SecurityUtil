package me.moe.securityutil.extensions

import dev.kord.common.entity.DiscordUser

fun DiscordUser.idDescriptor(): String = "$username#$discriminator :: ${id.value}"