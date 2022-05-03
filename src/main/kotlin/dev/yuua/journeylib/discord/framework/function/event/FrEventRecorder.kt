package dev.yuua.journeylib.discord.framework.function.event

import dev.yuua.journeylib.discord.framework.Framework
import dev.yuua.journeylib.universal.LibFlow

class FrEventRecorder(framework: Framework, classes: List<FrEventStruct>) {
    private val libFlow: LibFlow = LibFlow(this.javaClass.simpleName)

    init {
        for (frEventStruct in classes) {
            frEventStruct.listener(framework.jda)
            libFlow.success("Event:${frEventStruct::class.simpleName} successfully added.")
        }
    }
}
