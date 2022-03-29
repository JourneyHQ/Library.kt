package dev.yuua.journeylib.discord.framework.command.builder.option

import net.dv8tion.jda.api.interactions.commands.OptionMapping
import net.dv8tion.jda.api.interactions.commands.OptionType

class FrOptionIndex {
    var hasInitialized = false

    lateinit var name: String
    var value: Any? = null
    var optionMapping: OptionMapping? = null
    lateinit var type: OptionType

    /**
     * ### OptionMapping から FrOption を初期化します。
     * @param option OptionMapping
     */
    fun fromOptionMapping(option: OptionMapping): FrOptionIndex {
        if (hasInitialized)
            throw UnsupportedOperationException("Already initialized!")
        else hasInitialized = true

        this.name = option.name
        this.type = option.type
        this.optionMapping = option

        return this
    }

    /**
     * ### 手動で FrOption を初期化します。
     * @param name Option の名前
     * @param mainType Option の型
     * @param value Option の内容
     */
    fun fromData(name: String, value: Any, type: OptionType): FrOptionIndex {
        if (hasInitialized)
            throw UnsupportedOperationException("Already initialized!")
        else hasInitialized = true

        this.name = name
        this.value = value
        this.type = type

        return this
    }
}
