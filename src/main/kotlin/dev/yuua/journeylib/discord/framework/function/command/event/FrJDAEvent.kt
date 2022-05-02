package dev.yuua.journeylib.discord.framework.function.command.event

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class FrJDAEvent(
    val slash: SlashCommandInteractionEvent?,
    val text: MessageReceivedEvent?
) {
    var type: FrCmdType = if (slash != null && text == null)
        FrCmdType.SLASH
    else if (slash == null && text != null)
        FrCmdType.TEXT
    else throw IllegalArgumentException("One event must be null!")
}
