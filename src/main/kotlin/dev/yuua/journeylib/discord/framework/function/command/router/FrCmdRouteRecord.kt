package dev.yuua.journeylib.discord.framework.function.command.router

import dev.yuua.journeylib.discord.framework.function.command.builder.function.FrChecks
import dev.yuua.journeylib.discord.framework.function.command.builder.function.FrSlashFunction
import dev.yuua.journeylib.discord.framework.function.command.builder.function.FrTextFunction
import dev.yuua.journeylib.discord.framework.function.command.builder.structure.FrCmdName
import net.dv8tion.jda.api.interactions.commands.build.OptionData

data class FrCmdRouteRecord(
    val struct: FrCmdTypeEnum,
    val cmd: FrCmdName,
    val subcmdGroup: FrCmdName,
    val subcmd: FrCmdName,
    val options: MutableList<OptionData>,
    val function: FrSlashFunction?,
    val textFunction: FrTextFunction?,
    val checks: MutableList<FrChecks>
) {
    init {
        if ((function != null && textFunction != null) || (function == null && textFunction == null))
            throw IllegalArgumentException("Function and TextFunction cannot be used at the same time!")
    }
}
