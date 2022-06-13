package dev.yuua.journeylib.qnortz.functions.command.builder

import dev.minn.jda.ktx.interactions.commands.Command
import dev.minn.jda.ktx.interactions.commands.group
import dev.minn.jda.ktx.interactions.commands.subcommand
import dev.yuua.journeylib.qnortz.functions.command.CommandStructureType.*
import dev.yuua.journeylib.qnortz.functions.command.builder.function.CommandFunction
import dev.yuua.journeylib.qnortz.functions.command.builder.function.SlashFunction
import dev.yuua.journeylib.qnortz.functions.command.builder.function.TextFunction
import dev.yuua.journeylib.qnortz.functions.command.builder.option.OptionTool
import dev.yuua.journeylib.qnortz.functions.command.event.UnifiedCommandInteractionEvent
import dev.yuua.journeylib.qnortz.functions.command.router.CommandRoute
import dev.yuua.journeylib.qnortz.rules.RulesFunction
import dev.yuua.journeylib.qnortz.rules.RulesResult
import net.dv8tion.jda.api.entities.ChannelType
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData

class Command {
    val name: String
    val description: String

    constructor(name: String, description: String) {
        this.name = name
        this.description = description
    }

    constructor(name: String, description: String, script: Command.() -> Unit) {
        this.name = name
        this.description = description
        script()
    }

    var structureType = CommandType

    val subcommands = mutableListOf<Subcommand>()
    val subcommandGroups = mutableListOf<SubcommandGroup>()

    var slashFunction: SlashFunction? = null
    var textFunction: TextFunction? = null
    val jdaOptions = mutableListOf<OptionData>()

    val acceptedOn = mutableListOf<ChannelType>()

    var rulesFunction: RulesFunction<UnifiedCommandInteractionEvent>? = null

    // Java
    fun subcommands(vararg commands: Subcommand): Command {
        subcommands.addAll(commands)
        structureType = SubcommandType
        return this
    }

    // Kotlin DSL
    fun subcommand(name: String, description: String, script: Subcommand.() -> Unit) {
        subcommands.add(Subcommand(name, description, script))
        structureType = SubcommandType
    }

    // Java
    fun subcommandGroups(vararg commands: SubcommandGroup): Command {
        subcommandGroups.addAll(commands)
        structureType = SubcommandGroupType
        return this
    }

    // Kotlin DSL
    fun subcommandGroup(name: String, description: String, script: SubcommandGroup.() -> Unit) {
        subcommandGroups.add(SubcommandGroup(name, description, script))
        structureType = SubcommandGroupType
    }

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
    fun slashFunction(function: SlashFunction): Command {
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
    fun textFunction(function: TextFunction): Command {
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
    fun rules(script: RulesFunction<UnifiedCommandInteractionEvent>): Command {
        rulesFunction = script
        return this
    }

    fun acceptedOn(vararg channelType: ChannelType): Command {
        acceptedOn.addAll(channelType)
        return this
    }

    fun build(): CommandObject {
        val routes = hashMapOf<CommandRoute, CommandFunction>()
        lateinit var command: SlashCommandData

        fun addRoute(
            subcommandGroup: String?,
            subcommand: String?,
            slashFunction: SlashFunction?,
            textFunction: TextFunction?,
            options: MutableList<OptionData>,
            acceptedOn: List<ChannelType>,
            vararg rules: RulesFunction<UnifiedCommandInteractionEvent>?
        ) {
            routes[CommandRoute(name, subcommandGroup, subcommand)] =
                CommandFunction(
                    slashFunction,
                    textFunction,
                    options,
                    listOfNotNull(rulesFunction, *rules),
                    acceptedOn
                )
        }

        // todo clean up code
        when (structureType) {
            CommandType -> {
                addRoute(
                    null, null,
                    slashFunction, textFunction, jdaOptions, acceptedOn
                )
                command = Command(name, description) {
                    addOptions(jdaOptions)
                }
            }

            SubcommandType -> {
                command = Command(name, description) {
                    for (subcommand in this@Command.subcommands) {
                        subcommand(subcommand.name, subcommand.description) {
                            addOptions(subcommand.jdaOptions)
                        }
                        addRoute(
                            null, subcommand.name,
                            subcommand.slashFunction, subcommand.textFunction,
                            subcommand.jdaOptions,
                            subcommand.acceptedOn,
                            subcommand.rulesFunction
                        )
                    }
                }
            }

            SubcommandGroupType -> {
                command = Command(name, description) {
                    for (subcommandGroup in this@Command.subcommandGroups) {
                        group(subcommandGroup.name, subcommandGroup.description) {
                            for (subcommand in subcommandGroup.subcommands) {
                                subcommand(subcommand.name, subcommand.description) {
                                    addOptions(subcommand.jdaOptions)
                                }
                                addRoute(
                                    subcommandGroup.name, subcommand.name,
                                    subcommand.slashFunction, subcommand.textFunction,
                                    subcommand.jdaOptions,
                                    subcommand.acceptedOn,
                                    subcommandGroup.rulesFunction, subcommand.rulesFunction
                                )
                            }
                        }
                    }
                }
            }
        }

        return CommandObject(routes, command)
    }
}
