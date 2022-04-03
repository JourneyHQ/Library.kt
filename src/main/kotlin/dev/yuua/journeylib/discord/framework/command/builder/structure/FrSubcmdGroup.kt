package dev.yuua.journeylib.discord.framework.command.builder.structure

import dev.yuua.journeylib.discord.framework.command.builder.function.FrChecks
import dev.yuua.journeylib.discord.framework.command.builder.function.FrChecksResult
import dev.yuua.journeylib.discord.framework.command.event.FrCmdEvent
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData

class FrSubcmdGroup(val name: String, val details: String, vararg val alias: String) {

    val jdaSubcmdGroupData = SubcommandGroupData(name, details)

    var checks: FrChecks? = null

    val subcmds = mutableListOf<FrSubcmd>()

    fun addSubcmd(vararg subcmds: FrSubcmd): FrSubcmdGroup {
        this.subcmds.addAll(subcmds)
        jdaSubcmdGroupData.addSubcommands(subcmds.map { it.jdaSubcmdData })
        return this
    }

    fun setChecks(checks: (FrCmdEvent) -> FrChecksResult): FrSubcmdGroup {
        this.checks = FrChecks { checks(it) }
        return this
    }
}
