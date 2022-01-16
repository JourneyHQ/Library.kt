package dev.yuua.journeylib.discord

import net.dv8tion.jda.api.EmbedBuilder

object LibEmbed {
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
}