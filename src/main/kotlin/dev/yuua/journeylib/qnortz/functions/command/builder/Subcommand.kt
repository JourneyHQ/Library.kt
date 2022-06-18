package dev.yuua.journeylib.qnortz.functions.command.builder

import dev.yuua.journeylib.qnortz.rules.RulesFunction
import dev.yuua.journeylib.qnortz.rules.RulesResult
import dev.yuua.journeylib.qnortz.functions.command.builder.function.SlashFunction
import dev.yuua.journeylib.qnortz.functions.command.builder.function.TextFunction
import dev.yuua.journeylib.qnortz.functions.command.builder.option.OptionTool
import dev.yuua.journeylib.qnortz.functions.command.event.UnifiedCommandInteractionEvent
import dev.yuua.journeylib.qnortz.rules.RulesResultType
import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.OptionData

class Subcommand {
    val name: String
    val description: String

    /**
     * Creates [Subcommand] instance. (for Java)
     * @param name The name of the subcommand.
     * @param description The description of the subcommand.
     */
    constructor(name: String, description: String) {
        this.name = name
        this.description = description
    }

    /**
     * Creates [Subcommand] instance. (for Kotlin)
     *
     * @param name The name of the subcommand.
     * @param description The description of the subcommand.
     * @param script The script which is applied to [Subcommand].
     */
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

    /**
     * Adds options to the subcommand. (for Java)
     *
     * @param options Options of the subcommand.
     */
    fun options(vararg options: OptionData) {
        this.jdaOptions.addAll(options)
    }

    /**
     * Adds the option to the subcommand. (for Kotlin)
     *
     * @param name The name of the subcommand.
     * @param description The description of the subcommand.
     * @param required Whether the option is required or not.
     * @param autocomplete Whether the option supports auto-complete.
     * @param script The script which is applied to [OptionData]
     */
    inline fun <reified T> option(
        name: String,
        description: String,
        required: Boolean = false,
        autocomplete: Boolean = false,
        script: OptionData.() -> Unit = {}
    ) {
        val jdaOption = OptionTool.option<T>(name, description, required, autocomplete, script)
        jdaOptions.add(jdaOption)
    }

    /**
     * Adds the function to the subcommand. (for Kotlin)
     * It only supports slash-command.
     *
     * @param function The function to run.
     */
    fun slashFunction(function: SlashCommandInteractionEvent.() -> Unit) {
        slashFunction = SlashFunction {
            function(it)
        }
    }

    /**
     * Adds the function to the subcommand. (for Java)
     * It only supports slash-command.
     *
     * @param function The function to run.
     */
    fun slashFunction(function: SlashFunction): Subcommand {
        slashFunction = function
        return this
    }

    /**
     * Adds the function to the subcommand. (for Kotlin)
     * It supports both text-command and slash-command.
     *
     * @param function The function to run.
     */
    fun textFunction(function: UnifiedCommandInteractionEvent.() -> Unit) {
        textFunction = TextFunction {
            function(it)
        }
    }

    /**
     * Adds the function to the subcommand. (for Java)
     * It supports both text-command and slash-command.
     *
     * @param function The function to run.
     */
    fun textFunction(function: TextFunction): Subcommand {
        textFunction = function
        return this
    }

    /**
     * Adds the rules function to the subcommand. (for Kotlin)
     * To use the command, [RulesResultType] must be [RulesResultType.Passed]
     *
     * @param script The script to control access.
     */
    fun rules(script: UnifiedCommandInteractionEvent.() -> RulesResult) {
        rulesFunction = RulesFunction {
            script(it)
        }
    }

    /**
     * Adds the rules function to the subcommand. (for Java)
     * To use the command, [RulesResultType] must be [RulesResultType.Passed]
     *
     * @param script The script to control access.
     */
    fun rules(script: RulesFunction<UnifiedCommandInteractionEvent>): Subcommand {
        rulesFunction = script
        return this
    }

    /**
     * Make the command can be executed on specified [ChannelType].
     *
     * @param channelType Channel types that the subcommand accepts.
     */
    fun acceptedOn(vararg channelType: ChannelType): Subcommand {
        acceptedOn.addAll(channelType)
        return this
    }
}
