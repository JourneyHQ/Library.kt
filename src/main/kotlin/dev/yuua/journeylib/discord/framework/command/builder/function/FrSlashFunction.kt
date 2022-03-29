package dev.yuua.journeylib.discord.framework.command.builder.function

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

fun interface FrSlashFunction {
    fun execute(
        jda: JDA,
        guild: Guild?,
        isFromGuild: Boolean,
        isFromThread: Boolean,
        channel: MessageChannel,
        channelType: ChannelType,
        member: Member?,
        user: User,
        event: SlashCommandInteractionEvent
    )
}
