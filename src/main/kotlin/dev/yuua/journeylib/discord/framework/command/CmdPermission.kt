package dev.yuua.journeylib.discord.framework.command

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent

data class CmdPermission(val check: (event: SlashCommandEvent) -> Boolean)