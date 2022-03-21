package dev.yuua.journeylib.discord.framework.command.router

import dev.yuua.journeylib.discord.framework.command.builder.function.FrChecks
import dev.yuua.journeylib.discord.framework.command.builder.function.FrFunction
import dev.yuua.journeylib.discord.framework.command.builder.option.FrOption
import dev.yuua.journeylib.discord.framework.command.scope.FrCmdScope

data class FrCmdRouteRecord(
    val cmd: String,
    val subcmdGroup: String?,
    val subcmd: String?,
    val options: MutableList<FrOption>,
    val function: FrFunction,
    val checks: MutableList<FrChecks>
)
