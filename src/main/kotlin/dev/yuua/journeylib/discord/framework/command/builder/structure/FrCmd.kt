package dev.yuua.journeylib.discord.framework.command.builder.structure

import dev.yuua.journeylib.discord.framework.command.router.FrCmdStruct.*
import dev.yuua.journeylib.discord.framework.command.router.FrCmdRouteRecord
import dev.yuua.journeylib.discord.framework.command.builder.function.FrChecks
import dev.yuua.journeylib.discord.framework.command.builder.function.FrFunction
import dev.yuua.journeylib.discord.framework.command.builder.option.FrOption
import dev.yuua.journeylib.discord.framework.command.builder.option.FrOptionLib.toJDAOptionData
import net.dv8tion.jda.api.interactions.commands.build.Commands

class FrCmd(val name: String, val details: String) {
    var build = false
    var cmdStruct = Cmd

    val jdaCmdData = Commands.slash(name, details)

    val routes = mutableListOf<FrCmdRouteRecord>()

    lateinit var function: FrFunction
    val options = mutableListOf<FrOption>()
    val checks = mutableListOf<FrChecks>()

    val subcmds = mutableListOf<FrSubcmd>()
    val subcmdGroups = mutableListOf<FrSubcmdGroup>()

    fun addSubcmd(vararg subcmds: FrSubcmd): FrCmd {
        this.subcmds.addAll(subcmds)
        checks.addAll(subcmds.map { it.checks })
        jdaCmdData.addSubcommands(subcmds.map { it.jdaSubcmdData })
        cmdStruct = Cmd_Subcmd
        return this
    }

    fun addSubcmdGroup(vararg subcmdGroups: FrSubcmdGroup): FrCmd {
        this.subcmdGroups.addAll(subcmdGroups)
        for (subcmdGroup in subcmdGroups) {
            checks.add(subcmdGroup.checks)
            checks.addAll(subcmdGroup.subcmds.map { it.checks })
        }
        jdaCmdData.addSubcommandGroups(subcmdGroups.map { it.jdaSubcmdGroupData })
        cmdStruct = Cmd_SubcmdGroup
        return this
    }

    fun addOption(vararg options: FrOption): FrCmd {
        this.options.addAll(options)
        jdaCmdData.addOptions(options.map { it.toJDAOptionData() })
        return this
    }

    fun setFunction(function: FrFunction): FrCmd {
        if (cmdStruct != Cmd)
            throw IllegalStateException("Subcommandを使用する場合、CommandにFunctionを設定することはできません。")

        this.function = function
        return this
    }

    fun addChecks(checks: FrChecks): FrCmd {
        this.checks.add(checks)
        return this
    }

    fun build(): FrCmdBuild {
        when (cmdStruct) {
            Cmd -> {
                routes.add(FrCmdRouteRecord(name, null, null, options, function, checks))
            }
            Cmd_Subcmd -> {
                for (subcmd in subcmds) {
                    routes.add(
                        FrCmdRouteRecord(
                            name,
                            null,
                            subcmd.name,
                            subcmd.options,
                            subcmd.function,
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
                                name,
                                subcmdGroup.name,
                                subcmd.name,
                                subcmd.options,
                                subcmd.function,
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
