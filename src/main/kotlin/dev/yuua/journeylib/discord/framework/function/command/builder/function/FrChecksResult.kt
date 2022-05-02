package dev.yuua.journeylib.discord.framework.function.command.builder.function

import dev.yuua.journeylib.discord.framework.function.command.builder.function.FrChecksResultType.*

/**
 * Data class to store [FrChecks] results.
 * @param message Reason for stopping execution. If [type] is [PASSED], the [message] must be null.
 * @param type Result type. If it is not [PASSED], execution is stopped.
 */
data class FrChecksResult(
    val message: String?,
    val type: FrChecksResultType
) {
    init {
        when (type) {
            FORBIDDEN, ERROR, OTHER -> if (message == null)
                throw IllegalArgumentException("If result type is FORBIDDEN/ERROR/OTHER, the message cannot be null!")
            PASSED -> if (message != null)
                throw IllegalArgumentException("If result type is PASSED, the message must be null!")
        }
    }
}
