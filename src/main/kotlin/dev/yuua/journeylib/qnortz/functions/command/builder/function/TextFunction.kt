package dev.yuua.journeylib.qnortz.functions.command.builder.function

import dev.yuua.journeylib.qnortz.functions.command.event.UnifiedCommandInteractionEvent

fun interface TextFunction {
    fun execute(event: UnifiedCommandInteractionEvent)
}
