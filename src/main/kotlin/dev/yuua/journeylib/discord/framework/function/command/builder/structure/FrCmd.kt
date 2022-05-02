package dev.yuua.journeylib.discord.framework.function.command.builder.structure

import dev.yuua.journeylib.discord.framework.function.command.builder.function.FrChecks
import dev.yuua.journeylib.discord.framework.function.command.builder.function.FrChecksResult
import dev.yuua.journeylib.discord.framework.function.command.builder.function.FrSlashFunction
import dev.yuua.journeylib.discord.framework.function.command.builder.function.FrTextFunction
import dev.yuua.journeylib.discord.framework.function.command.builder.option.FrOption
import dev.yuua.journeylib.discord.framework.function.command.event.FrCmdEvent
import dev.yuua.journeylib.discord.framework.function.command.router.FrCmdRouteRecord
import dev.yuua.journeylib.discord.framework.function.command.router.FrCmdTypeEnum.*
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData

/**
 * Stores all information about the command.
 * @param name Name of command.
 * @param details Details of command.
 * @param alias Aliases of command.
 */
class FrCmd(val name: String, val details: String, vararg val alias: String) {
    var build = false
    var cmdStruct = Cmd

    val jdaCmdData = Commands.slash(name, details)

    val routes = mutableListOf<FrCmdRouteRecord>()

    var function: FrSlashFunction? = null
    var textFunction: FrTextFunction? = null
    val options = mutableListOf<OptionData>()
    val checks = mutableListOf<FrChecks>()

    val subcmds = mutableListOf<FrSubcmd>()
    val subcmdGroups = mutableListOf<FrSubcmdGroup>()

    /**
     * Add subcommands to [FrCmd].
     * @param subcmds [FrSubcmd]s to add.
     */
    fun addSubcmd(vararg subcmds: FrSubcmd): FrCmd {
        this.subcmds.addAll(subcmds)
        checks.addAll(subcmds.mapNotNull { it.checks })
        jdaCmdData.addSubcommands(subcmds.map { it.jdaSubcmdData })
        cmdStruct = Cmd_Subcmd
        return this
    }

    /**
     * Add [subcmdGroups] to [FrCmd].
     * @param subcmdGroups [FrSubcmdGroup]s to add.
     */
    fun addSubcmdGroup(vararg subcmdGroups: FrSubcmdGroup): FrCmd {
        this.subcmdGroups.addAll(subcmdGroups)
        for (subcmdGroup in subcmdGroups) {
            val subcmdGroupChecks = subcmdGroup.checks
            if (subcmdGroupChecks != null) checks.add(subcmdGroupChecks)
            checks.addAll(subcmdGroup.subcmds.mapNotNull { it.checks })
        }
        jdaCmdData.addSubcommandGroups(subcmdGroups.map { it.jdaSubcmdGroupData })
        cmdStruct = Cmd_SubcmdGroup
        return this
    }

    /**
     * Add [options] to [FrCmd].
     * @param options [OptionData]s to add.
     */
    fun addOptions(vararg options: OptionData): FrCmd {
        this.options.addAll(options)
        jdaCmdData.addOptions(*options)
        return this
    }

    /**
     * Add option to [FrCmd].
     * @param name Name of [OptionData].
     * @param details Description of [OptionData].
     * @param required Whether the option is required or not.
     * @param autocomplete Whether the option supports autocomplete or not.
     * @param builder Code to adapt to [OptionData].
     */
    inline fun <reified T> addOption(
        name: String,
        details: String,
        required: Boolean = false,
        autocomplete: Boolean = false,
        builder: OptionData.() -> Unit = {}
    ): FrCmd {
        val option = FrOption<T>(name, details, required, autocomplete, builder)
        this.options.add(option)
        jdaCmdData.addOptions(option)
        return this
    }

    fun setFunction(function: FrSlashFunction): FrCmd {
        if (cmdStruct != Cmd)
            throw IllegalStateException("When use Subcommand, you cannot configure a Function to Command!")

        this.function = function
        return this
    }

    fun setFunction(function: FrTextFunction): FrCmd {
        if (cmdStruct != Cmd)
            throw IllegalStateException("When use Subcommand, you cannot configure a Function to Command!")

        this.textFunction = function
        return this
    }

    fun setChecks(checks: (FrCmdEvent) -> FrChecksResult): FrCmd {
        this.checks.add(FrChecks { checks(it) })
        return this
    }

    fun build(): FrCmdBuild {
        when (cmdStruct) {
            Cmd -> {
                routes.add(
                    FrCmdRouteRecord(
                        cmdStruct,
                        FrCmdName(name, *alias),
                        FrCmdName(null),
                        FrCmdName(null),
                        options,
                        function,
                        textFunction,
                        checks
                    )
                )
            }
            Cmd_Subcmd -> {
                for (subcmd in subcmds) {
                    routes.add(
                        FrCmdRouteRecord(
                            cmdStruct,
                            FrCmdName(name, *alias),
                            FrCmdName(null),
                            FrCmdName(subcmd.name, *subcmd.alias),
                            subcmd.options,
                            subcmd.function,
                            subcmd.textFunction,
                            checks
                        )
                    )
                }
            }
            Cmd_SubcmdGroup -> {
                for (subcmdGroup in subcmdGroups) {
                    for (subcmd in subcmdGroup.subcmds) {
                        routes.add(
                            FrCmdRouteRecord(
                                cmdStruct,
                                FrCmdName(name, *alias),
                                FrCmdName(subcmdGroup.name, *subcmdGroup.alias),
                                FrCmdName(subcmd.name, *subcmd.alias),
                                subcmd.options,
                                subcmd.function,
                                subcmd.textFunction,
                                checks
                            )
                        )
                    }
                }
            }
        }
        build = true
        return FrCmdBuild(this)
    }
}
