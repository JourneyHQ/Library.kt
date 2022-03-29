package dev.yuua.journeylib.discord.framework.event

import dev.yuua.journeylib.discord.framework.FrameworkManager
import dev.yuua.journeylib.universal.LibFlow
import net.dv8tion.jda.api.JDA

class FrEventRecorder(val jda: JDA) {
    private val libFlow: LibFlow = LibFlow(this.javaClass.simpleName)

    init {
        for (frEventStruct in FrameworkManager.events) {
            frEventStruct.listener(jda)
            libFlow.success("Event:${frEventStruct::class.simpleName} successfully added.")
        }
    }
}
