package dev.yuua.journeylib.discord.framework.command.scope

import dev.yuua.journeylib.discord.framework.command.builder.function.FrChecks
import dev.yuua.journeylib.discord.framework.command.builder.function.FrChecksResult
import dev.yuua.journeylib.discord.framework.command.builder.function.FrChecksResultType

data class FrCmdScope(
    val guilds: MutableList<String> = mutableListOf(),
    val users: MutableList<String> = mutableListOf(),
    val checks: FrChecks? = FrChecks { FrChecksResult("", FrChecksResultType.PASSED) }
)
