package dev.yuua.journeylib.qnortz.functions.command.router

import dev.minn.jda.ktx.messages.Embed
import dev.yuua.journeylib.qnortz.QnortzColor
import dev.yuua.journeylib.qnortz.codeBlock
import net.dv8tion.jda.api.entities.ChannelType

fun invalidChannelTypeEmbed(acceptedOn: List<ChannelType>) =
    Embed {
        title = ":interrobang: Invalid Channel Type!"
        description = codeBlock(
            """
            Following channel types are accepted:
            ${acceptedOn.joinToString(" , ")}
            """.trimIndent()
        )
        color = QnortzColor.Red.int()
    }

fun accessForbiddenEmbed(message: String?) = Embed {
    title = ":interrobang: Access Forbidden!"
    description = codeBlock(message ?: "You do not have access to this command.")
    color = QnortzColor.Red.int()
}
