package dev.yuua.journeylib.qnortz.functions.command.builder

import dev.minn.jda.ktx.interactions.commands.Command
import dev.minn.jda.ktx.interactions.commands.group
import dev.minn.jda.ktx.interactions.commands.restrict
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
import dev.yuua.journeylib.qnortz.rules.RulesResultType
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData

class Command {
    val name: String
    val description: String

    /**
     * Creates [Command] instance. (for Java)
     * @param name The name of the command.
     * @param description The description of the command.
     */
    constructor(name: String, description: String) {
        this.name = name
        this.description = description
    }

    /**
     * Creates [Command] instance. (for Kotlin)
     *
     * @param name The name of the command.
     * @param description The description of the command.
     * @param script The script which is applied to [Command].
     */
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

    /**
     * Adds subcommands to the command. (for Java)
     *
     * @param commands Subcommands to add.
     */
    fun subcommands(vararg commands: Subcommand): Command {
        subcommands.addAll(commands)
        structureType = SubcommandType
        return this
    }

    /**
     * Adds the subcommand to the command. (for Kotlin)
     *
     * @param name The name of the subcommand.
     * @param description The description of the subcommand.
     * @param script The script which is applied to [Subcommand].
     */
    fun subcommand(name: String, description: String, script: Subcommand.() -> Unit) {
        subcommands.add(Subcommand(name, description, script))
        structureType = SubcommandType
    }

    /**
     * Adds subcommand groups to the command. (for Java)
     *
     * @param commands SubcommandGroups to add.
     */
    fun subcommandGroups(vararg commands: SubcommandGroup): Command {
        subcommandGroups.addAll(commands)
        structureType = SubcommandGroupType
        return this
    }

    /**
     * Adds the subcommand group to the command. (for Kotlin)
     *
     * @param name The name of the subcommand group.
     * @param description The description of the subcommand group.
     * @param script The script which is applied to [SubcommandGroup].
     */
    fun subcommandGroup(name: String, description: String, script: SubcommandGroup.() -> Unit) {
        subcommandGroups.add(SubcommandGroup(name, description, script))
        structureType = SubcommandGroupType
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
    fun slashFunction(function: SlashFunction): Command {
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
    fun textFunction(function: TextFunction): Command {
        textFunction = function
        return this
    }

    /**
     * Adds the rules function to the command. (for Kotlin)
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
     * Adds the rules function to the command. (for Java)
     * To use the command, [RulesResultType] must be [RulesResultType.Passed]
     *
     * @param script The script to control access.
     */
    fun rules(script: RulesFunction<UnifiedCommandInteractionEvent>): Command {
        rulesFunction = script
        return this
    }

    /**
     * Make the command can be executed on specified [ChannelType].
     *
     * @param channelType Channel types that the command accepts.
     */
    fun acceptedOn(vararg channelType: ChannelType): Command {
        acceptedOn.addAll(channelType)
        return this
    }

    var guildOnly = false
    var permissions: DefaultMemberPermissions? = null

    fun restrict(guildOnly: Boolean = false, permissions: DefaultMemberPermissions? = null): Command {
        this.guildOnly = guildOnly
        this.permissions = permissions
        return this
    }

    fun restrict(guildOnly: Boolean = false, vararg permissions: Permission) =
        restrict(guildOnly, DefaultMemberPermissions.enabledFor(*permissions))

    /**
     * Creates [CommandObject]
     */
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
                    this.restrict(guildOnly, permissions)
                    addOptions(jdaOptions)
                }
            }

            SubcommandType -> {
                command = Command(name, description) SlashCommand@{
                    this.restrict(guildOnly, permissions)
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
                command = Command(name, description) SlashCommand@{
                    this.restrict(guildOnly, permissions)
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
