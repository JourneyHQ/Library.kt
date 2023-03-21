package dev.yuua.journeylib.qnortz.functions.command.builder.option

import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.OptionType.*

val userRegex = Regex("<@!?(\\d+)>")
val channelRegex = Regex("<#(\\d+)>")
val roleRegex = Regex("<@&(\\d+)>")
val mentionableRegex = Regex("<@[&!]?(\\d+)>")

/**
 * Returns whether [value] is able to cast to [optionType].
 * @param optionType expected [OptionType]
 * @param value option value to check.
 */
fun matchType(optionType: OptionType, value: Any?): Boolean {
    val string = value.toString()
    return when (optionType) {
        STRING -> value != null
        INTEGER -> string.toIntOrNull() != null
        BOOLEAN -> string.toBooleanStrictOrNull() != null
        USER -> userRegex.matches(string)
        CHANNEL -> channelRegex.matches(string)
        ROLE -> roleRegex.matches(string)
        MENTIONABLE -> mentionableRegex.matches(string)
        NUMBER -> string.toLongOrNull() != null || string.toDoubleOrNull() != null

        else -> false
    }
}


