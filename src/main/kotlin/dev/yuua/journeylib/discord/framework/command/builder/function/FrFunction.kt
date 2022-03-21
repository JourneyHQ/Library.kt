package dev.yuua.journeylib.discord.framework.command.builder.function

import dev.yuua.journeylib.discord.framework.command.builder.option.FrOptionIndex
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

fun interface FrFunction {
    fun execute(
        jda: JDA,
        guild: Guild?,
        isFromGuild: Boolean,
        channel: MessageChannel,
        channelType: ChannelType,
        member: Member?,
        user: User,
        options: MutableList<FrOptionIndex>,
        event: SlashCommandInteractionEvent
    )
}
