package dev.yuua.librarykt.qnortz.functions.command.event.unifiedReply

import dev.yuua.librarykt.qnortz.functions.command.CommandMethodType
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.requests.restaction.MessageEditAction
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction

data class UnifiedEditActionDispatcher(
    val webhookMessageAction: WebhookMessageEditAction<Message>? = null,
    val messageAction: MessageEditAction? = null
) {
    private val illegalArgs = IllegalArgumentException("One of them must be null and the other must be not null!")

    private val webhookMessageIsNull = webhookMessageAction == null
    private val messageIsNull = messageAction == null

    init {
        if ((!webhookMessageIsNull && !messageIsNull) || (webhookMessageIsNull && messageIsNull))
            throw illegalArgs
    }

    val type = when {
        !webhookMessageIsNull -> CommandMethodType.SlashCommand
        !messageIsNull -> CommandMethodType.TextCommand
        else -> throw illegalArgs // never happen
    }

    fun queue(success: ((Message) -> Unit), failure: ((Throwable) -> Unit)) = when (type) {
        CommandMethodType.SlashCommand ->
            webhookMessageAction!!.queue({ success(it) }, { failure(it) })

        CommandMethodType.TextCommand ->
            messageAction!!.queue({ success(it) }, { failure(it) })
    }

    fun queue(success: ((Message) -> Unit)) = when (type) {
        CommandMethodType.SlashCommand ->
            webhookMessageAction!!.queue { success(it) }

        CommandMethodType.TextCommand ->
            messageAction!!.queue { success(it) }
    }

    fun queue() = when (type) {
        CommandMethodType.SlashCommand ->
            webhookMessageAction!!.queue()

        CommandMethodType.TextCommand ->
            messageAction!!.queue()
    }

    fun complete(): Message = when (type) {
        CommandMethodType.SlashCommand ->
            webhookMessageAction!!.complete()

        CommandMethodType.TextCommand ->
            messageAction!!.complete()
    }
}
