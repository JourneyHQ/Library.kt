package dev.yuua.journeylib.discord.framework

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.requests.restaction.WebhookMessageAction

object InteractionExtension {
    fun SlashCommandInteractionEvent.followUp(message: String): WebhookMessageAction<Message> {
        return this.hook.sendMessage(message)
    }

    fun SlashCommandInteractionEvent.followUp(message: MessageEmbed, vararg messages: MessageEmbed): WebhookMessageAction<Message> {
        return this.hook.sendMessageEmbeds(message, *messages)
    }
}
