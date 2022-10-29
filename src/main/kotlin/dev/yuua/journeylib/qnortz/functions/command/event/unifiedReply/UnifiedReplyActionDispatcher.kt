package dev.yuua.journeylib.qnortz.functions.command.event.unifiedReply

import dev.yuua.journeylib.qnortz.functions.command.CommandMethodType
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction

data class UnifiedReplyActionDispatcher(
    val replyCallbackAction: ReplyCallbackAction? = null,
    val messageCreateAction: MessageCreateAction? = null
) {
    private val illegalArgs = IllegalArgumentException("One of them must be null and the other must be not null!")

    private val replyCallbackIsNull = replyCallbackAction == null
    private val messageIsNull = messageCreateAction == null

    init {
        if ((!replyCallbackIsNull && !messageIsNull) || (replyCallbackIsNull && messageIsNull))
            throw illegalArgs
    }

    val type = when {
        !replyCallbackIsNull -> CommandMethodType.SlashCommand
        !messageIsNull -> CommandMethodType.TextCommand
        else -> throw illegalArgs // never happen
    }

    fun queue(success: ((UnifiedEditAction) -> Unit), failure: ((Throwable) -> Unit)) = when (type) {
        CommandMethodType.SlashCommand ->
            replyCallbackAction!!.queue({ success(it.toUnifiedEditAction()) }, { failure(it) })

        CommandMethodType.TextCommand ->
            messageCreateAction!!.queue({ success(it.toUnifiedEditAction()) }, { failure(it) })
    }

    fun queue(success: ((UnifiedEditAction) -> Unit)) = when (type) {
        CommandMethodType.SlashCommand ->
            replyCallbackAction!!.queue { success(it.toUnifiedEditAction()) }

        CommandMethodType.TextCommand ->
            messageCreateAction!!.queue { success(it.toUnifiedEditAction()) }
    }

    fun queue() = when (type) {
        CommandMethodType.SlashCommand ->
            replyCallbackAction!!.queue()

        CommandMethodType.TextCommand ->
            messageCreateAction!!.queue()
    }

    fun complete() = when (type) {
        CommandMethodType.SlashCommand ->
            replyCallbackAction!!.complete().toUnifiedEditAction()

        CommandMethodType.TextCommand ->
            messageCreateAction!!.complete().toUnifiedEditAction()
    }
}
