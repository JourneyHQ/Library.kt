package dev.yuua.journeylib.discord.framework.function.scope

import dev.yuua.journeylib.discord.framework.function.command.builder.function.FrChecks
import dev.yuua.journeylib.discord.framework.function.command.builder.function.FrChecksResult
import dev.yuua.journeylib.discord.framework.function.command.builder.function.FrChecksResultType

data class FrCmdScope(
    val guilds: List<String> = listOf(),
    val users: List<String> = listOf(),
    val checks: FrChecks? = FrChecks { FrChecksResult("", FrChecksResultType.PASSED) }
)
