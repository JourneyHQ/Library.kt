package dev.yuua.journeylib.discord.framework.command.builder.function

import dev.yuua.journeylib.discord.framework.command.event.FrCmdEvent
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.*

fun interface FrTextFunction {
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
