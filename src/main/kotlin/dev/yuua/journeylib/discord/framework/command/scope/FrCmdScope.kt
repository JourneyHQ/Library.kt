package dev.yuua.journeylib.discord.framework.command.scope

import dev.yuua.journeylib.discord.framework.command.builder.function.FrChecks

data class FrCmdScope(
    val guilds: MutableList<String>,
    val users: MutableList<String>,
    val checks: FrChecks?
)
