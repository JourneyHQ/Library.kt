package dev.yuua.journeylib.discord.framework.command.event

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class FrJDAEvent(
    val slash: SlashCommandInteractionEvent?,
    val text: MessageReceivedEvent?
) {
    lateinit var type: Type

    init {
        if (slash != null && text == null) {
            type = Type.SLASH
        } else if (slash == null && text != null) {
            type = Type.TEXT
        } else {
            throw IllegalArgumentException("片方のEventがnullでなければいけません！")
        }
    }

    enum class Type {
        SLASH, TEXT
    }
}
