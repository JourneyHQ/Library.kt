package dev.yuua.journeylib.qnortz.rules

fun interface RulesFunction<E> {
    fun execute(event: E): RulesResult
}
