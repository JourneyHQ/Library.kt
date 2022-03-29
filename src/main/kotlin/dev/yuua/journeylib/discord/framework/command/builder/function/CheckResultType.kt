package dev.yuua.journeylib.discord.framework.command.builder.function

enum class CheckResultType(val code: String) {
    FORBIDDEN("Access Forbidden"), ERROR("An Error Occurred"), OTHER("Something Went Wrong"), PASSED("")
}
