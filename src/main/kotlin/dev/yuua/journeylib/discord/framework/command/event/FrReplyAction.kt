package dev.yuua.journeylib.discord.framework.command.event

import net.dv8tion.jda.api.requests.restaction.MessageAction
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction

class FrReplyAction(
    val replyCallbackAction: ReplyCallbackAction?,
    val messageAction: MessageAction?
) {
    var type: FrCmdType = if (replyCallbackAction != null && messageAction == null)
        FrCmdType.SLASH
    else if (replyCallbackAction == null && messageAction != null)
        FrCmdType.TEXT
    else throw IllegalArgumentException("One event must be null！")

    fun queue(success: ((FrEditAction) -> Unit), failure: ((Throwable) -> Unit)) {
        return when (type) {
            FrCmdType.SLASH ->
                replyCallbackAction!!.queue({ success(FrEditAction(it, null)) }, { failure(it) })
            FrCmdType.TEXT ->
                messageAction!!.queue({ success(FrEditAction(null, it)) }, { failure(it) })
        }
    }

    fun queue(success: ((FrEditAction) -> Unit)) {
        return when (type) {
            FrCmdType.SLASH ->
                replyCallbackAction!!.queue { success(FrEditAction(it, null)) }
            FrCmdType.TEXT ->
                messageAction!!.queue { success(FrEditAction(null, it)) }
        }
    }

    fun queue() {
        return when (type) {
            FrCmdType.SLASH ->
                replyCallbackAction!!.queue()
            FrCmdType.TEXT ->
                messageAction!!.queue()
        }
    }

    fun complete(): FrEditAction {
        return when (type) {
            FrCmdType.SLASH ->
                FrEditAction(replyCallbackAction!!.complete(), null)
            FrCmdType.TEXT ->
                FrEditAction(null, messageAction!!.complete())
        }
    }
}
