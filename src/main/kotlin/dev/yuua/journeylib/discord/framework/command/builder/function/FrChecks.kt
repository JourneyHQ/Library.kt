package dev.yuua.journeylib.discord.framework.command.builder.function

import dev.yuua.journeylib.discord.framework.command.event.FrCmdEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

fun interface FrChecks {
    fun check(event: FrCmdEvent): CheckResult
}
