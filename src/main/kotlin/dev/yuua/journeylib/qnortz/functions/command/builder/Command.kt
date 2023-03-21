package dev.yuua.journeylib.qnortz.functions.command.builder

import dev.minn.jda.ktx.interactions.commands.Command
import dev.minn.jda.ktx.interactions.commands.group
import dev.minn.jda.ktx.interactions.commands.restrict
import dev.minn.jda.ktx.interactions.commands.subcommand
import dev.yuua.journeylib.qnortz.functions.command.CommandStructureType.*
import dev.yuua.journeylib.qnortz.functions.command.builder.function.CommandFunction
import dev.yuua.journeylib.qnortz.functions.command.builder.function.SlashFunction
import dev.yuua.journeylib.qnortz.functions.command.builder.function.TextFunction
import dev.yuua.journeylib.qnortz.functions.command.router.CommandRoute
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData

class Command : CommandBaseWithFunction<Command> {
    /**
     * Creates [Command] instance. (for Java)
     * @param name The name of the command.
     * @param description The description of the command.
     */
    constructor(name: String, description: String) : super(name, description)

    /**
     * Creates [Command] instance. (for Kotlin)
     *
     * @param name The name of the command.
     * @param description The description of the command.
     * @param script The script which is applied to [Command].
     */
    constructor(name: String, description: String, script: Command.() -> Unit) : super(name, description) {
        script()
    }

    var structureType = CommandType

    val subcommands = mutableListOf<Subcommand>()
    val subcommandGroups = mutableListOf<SubcommandGroup>()

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
            options: List<OptionData>,
            autocompletes: HashMap<String, AutocompleteFunction>,
            vararg filter: UnifiedCommandFilter?
        ) {
            routes[CommandRoute(name, subcommandGroup, subcommand)] =
                CommandFunction(
                    slashFunction,
                    textFunction,
                    options,
                    autocompletes,
                    filter.filterNotNull()
                )
        }

        // todo clean up code
        when (structureType) {
            CommandType -> {
                addRoute(
                    null, null,
                    slashFunction, textFunction, jdaOptions, autocompletes, filter
                )
                command = Command(name, description) SlashCommand@{
                    restrict(filter.guildOnly, filter.defaultMemberPermissions)
                    addOptions(jdaOptions)
                }
            }

            SubcommandType -> {
                command = Command(name, description) SlashCommand@{
                    restrict(filter.guildOnly, filter.defaultMemberPermissions)
                    for (subcommand in this@Command.subcommands) {
                        subcommand(subcommand.name, subcommand.description) {
                            addOptions(subcommand.jdaOptions)
                        }
                        addRoute(
                            null, subcommand.name,
                            subcommand.slashFunction, subcommand.textFunction,
                            subcommand.jdaOptions, subcommand.autocompletes,
                            filter, subcommand.filter
                        )
                    }
                }
            }

            SubcommandGroupType -> {
                command = Command(name, description) SlashCommand@{
                    restrict(filter.guildOnly, filter.defaultMemberPermissions)
                    for (subcommandGroup in this@Command.subcommandGroups) {
                        group(subcommandGroup.name, subcommandGroup.description) {
                            for (subcommand in subcommandGroup.subcommands) {
                                subcommand(subcommand.name, subcommand.description) {
                                    addOptions(subcommand.jdaOptions)
                                }
                                addRoute(
                                    subcommandGroup.name, subcommand.name,
                                    subcommand.slashFunction, subcommand.textFunction,
                                    subcommand.jdaOptions, subcommand.autocompletes,
                                    filter, subcommandGroup.filter, subcommand.filter
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
