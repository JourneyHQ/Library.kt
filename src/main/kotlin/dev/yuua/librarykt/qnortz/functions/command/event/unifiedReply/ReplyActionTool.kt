package dev.yuua.librarykt.qnortz.functions.command.event.unifiedReply

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction
import net.dv8tion.jda.api.requests.restaction.MessageEditAction
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction

fun ReplyCallbackAction.toUnifiedReplyActionDispatcher() =
    UnifiedReplyActionDispatcher(replyCallbackAction = this)

fun MessageCreateAction.toUnifiedReplyActionDispatcher() =
    UnifiedReplyActionDispatcher(messageCreateAction = this)

fun InteractionHook.toUnifiedEditAction() =
    UnifiedEditAction(interaction = this)

fun Message.toUnifiedEditAction() =
    UnifiedEditAction(message = this)

fun WebhookMessageEditAction<Message>.toUnifiedEditActionDispatcher() =
    UnifiedEditActionDispatcher(webhookMessageAction = this)

fun MessageEditAction.toUnifiedEditActionDispatcher() =
    UnifiedEditActionDispatcher(messageAction = this)
