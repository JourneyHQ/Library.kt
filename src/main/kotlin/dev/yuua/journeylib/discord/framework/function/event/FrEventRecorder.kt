package dev.yuua.journeylib.discord.framework.function.event

import dev.yuua.journeylib.discord.framework.FrameworkManager
import dev.yuua.journeylib.universal.LibFlow

class FrEventRecorder(frameworkManager: FrameworkManager, classes: List<FrEventStruct>) {
    private val libFlow: LibFlow = LibFlow(this.javaClass.simpleName)

    init {
        for (frEventStruct in classes) {
            frEventStruct.listener(frameworkManager.jda)
            libFlow.success("Event:${frEventStruct::class.simpleName} successfully added.")
        }
    }
}
