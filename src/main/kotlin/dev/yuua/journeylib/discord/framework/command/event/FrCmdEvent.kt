package dev.yuua.journeylib.discord.framework.command.event

import dev.yuua.journeylib.discord.framework.command.builder.option.FrOptionIndex
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.*

class FrCmdEvent(
    val jda: JDA,
    val guild: Guild?,
    val isFromGuild: Boolean,
    val channel: MessageChannel,
    val channelType: ChannelType,
    val member: Member?,
    val user: User,
    val options: MutableList<FrOptionIndex>,
    val event: FrJDAEvent
)
