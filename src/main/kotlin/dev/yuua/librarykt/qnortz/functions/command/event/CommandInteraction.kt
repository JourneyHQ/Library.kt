package dev.yuua.librarykt.qnortz.functions.command.event

import dev.yuua.librarykt.qnortz.functions.command.CommandMethodType
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

/**
 * Data class which holds both [SlashCommandInteractionEvent] and [MessageReceivedEvent].
 *
 * @param slashCommandInteractionEvent [SlashCommandInteractionEvent].
 * @param messageReceivedEvent [MessageReceivedEvent].
 *
 * @throws IllegalArgumentException When both of them is null or nonnull.
 */
data class CommandInteraction(
    val slashCommandInteractionEvent: SlashCommandInteractionEvent?,
    val messageReceivedEvent: MessageReceivedEvent?
) {
    private val illegalArgs = IllegalArgumentException("One of them must be null and the other must be not null!")

    private val slashIsNull = slashCommandInteractionEvent == null
    private val textIsNull = messageReceivedEvent == null

    val type = when {
        !slashIsNull -> CommandMethodType.SlashCommand
        !textIsNull -> CommandMethodType.TextCommand
        else -> throw illegalArgs // never happen
    }

    init {
        if (!(slashIsNull xor textIsNull))
            throw illegalArgs
    }
}
