package dev.yuua.journeylib.qnortz.limit

import dev.yuua.journeylib.qnortz.rules.RulesFunction
import dev.yuua.journeylib.qnortz.rules.RulesResult
import dev.yuua.journeylib.qnortz.rules.RulesResultType

data class Limit<T>(
    val guilds: List<String> = emptyList(),
    val channels: List<String> = emptyList(),
    val users: List<String> = emptyList(),
    val rule: RulesFunction<T> = RulesFunction { RulesResult(type = RulesResultType.Passed) }
) {
    fun isNotEmpty() = !(guilds.isEmpty() && users.isEmpty())
}

// todo support Discord's slash-command permissions (wait JDA & JDA-ktx)
