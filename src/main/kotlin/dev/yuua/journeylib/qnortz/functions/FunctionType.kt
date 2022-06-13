package dev.yuua.journeylib.qnortz.functions

import dev.yuua.journeylib.qnortz.functions.command.CommandManager
import dev.yuua.journeylib.qnortz.functions.command.event.UnifiedCommandInteractionEvent
import kotlin.reflect.KClass

enum class FunctionType(val manager: KClass<*>, val event: KClass<*>) {
    AUTOCOMPLETE(TODO(), TODO()),
    BUTTON(TODO(), TODO()),
    COMMAND(CommandManager::class, UnifiedCommandInteractionEvent::class),
    CONTEXTMENU(TODO(), TODO()),
    EVENT(TODO(), TODO()),
    MODAL(TODO(), TODO()),
    SELECTMENU(TODO(), TODO())
}
