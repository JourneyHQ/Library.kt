package dev.yuua.journeylib.qnortz.rules

data class RulesResult(val message: String? = null, val type: RulesResultType)

enum class RulesResultType(val title: String?) {
    PASSED(null), FAILED("Failed"), FORBIDDEN("Access Forbidden")
}
