package dev.yuua.journeylib.qnortz.functions.command.event.unifiedReply

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.requests.restaction.MessageAction
import net.dv8tion.jda.api.requests.restaction.WebhookMessageUpdateAction
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction

fun ReplyCallbackAction.toUnifiedReplyActionDispatcher() =
    UnifiedReplyActionDispatcher(replyCallbackAction = this)

fun MessageAction.toUnifiedReplyActionDispatcher() =
    UnifiedReplyActionDispatcher(messageAction = this)

fun InteractionHook.toUnifiedEditAction() =
    UnifiedEditAction(interaction = this)

fun Message.toUnifiedEditAction() =
    UnifiedEditAction(message = this)

fun WebhookMessageUpdateAction<Message>.toUnifiedEditActionDispatcher() =
    UnifiedEditActionDispatcher(webhookMessageAction = this)

fun MessageAction.toUnifiedEditActionDispatcher() =
    UnifiedEditActionDispatcher(messageAction = this)
