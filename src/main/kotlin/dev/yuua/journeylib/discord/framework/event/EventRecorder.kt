package dev.yuua.journeylib.discord.framework.event

import dev.yuua.journeylib.discord.framework.FrameworkManager
import dev.yuua.journeylib.universal.LibFlow
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.lang.reflect.Constructor

class EventRecorder(jdaBuilder: JDABuilder) {
    private val libFlow: LibFlow = LibFlow(this.javaClass.simpleName)

    init {
        libFlow.task("イベントを登録中...")

        for (eventClass in FrameworkManager.getEventClasses()) {
            if (eventClass.enclosingClass != null || eventClass.name.contains("$")) continue

            val instance = (eventClass.getConstructor() as Constructor<*>).newInstance()

            if (instance !is ListenerAdapter) continue

            jdaBuilder.addEventListeners(instance)
            libFlow.success("Event:${eventClass.simpleName} を登録しました！")
        }
    }
}
