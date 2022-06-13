package dev.yuua.journeylib.qnortz.functions.command.event

import dev.yuua.journeylib.qnortz.functions.command.CommandFromType
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

data class CommandInteraction(
    val slashCommandInteractionEvent: SlashCommandInteractionEvent?,
    val messageReceivedEvent: MessageReceivedEvent?
) {
    private val illegalArgs = IllegalArgumentException("One of them must be null and the other must be not null!")

    private val slashIsNull = slashCommandInteractionEvent == null
    private val textIsNull = messageReceivedEvent == null

    init {
        if ((!slashIsNull && !textIsNull) || (slashIsNull && textIsNull))
            throw illegalArgs
    }

    val type = when {
        !slashIsNull -> CommandFromType.SlashCommand
        !textIsNull -> CommandFromType.TextCommand
        else -> throw illegalArgs // never happen
    }
}
