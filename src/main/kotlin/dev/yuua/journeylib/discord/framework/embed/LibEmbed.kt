package dev.yuua.journeylib.discord.framework.embed

import net.dv8tion.jda.api.EmbedBuilder

object LibEmbed {
    private fun escapeChannelMention(message: String):String {
        return message.let {
            var replaceMessageTemp = it

            for (matchResult in Regex("<.+?>").findAll(it))
                replaceMessageTemp = replaceMessageTemp.replace(matchResult.value, "`${matchResult.value}`")

            replaceMessageTemp
        }.let {
            val msg = "`$it`"
            if (msg.startsWith("``")) msg.substring(2)
            else msg
        }
    }

    fun failure(): EmbedBuilder {
        return EmbedBuilder().setColor(LibEmbedColor.failure)
    }

    fun caution(): EmbedBuilder {
        return EmbedBuilder().setColor(LibEmbedColor.caution)
    }

    fun success(): EmbedBuilder {
        return EmbedBuilder().setColor(LibEmbedColor.success)
    }

    fun normal(): EmbedBuilder {
        return EmbedBuilder().setColor(LibEmbedColor.normal)
    }

    fun failure(emoji: String, name: String, message: String): EmbedBuilder {
        return failure()
            .setTitle("$emoji _FAILED: ${name}_")
            .setDescription(escapeChannelMention(message))
    }

    fun success(emoji: String, name: String, message: String): EmbedBuilder {
        return failure()
            .setTitle("$emoji $name")
            .setDescription(escapeChannelMention(message))
    }
}