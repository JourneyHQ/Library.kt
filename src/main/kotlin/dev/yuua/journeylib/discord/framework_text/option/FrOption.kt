package dev.yuua.journeylib.discord.framework_text.option

import net.dv8tion.jda.api.interactions.commands.OptionMapping
import net.dv8tion.jda.api.interactions.commands.OptionType

/**
 *
 */
class FrOption {
    private val initErrorMsg = "すでに初期化されています！"

    private var hasInitialized = false

    lateinit var name: String
    lateinit var type: OptionType
    lateinit var value: Any

    /**
     * ### OptionMapping から FrOption を初期化します。
     * @param option OptionMapping
     */
    fun fromOptionMapping(option: OptionMapping, expectedType: OptionType) {
        if (hasInitialized)
            throw UnsupportedOperationException(initErrorMsg)
        else hasInitialized = true

        name = option.name
        type = option.type

        value = when (type) {
            OptionType.SUB_COMMAND,
            OptionType.SUB_COMMAND_GROUP,
            OptionType.STRING,
            OptionType.UNKNOWN -> option.asString
            OptionType.INTEGER, OptionType.NUMBER -> option.asLong
            OptionType.BOOLEAN -> option.asBoolean
            OptionType.USER -> option.asUser
            OptionType.CHANNEL -> option.asGuildChannel //もしかしたらMessageChannelの方かも
            OptionType.ROLE -> option.asRole
            OptionType.MENTIONABLE -> option.asMentionable
            //Member,Doubleが無い
        }
    }

    /**
     * ### 手動で FrOption を初期化します。
     * @param name Option の名前
     * @param type Option の型
     * @param value Option の内容
     */
    fun fromData(name: String, type: OptionType, value: Any) {
        if (hasInitialized)
            throw UnsupportedOperationException(initErrorMsg)
        else hasInitialized = true

        this.name = name
        this.type = type
        this.value = value
    }
}