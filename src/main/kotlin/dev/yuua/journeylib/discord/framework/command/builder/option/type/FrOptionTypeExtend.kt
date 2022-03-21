package dev.yuua.journeylib.discord.framework.command.builder.option.type

import dev.yuua.journeylib.discord.framework.command.builder.option.FrOptionIndex
import net.dv8tion.jda.api.entities.*

/**
 * ### 型を指定して値を取り出します。
 */
object FrOptionTypeExtend {
    fun <T> FrOptionIndex.asType(): T {
        @Suppress("UNCHECKED_CAST")
        return (value as T)
    }

    fun FrOptionIndex.asString(): String {
        return (value as String)
    }

    fun FrOptionIndex.asBoolean(): Boolean {
        return (value as Boolean)
    }

    fun FrOptionIndex.asUser(): User {
        return (value as User)
    }

    fun FrOptionIndex.asMember(): Member {
        return (value as Member)
    }

    fun FrOptionIndex.asRole(): Role {
        return (value as Role)
    }

    fun FrOptionIndex.asMentionable(): IMentionable {
        return (value as IMentionable)
    }

    fun FrOptionIndex.asLong(): Long {
        return (value as Long)
    }

    fun FrOptionIndex.asDouble(): Double {
        return (value as Double)
    }

    fun FrOptionIndex.asGuildChannel(): GuildChannel {
        return (value as GuildChannel)
    }

    fun FrOptionIndex.asAudioChannel(): AudioChannel {
        return (value as AudioChannel)
    }

    fun FrOptionIndex.asMessageChannel(): MessageChannel {
        return (value as MessageChannel)
    }

    fun FrOptionIndex.asTextChannel(): TextChannel {
        return (value as TextChannel)
    }

    fun FrOptionIndex.asThreadChannel(): ThreadChannel {
        return (value as ThreadChannel)
    }

    fun FrOptionIndex.asVoiceChannel(): VoiceChannel {
        return (value as VoiceChannel)
    }

    fun FrOptionIndex.asNewsChannel(): NewsChannel {
        return (value as NewsChannel)
    }

    fun FrOptionIndex.asStageChannel(): StageChannel {
        return (value as StageChannel)
    }
}
