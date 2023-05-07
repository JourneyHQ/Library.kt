package dev.yuua.librarykt.qnortz.functions.event

import dev.yuua.librarykt.qnortz.Qnortz
import dev.yuua.librarykt.qnortz.functions.ManagerStruct
import dev.yuua.librarykt.qnortz.functions.functionClasses
import net.dv8tion.jda.api.events.GenericEvent

/**
 * Manages event classes.
 */
class EventManager(
    override val qnortz: Qnortz,
    override val functionPackage: String
) : ManagerStruct<EventStruct, GenericEvent> {

    override val name = "Event"

    override val instances: MutableList<EventStruct> by lazy {
        functionClasses()
    }

    override fun init() {
        for (instance in instances)
            instance.script(qnortz.jda)
    }
}
