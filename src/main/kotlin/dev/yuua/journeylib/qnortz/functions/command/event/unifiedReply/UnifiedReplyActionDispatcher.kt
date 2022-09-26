package dev.yuua.journeylib.qnortz.functions.command.event.unifiedReply

import dev.yuua.journeylib.qnortz.functions.command.CommandFromType
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
        !replyCallbackIsNull -> CommandFromType.SlashCommand
        !messageIsNull -> CommandFromType.TextCommand
        else -> throw illegalArgs // never happen
    }

    fun queue(success: ((UnifiedEditAction) -> Unit), failure: ((Throwable) -> Unit)) = when (type) {
        CommandFromType.SlashCommand ->
            replyCallbackAction!!.queue({ success(it.toUnifiedEditAction()) }, { failure(it) })

        CommandFromType.TextCommand ->
            messageCreateAction!!.queue({ success(it.toUnifiedEditAction()) }, { failure(it) })
    }

    fun queue(success: ((UnifiedEditAction) -> Unit)) = when (type) {
        CommandFromType.SlashCommand ->
            replyCallbackAction!!.queue { success(it.toUnifiedEditAction()) }

        CommandFromType.TextCommand ->
            messageCreateAction!!.queue { success(it.toUnifiedEditAction()) }
    }

    fun queue() = when (type) {
        CommandFromType.SlashCommand ->
            replyCallbackAction!!.queue()

        CommandFromType.TextCommand ->
            messageCreateAction!!.queue()
    }

    fun complete() = when (type) {
        CommandFromType.SlashCommand ->
            replyCallbackAction!!.complete().toUnifiedEditAction()

        CommandFromType.TextCommand ->
            messageCreateAction!!.complete().toUnifiedEditAction()
    }
}
