package dev.yuua.journeylib.discord.framework.command.scope

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

data class FrCmdScope (
    val guilds: MutableList<String>,
    val users: MutableList<String>,
    val rule: (event: SlashCommandInteractionEvent) -> Boolean
)
