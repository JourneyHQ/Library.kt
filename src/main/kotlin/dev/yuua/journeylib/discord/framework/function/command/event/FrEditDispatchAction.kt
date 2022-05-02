package dev.yuua.journeylib.discord.framework.function.command.event

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.requests.restaction.MessageAction
import net.dv8tion.jda.api.requests.restaction.WebhookMessageUpdateAction

class FrEditDispatchAction(
    val webhookMessageAction: WebhookMessageUpdateAction<Message>?,
    val messageAction: MessageAction?
) {
    var type: FrCmdType = if (webhookMessageAction != null && messageAction == null)
        FrCmdType.SLASH
    else if (webhookMessageAction == null && messageAction != null)
        FrCmdType.TEXT
    else throw IllegalArgumentException("One event must be null!")

    fun queue(success: ((Message) -> Unit), failure: ((Throwable) -> Unit)) {
        return when (type) {
            FrCmdType.SLASH ->
                webhookMessageAction!!.queue({ success(it) }, { failure(it) })
            FrCmdType.TEXT ->
                messageAction!!.queue({ success(it) }, { failure(it) })
        }
    }

    fun queue(success: ((Message) -> Unit)) {
        return when (type) {
            FrCmdType.SLASH ->
                webhookMessageAction!!.queue { success(it) }
            FrCmdType.TEXT ->
                messageAction!!.queue { success(it) }
        }
    }

    fun queue() {
        return when (type) {
            FrCmdType.SLASH ->
                webhookMessageAction!!.queue()
            FrCmdType.TEXT ->
                messageAction!!.queue()
        }
    }

    fun complete(): Message {
        return when (type) {
            FrCmdType.SLASH ->
                webhookMessageAction!!.complete()
            FrCmdType.TEXT ->
                messageAction!!.complete()
        }
    }
}
