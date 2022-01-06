package dev.yuua.journeylib.discord.framework.scope

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent

fun interface CmdScopeRule {
    fun check(event: SlashCommandEvent): Boolean
}