package dev.yuua.librarykt.qnortz.functions.command.router

import dev.minn.jda.ktx.messages.Embed
import dev.yuua.librarykt.qnortz.QnortzColor
import dev.yuua.librarykt.qnortz.codeBlock
import net.dv8tion.jda.api.entities.channel.ChannelType

fun invalidChannelTypeEmbed(acceptedOn: List<ChannelType>) =
    Embed {
        title = ":interrobang: Invalid Channel Type!"
        description = codeBlock("""
            Following channel types are accepted:
            ${acceptedOn.joinToString(" , ")}
        """.trimIndent())
        color = QnortzColor.Red.int()
    }

fun accessForbiddenEmbed(message: String? = null) = Embed {
    title = ":interrobang: Access Forbidden!"
    description = codeBlock(message ?: "You do not have access to this command.")
    color = QnortzColor.Red.int()
}
