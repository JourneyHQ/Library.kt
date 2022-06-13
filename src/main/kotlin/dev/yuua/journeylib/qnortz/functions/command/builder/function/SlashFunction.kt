package dev.yuua.journeylib.qnortz.functions.command.builder.function

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

fun interface SlashFunction {
    fun execute(event: SlashCommandInteractionEvent)
}
