package dev.yuua.journeylib.discord.framework.command.builder.function

import dev.yuua.journeylib.discord.framework.command.event.FrCmdEvent

fun interface FrChecks {
    fun check(event: FrCmdEvent): FrChecksResult
}
