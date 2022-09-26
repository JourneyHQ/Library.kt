package dev.yuua.journeylib.qnortz.limit

import dev.yuua.journeylib.qnortz.rules.RulesFunction
import dev.yuua.journeylib.qnortz.rules.RulesResult
import dev.yuua.journeylib.qnortz.rules.RulesResultType

data class Limit<T>(
    val guildIds: List<String> = emptyList(),
    val channelIds: List<String> = emptyList(),
    val userIds: List<String> = emptyList(),
    val rule: RulesFunction<T> = RulesFunction { RulesResult(type = RulesResultType.Passed) }
) {
    fun isNotEmpty() = !(guildIds.isEmpty() && userIds.isEmpty())
}
