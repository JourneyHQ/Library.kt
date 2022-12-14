package dev.yuua.journeylib.qnortz.functions.event

import dev.yuua.journeylib.qnortz.Qnortz
import dev.yuua.journeylib.qnortz.filter.PackageFilterRouter
import dev.yuua.journeylib.qnortz.functions.ManagerStruct
import dev.yuua.journeylib.qnortz.functions.functionClasses
import net.dv8tion.jda.api.events.GenericEvent

/**
 * Manages event classes.
 */
class EventManager(
    override val qnortz: Qnortz,
    override val functionPackage: String,
    override val packageFilterRouter: PackageFilterRouter<GenericEvent>
) : ManagerStruct<EventStruct, GenericEvent> {

    override val name = "Event"

    override val instances: MutableList<EventStruct> by lazy {
        functionClasses()
    }

    override fun init() {
        val jda = qnortz.jda

        for (instance in instances)
            instance.script(jda)
    }
}