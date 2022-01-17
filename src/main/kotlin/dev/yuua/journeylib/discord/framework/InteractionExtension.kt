package dev.yuua.journeylib.discord.framework

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.requests.restaction.WebhookMessageAction

object InteractionExtension {
    fun SlashCommandEvent.followUp(message: String): WebhookMessageAction<Message> {
        return this.hook.sendMessage(message)
    }

    fun SlashCommandEvent.followUp(message: MessageEmbed, vararg messages: MessageEmbed): WebhookMessageAction<Message> {
        return this.hook.sendMessageEmbeds(message, *messages)
    }
}