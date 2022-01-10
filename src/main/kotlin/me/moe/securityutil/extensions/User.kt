package me.moe.securityutil.extensions

import dev.kord.core.entity.User


fun User.pfp(): String {
    if (avatar != null)
        return avatar!!.url

    return "https://cdn.discordapp.com/embed/avatars/${(discriminator.toInt() % 5)}.png"
}