package dev.yuua.journeylib.discord.framework.function.command.builder.function

import dev.yuua.journeylib.discord.framework.function.command.event.FrCmdEvent

fun interface FrChecks {
    /**
     * Function to control by custom rules whether [FrCmdEvent] should be executed or not.
     * @param event [FrCmdEvent] to check.
     * @return [FrChecksResult]
     */
    fun check(event: FrCmdEvent): FrChecksResult
}
