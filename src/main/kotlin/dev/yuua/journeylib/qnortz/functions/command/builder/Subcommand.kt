package dev.yuua.journeylib.qnortz.functions.command.builder

import dev.yuua.journeylib.qnortz.rules.RulesFunction
import dev.yuua.journeylib.qnortz.rules.RulesResult
import dev.yuua.journeylib.qnortz.functions.command.builder.function.SlashFunction
import dev.yuua.journeylib.qnortz.functions.command.builder.function.TextFunction
import dev.yuua.journeylib.qnortz.functions.command.builder.option.OptionTool
import dev.yuua.journeylib.qnortz.functions.command.event.UnifiedCommandInteractionEvent
import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.OptionData

class Subcommand {
    val name: String
    val description: String

    constructor(name: String, description: String) {
        this.name = name
        this.description = description
    }

    constructor(name: String, description: String, script: Subcommand.() -> Unit) {
        this.name = name
        this.description = description
        script()
    }

    var slashFunction: SlashFunction? = null
    var textFunction: TextFunction? = null
    val jdaOptions = mutableListOf<OptionData>()

    val acceptedOn = mutableListOf<ChannelType>()

    var rulesFunction: RulesFunction<UnifiedCommandInteractionEvent>? = null

    fun options(vararg options: OptionData) {
        this.jdaOptions.addAll(options)
    }

    inline fun <reified T> option(
        name: String,
        description: String,
        required: Boolean = false,
        autocomplete: Boolean = false,
        builder: OptionData.() -> Unit = {}
    ) {
        val jdaOption = OptionTool.option<T>(name, description, required, autocomplete, builder)
        jdaOptions.add(jdaOption)
    }

    // Kotlin DSL
    fun slashFunction(function: SlashCommandInteractionEvent.() -> Unit) {
        slashFunction = SlashFunction {
            function(it)
        }
    }

    // Java
    fun slashFunction(function: SlashFunction): Subcommand {
        slashFunction = function
        return this
    }

    // Kotlin DSL
    fun textFunction(function: UnifiedCommandInteractionEvent.() -> Unit) {
        textFunction = TextFunction {
            function(it)
        }
    }

    // Java
    fun textFunction(function: TextFunction): Subcommand {
        textFunction = function
        return this
    }

    // Kotlin DSL
    fun rules(script: UnifiedCommandInteractionEvent.() -> RulesResult) {
        rulesFunction = RulesFunction {
            script(it)
        }
    }

    // Java
    fun rules(script: RulesFunction<UnifiedCommandInteractionEvent>): Subcommand {
        rulesFunction = script
        return this
    }

    fun acceptedOn(vararg channelType: ChannelType): Subcommand {
        acceptedOn.addAll(channelType)
        return this
    }
}
