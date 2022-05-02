package dev.yuua.journeylib.discord.framework.function.command.builder.function

/**
 * Result type of [FrChecks].
 */
enum class FrChecksResultType(val code: String?) {
    FORBIDDEN("Access Forbidden"),
    ERROR("An Error Occurred"),
    OTHER("Something Went Wrong"),
    PASSED(null)
}
