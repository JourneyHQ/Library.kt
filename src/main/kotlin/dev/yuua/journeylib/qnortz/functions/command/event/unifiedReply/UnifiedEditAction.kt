package dev.yuua.journeylib.qnortz.functions.command.event.unifiedReply

import dev.minn.jda.ktx.messages.edit
import dev.minn.jda.ktx.messages.editMessage
import dev.yuua.journeylib.qnortz.functions.command.CommandFromType
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.components.LayoutComponent
import net.dv8tion.jda.api.utils.FileUpload

data class UnifiedEditAction(
    val interaction: InteractionHook? = null,
    val message: Message? = null
) {
    private val illegalArgs = IllegalArgumentException("One of them must be null and the other must be not null!")

    private val interactionIsNull = interaction == null
    private val messageIsNull = message == null

    init {
        if ((!interactionIsNull && !messageIsNull) || (interactionIsNull && messageIsNull))
            throw illegalArgs
    }

    val type = when {
        !interactionIsNull -> CommandFromType.SlashCommand
        !messageIsNull -> CommandFromType.TextCommand
        else -> throw illegalArgs // never happen
    }

    fun edit(
        content: String? = null,
        embeds: Collection<MessageEmbed> = emptyList(),
        components: Collection<LayoutComponent> = emptyList(),
        files: Collection<FileUpload> = emptyList(),
        replace: Boolean = false
    ) = when (type) {
        CommandFromType.SlashCommand -> {
            val webhookMessageUpdateAction =
                interaction!!.editMessage("@original", content, embeds, components, files, replace)
            webhookMessageUpdateAction.toUnifiedEditActionDispatcher()
        }

        CommandFromType.TextCommand -> {
            val messageAction = message!!.edit(content, embeds, components, files, replace)
            messageAction.toUnifiedEditActionDispatcher()
        }
    }
}
