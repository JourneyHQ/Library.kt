package dev.yuua.journeylib.discord.framework_text.option

import net.dv8tion.jda.api.entities.*

/**
 * ### 型を指定して値を取り出します。
 */
object FrOptionTypeExtend {
    //fixme 更新
    fun FrOption.asUser(): User {
        return (value as User)
    }

    fun FrOption.asLong(): Long {
        return (value as Long)
    }

    fun FrOption.asBoolean(): Boolean {
        return (value as Boolean)
    }

    fun FrOption.asString(): String {
        return (value as String)
    }

    fun FrOption.asRole(): Role {
        return (value as Role)
    }

    fun FrOption.asDouble(): Double {
        return (value as Double)
    }

    fun FrOption.asGuildChannel(): GuildChannel {
        return (value as GuildChannel)
    }

    fun FrOption.asMember(): Member {
        return (value as Member)
    }

    fun FrOption.asMentionable(): IMentionable {
        return (value as IMentionable)
    }

    fun FrOption.asMessageChannel(): MessageChannel {
        return (value as MessageChannel)
    }
}