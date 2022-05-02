package dev.yuua.journeylib.discord.framework.function.command.builder.function

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

interface FrSlashStruct {
    val jda: JDA
    val guild: Guild?
    val isFromGuild: Boolean
    val isFromThread: Boolean
    val channel: MessageChannel
    val channelType: ChannelType
    val member: Member?
    val user: User
    val event: SlashCommandInteractionEvent
}
