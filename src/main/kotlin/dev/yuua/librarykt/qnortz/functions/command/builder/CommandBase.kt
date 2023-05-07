package dev.yuua.librarykt.qnortz.functions.command.builder

import dev.minn.jda.ktx.events.CoroutineEventListener
import dev.yuua.librarykt.qnortz.filter.Filter
import dev.yuua.librarykt.qnortz.functions.command.builder.function.SlashFunction
import dev.yuua.librarykt.qnortz.functions.command.builder.function.TextFunction
import dev.yuua.librarykt.qnortz.functions.command.builder.option.OptionTool
import dev.yuua.librarykt.qnortz.functions.command.event.UnifiedCommandInteractionEvent
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.OptionData

typealias AutocompleteFunction = suspend CoroutineEventListener.(CommandAutoCompleteInteractionEvent) -> Unit

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
        script: UnifiedCommandInteractionEvent.() -> Pair<Boolean, String> = { true to "" }
    ) {
        filter =
            UnifiedCommandFilter(guildIds, channelIds, userIds, roleIds, permissions, channelTypes, guildOnly, script)
    }
}

open class CommandBaseWithFunction<T>(name: String, description: String) : CommandBase<T>(name, description) {
    var slashFunction: SlashFunction? = null
    var textFunction: TextFunction? = null
    val jdaOptions = mutableListOf<OptionData>()
    val autocompletes = hashMapOf<String, AutocompleteFunction>()

    /**
     * Adds the option to the command. (for Kotlin)
     *
     * @param name The name of the command.
     * @param description The description of the command.
     * @param required Whether the option is required or not.
     * @param autocomplete Whether the option supports autocomplete.
     * @param script The script which is applied to [OptionData]
     */
    inline fun <reified T> option(
        name: String,
        description: String,
        required: Boolean = false,
        script: OptionData.() -> Unit = {},
        noinline autocomplete: AutocompleteFunction? = null
    ) {
        val jdaOption = OptionTool.option<T>(name, description, required, autocomplete != null, script)

        if (autocomplete != null) autocompletes[name] = autocomplete

        jdaOptions.add(jdaOption)
    }

    /**
     * Adds options to the command. (for Java)
     *
     * @param options Options of the command.
     */
    fun options(vararg options: OptionData) {
        this.jdaOptions.addAll(options)
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
