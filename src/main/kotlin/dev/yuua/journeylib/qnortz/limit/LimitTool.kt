package dev.yuua.journeylib.qnortz.limit

import dev.yuua.journeylib.qnortz.functions.FunctionType
import dev.yuua.journeylib.qnortz.rules.RulesResultType
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel

inline fun <reified T> limits(basePackage: String, vararg limits: Pair<String, Limit<T>>) =
    LimitRouter(basePackage, listOf(*limits))

inline fun <reified T> Limit<T>.check(
    event: T,
    guild: Guild?,
    channel: MessageChannel,
    user: User,
    hideReason: Boolean
): Pair<Boolean, String?> {
    if (this.isNotEmpty()) {
        val ruleResult = rule.execute(event)

        val guildRestricted = guildIds.isNotEmpty() && !guildIds.contains(guild?.id)
        val channelsRestricted = channelIds.isNotEmpty() && !channelIds.contains(channel.id)
        val userRestricted = userIds.isNotEmpty() && !userIds.contains(user.id)
        val ruleRestricted = ruleResult.type != RulesResultType.Passed

        val restricted = guildRestricted || channelsRestricted || userRestricted || ruleRestricted

        val name = FunctionType.values().first { it.event == T::class }.name.lowercase()

        // todo improve message
        val message =
            if (hideReason) null
            else if (guildRestricted) "This $name not available on this guild."
            else if (channelsRestricted) "This $name not available on this channel."
            else if (userRestricted) "You are not allowed to use this $name."
            else if (ruleRestricted) ruleResult.message ?: "Restricted by custom rule."
            else null // never happen

        return restricted to message
    } else return true to null
}
