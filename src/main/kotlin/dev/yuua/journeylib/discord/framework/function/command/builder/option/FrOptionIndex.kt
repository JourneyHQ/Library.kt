package dev.yuua.journeylib.discord.framework.function.command.builder.option

import net.dv8tion.jda.api.interactions.commands.OptionMapping
import net.dv8tion.jda.api.interactions.commands.OptionType

/**
 * Stores the data for the option.
 */
class FrOptionIndex {
    private var hasInitialized = false

    lateinit var name: String
    var value: Any? = null
    var optionMapping: OptionMapping? = null
    lateinit var type: OptionType

    /**
     * Initialize [FrOptionIndex] from [OptionMapping].
     * @param option [OptionMapping] to use.
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
     * Initialize [FrOptionIndex] from data.
     * @param name Name of option.
     * @param value Value of option.
     * @param type Type of option.
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
