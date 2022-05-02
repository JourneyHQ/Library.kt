package dev.yuua.journeylib.discord.framework.function.command.builder.function

import dev.yuua.journeylib.discord.framework.function.command.event.FrCmdEvent
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.*

/**
 * Function of command that use [FrCmdEvent].
 *
 * Compatible with TextCommand.
 */
fun interface FrTextFunction {
    //todo context
    fun execute(
        jda: JDA,
        guild: Guild?,
        isFromGuild: Boolean,
        isFromThread: Boolean,
        channel: MessageChannel,
        channelType: ChannelType,
        member: Member?,
        user: User,
        event: FrCmdEvent
    )
}
