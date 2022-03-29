package dev.yuua.journeylib.discord.framework.command.event

import dev.yuua.journeylib.discord.framework.command.builder.option.FrOptionIndex
import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.requests.restaction.MessageAction
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction

object FrEventLib {
    fun SlashCommandInteractionEvent.isFromThread(): Boolean {
        return listOf(
            ChannelType.GUILD_NEWS_THREAD,
            ChannelType.GUILD_PRIVATE_THREAD,
            ChannelType.GUILD_PUBLIC_THREAD
        ).contains(this.channelType)
    }

    fun SlashCommandInteractionEvent.toFrCmdEvent(): FrCmdEvent {
        val options = mutableListOf<FrOptionIndex>()
        for (jdaOption in this.options)
            options.add(FrOptionIndex().fromOptionMapping(jdaOption))

        return FrCmdEvent(
            this.jda, this.guild, this.isFromGuild,
            this.isFromThread(), this.channel, this.channelType,
            this.member, this.user, options, FrJDAEvent(this, null)
        )
    }

    fun MessageReceivedEvent.toFrCmdEvent(options: MutableList<FrOptionIndex>): FrCmdEvent {
        return FrCmdEvent(
            this.jda, this.guild, this.isFromGuild,
            this.isFromThread, this.channel, this.channelType,
            this.member, this.author, options, FrJDAEvent(null, this)
        )
    }

    fun ReplyCallbackAction.toFrReplyAction(): FrReplyAction {
        return FrReplyAction(this, null)
    }

    fun MessageAction.toFrReplyAction(): FrReplyAction {
        return FrReplyAction(null, this)
    }
}
