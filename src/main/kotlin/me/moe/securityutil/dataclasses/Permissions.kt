package me.moe.securityutil.dataclasses

import me.jakejmattson.discordkt.dsl.PermissionContext
import me.jakejmattson.discordkt.dsl.PermissionSet
import me.moe.securityutil.utilities.Constants

enum class Permissions : PermissionSet {
    BOT_OWNER {
        override suspend fun hasPermission(context: PermissionContext): Boolean {
            return context.user.id.toString() == Constants.BOT_OWNER
        }
    },

    EVERYONE {
        override suspend fun hasPermission(context: PermissionContext) = true
    }
}