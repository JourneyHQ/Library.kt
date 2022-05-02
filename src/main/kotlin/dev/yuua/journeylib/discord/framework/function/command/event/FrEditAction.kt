package dev.yuua.journeylib.discord.framework.function.command.event

import dev.minn.jda.ktx.messages.*
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.interactions.InteractionHook

class FrEditAction(
    val interaction: InteractionHook?,
    val message: Message?
) {
    var type: FrCmdType = if (interaction != null && message == null)
        FrCmdType.SLASH
    else if (interaction == null && message != null)
        FrCmdType.TEXT
    else throw IllegalArgumentException("One event must be null!")

    fun edit(
        content: String? = null,
        embed: MessageEmbed? = null,
        embeds: Embeds? = null,
        components: Components? = null,
        file: NamedFile? = null,
        files: Files? = null,
        replace: Boolean = false
    ): FrEditDispatchAction {
        return when (type) {
            FrCmdType.SLASH -> {
                val webhookMessageUpdateAction =
                    interaction!!.editMessage("@original", content, embed, embeds, components, file, files, replace)
                FrEditDispatchAction(webhookMessageUpdateAction, null)
            }
            FrCmdType.TEXT -> {
                val messageAction = message!!.edit(content, embed, embeds, components, file, files, replace)
                FrEditDispatchAction(null, messageAction)
            }
        }
    }
}
