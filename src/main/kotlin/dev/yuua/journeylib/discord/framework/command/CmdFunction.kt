package dev.yuua.journeylib.discord.framework.command

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent

fun interface CmdFunction {
    fun execute(
        jda: JDA, guild: Guild?, isFromGuild: Boolean,
        channel: MessageChannel, type: ChannelType,
        member: Member?, user: User,
        event: SlashCommandEvent
    )
}