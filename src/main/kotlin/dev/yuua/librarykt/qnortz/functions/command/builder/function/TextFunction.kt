package dev.yuua.librarykt.qnortz.functions.command.builder.function

import dev.yuua.librarykt.qnortz.functions.command.event.UnifiedCommandInteractionEvent

/**
 * Interface for TextCommand function.
 */
fun interface TextFunction {
    fun execute(event: UnifiedCommandInteractionEvent)
}
