package dev.yuua.journeylib.qnortz.functions.command.builder

import dev.yuua.journeylib.qnortz.filter.Filter
import dev.yuua.journeylib.qnortz.functions.command.builder.function.SlashFunction
import dev.yuua.journeylib.qnortz.functions.command.builder.function.TextFunction
import dev.yuua.journeylib.qnortz.functions.command.builder.option.OptionTool
import dev.yuua.journeylib.qnortz.functions.command.event.UnifiedCommandInteractionEvent
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.OptionData


/**
 * Creates [CommandBase] instance.
 * @param name The name of the command.
 * @param description The description of the command.
 */
open class CommandBase<T>(val name: String, val description: String) {
    var filter = UnifiedCommandFilter()

    fun filter(
        guildIds: List<String> = emptyList(),
        channelIds: List<String> = emptyList(),
        userIds: List<String> = emptyList(),
        roleIds: List<String> = emptyList(),
        permissions: List<Permission> = emptyList(),
        channelTypes: List<ChannelType> = emptyList(),
        guildOnly: Boolean = false,
        script: UnifiedCommandInteractionEvent.() -> Boolean = { true }
    ) {
        filter =
            UnifiedCommandFilter(guildIds, channelIds, userIds, roleIds, permissions, channelTypes, guildOnly, script)
    }
}

open class CommandBaseWithFunction<T>(name: String, description: String) : CommandBase<T>(name, description) {
    var slashFunction: SlashFunction? = null
    var textFunction: TextFunction? = null
    val jdaOptions = mutableListOf<OptionData>()

    /**
     * Adds options to the command. (for Java)
     *
     * @param options Options of the command.
     */
    fun options(vararg options: OptionData) {
        this.jdaOptions.addAll(options)
    }

    /**
     * Adds the option to the command. (for Kotlin)
     *
     * @param name The name of the command.
     * @param description The description of the command.
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
     * Adds the function to the command. (for Kotlin)
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
     * Adds the function to the command. (for Java)
     * It only supports slash-command.
     *
     * @param function The function to run.
     */
    fun slashFunction(function: SlashFunction): CommandBaseWithFunction<T> {
        slashFunction = function
        return this
    }

    /**
     * Adds the function to the command. (for Kotlin)
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
     * Adds the function to the command. (for Java)
     * It supports both text-command and slash-command.
     *
     * @param function The function to run.
     */
    fun textFunction(function: TextFunction): CommandBaseWithFunction<T> {
        textFunction = function
        return this
    }
}

typealias UnifiedCommandFilter = Filter<UnifiedCommandInteractionEvent>
