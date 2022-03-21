package dev.yuua.journeylib.discord.framework.command.builder.structure

import dev.yuua.journeylib.discord.framework.command.builder.function.FrChecks
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData

class FrSubcmdGroup(val name: String, val details: String) {

    val jdaSubcmdGroupData = SubcommandGroupData(name, details)

    lateinit var checks: FrChecks

    val subcmds = mutableListOf<FrSubcmd>()

    fun addSubcmd(vararg subcmds: FrSubcmd): FrSubcmdGroup {
        this.subcmds.addAll(subcmds)
        jdaSubcmdGroupData.addSubcommands(subcmds.map { it.jdaSubcmdData })
        return this
    }

    fun addChecks(checks: FrChecks): FrSubcmdGroup {
        this.checks = checks
        return this
    }
}
