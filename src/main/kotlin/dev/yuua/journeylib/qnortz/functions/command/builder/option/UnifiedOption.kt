package dev.yuua.journeylib.qnortz.functions.command.builder.option

import net.dv8tion.jda.api.interactions.commands.OptionMapping
import net.dv8tion.jda.api.interactions.commands.OptionType

/**
 * Option that capable with both SlashCommand and TextCommand.
 */
class UnifiedOption {
    private var hasInitialized = false

    var name: String
    var value: Any? = null
    var optionMapping: OptionMapping? = null
    var type: OptionType

    private val alreadyInitError = UnsupportedOperationException("Already initialized!")

    constructor(option: OptionMapping) {
        if (hasInitialized)
            throw alreadyInitError
        else hasInitialized = true

        this.name = option.name
        this.type = option.type
        this.optionMapping = option
    }

    constructor(name: String, value: Any, type: OptionType) {
        if (hasInitialized)
            throw alreadyInitError
        else hasInitialized = true

        this.name = name
        this.value = value
        this.type = type
    }
}
