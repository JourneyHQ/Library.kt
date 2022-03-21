package dev.yuua.journeylib.discord.framework.command.builder.option

import dev.yuua.journeylib.discord.framework.command.builder.option.FrOptionLib.toFrOptionType
import dev.yuua.journeylib.discord.framework.command.builder.option.type.MainType
import dev.yuua.journeylib.discord.framework.command.builder.option.type.SubType
import net.dv8tion.jda.api.interactions.commands.OptionMapping
import net.dv8tion.jda.api.interactions.commands.OptionType

/**
 *
 */
class FrOptionIndex {
    private var hasInitialized = false

    lateinit var name: String
    lateinit var mainType: MainType
    lateinit var subType: Array<out SubType>
    lateinit var value: Any

    /**
     * ### OptionMapping から FrOption を初期化します。
     * @param option OptionMapping
     */
    fun fromOptionMapping(option: OptionMapping, vararg subType: SubType): FrOptionIndex {
        if (hasInitialized)
            throw UnsupportedOperationException("すでに初期化されています！")
        else hasInitialized = true

        this.name = option.name
        this.mainType = option.type.toFrOptionType()
        this.subType = subType

        this.value = when (option.type) {
            OptionType.STRING -> option.asString
            OptionType.INTEGER -> option.asInt
            OptionType.BOOLEAN -> option.asBoolean
            OptionType.USER -> option.asUser
            OptionType.CHANNEL ->
                try {
                    option.asTextChannel
                } catch (e: IllegalStateException) {
                    option.asVoiceChannel
                }
            OptionType.ROLE -> option.asRole
            OptionType.MENTIONABLE -> option.asMentionable
            OptionType.NUMBER ->
                try {
                    option.asLong
                } catch (e: IllegalStateException) {
                    option.asDouble
                }
            OptionType.ATTACHMENT -> option.asAttachment
            else -> throw UnsupportedOperationException("この型はFrOptionでサポートされていません！")
        }!!
        return this
    }

    /**
     * ### 手動で FrOption を初期化します。
     * @param name Option の名前
     * @param mainType Option の型
     * @param value Option の内容
     */
    fun fromData(name: String, value: Any, mainType: MainType, vararg subType: SubType): FrOptionIndex {
        if (hasInitialized)
            throw UnsupportedOperationException("すでに初期化されています！")
        else hasInitialized = true

        this.name = name
        this.mainType = mainType
        this.subType = subType
        this.value = value

        return this
    }
}
