package dev.yuua.journeylib.discord.framework.function.command.builder.function

import dev.yuua.journeylib.discord.framework.function.command.event.FrCmdEvent
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.*

interface FrTextStruct {
    val jda: JDA
    val guild: Guild?
    val isFromGuild: Boolean
    val isFromThread: Boolean
    val channel: MessageChannel
    val channelType: ChannelType
    val member: Member?
    val user: User
    val event: FrCmdEvent
}
