package dev.yuua.librarykt.qnortz.functions.command.builder.function

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

/**
 * Interface for SlashCommand function.
 */
fun interface SlashFunction {
    fun execute(event: SlashCommandInteractionEvent)
}
