package dev.yuua.journeylib.qnortz.functions.command.event.unifiedReply

import dev.yuua.journeylib.qnortz.functions.command.CommandFromType
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.requests.restaction.MessageAction
import net.dv8tion.jda.api.requests.restaction.WebhookMessageUpdateAction

data class UnifiedEditActionDispatcher(
    val webhookMessageAction: WebhookMessageUpdateAction<Message>? = null,
    val messageAction: MessageAction? = null
) {
    private val illegalArgs = IllegalArgumentException("One of them must be null and the other must be not null!")

    private val webhookMessageIsNull = webhookMessageAction == null
    private val messageIsNull = messageAction == null

    init {
        if ((!webhookMessageIsNull && !messageIsNull) || (webhookMessageIsNull && messageIsNull))
            throw illegalArgs
    }

    val type = when {
        !webhookMessageIsNull -> CommandFromType.SlashCommand
        !messageIsNull -> CommandFromType.TextCommand
        else -> throw illegalArgs // never happen
    }

    fun queue(success: ((Message) -> Unit), failure: ((Throwable) -> Unit)) = when (type) {
        CommandFromType.SlashCommand ->
            webhookMessageAction!!.queue({ success(it) }, { failure(it) })

        CommandFromType.TextCommand ->
            messageAction!!.queue({ success(it) }, { failure(it) })
    }

    fun queue(success: ((Message) -> Unit)) = when (type) {
        CommandFromType.SlashCommand ->
            webhookMessageAction!!.queue { success(it) }

        CommandFromType.TextCommand ->
            messageAction!!.queue { success(it) }
    }

    fun queue() = when (type) {
        CommandFromType.SlashCommand ->
            webhookMessageAction!!.queue()

        CommandFromType.TextCommand ->
            messageAction!!.queue()
    }

    fun complete(): Message = when (type) {
        CommandFromType.SlashCommand ->
            webhookMessageAction!!.complete()

        CommandFromType.TextCommand ->
            messageAction!!.complete()
    }
}
