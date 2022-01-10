package me.moe.securityutil.extensions

import dev.kord.common.entity.Snowflake
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun Snowflake.descriptor(): String = "$value\n${timeDescriptor()}"

fun Snowflake.timeDescriptor(): String {
    val time = timestamp.toLocalDateTime(TimeZone.UTC)

    return "${time.year}-${time.monthNumber}-${time.dayOfMonth} ${time.hour}:${time.minute}:${time.second}"
}