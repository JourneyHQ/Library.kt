package dev.yuua.journeylib.discord.framework

import dev.yuua.journeylib.discord.framework.command.CmdRecorder
import dev.yuua.journeylib.discord.framework.command.builder.structure.FrCmdSubstrate
import dev.yuua.journeylib.discord.framework.command.router.FrCmdRouter
import dev.yuua.journeylib.discord.framework.event.EventRecorder
import dev.yuua.journeylib.discord.framework.command.scope.FrCmdScopeDB
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.reflections.Reflections

object FrameworkManager {
    lateinit var commandPackage: String
    lateinit var eventPackage: String
    lateinit var jdaBuilder: JDABuilder
    var buildFinished = false

    fun setJDABuilder(jdaBuilder: JDABuilder): FrameworkManager {
        this.jdaBuilder = jdaBuilder
        return this
    }

    fun initEventManager(eventPackage: String): FrameworkManager {
        if (Reflections(eventPackage).getSubTypesOf(ListenerAdapter::class.java).isEmpty())
            throw UnsupportedOperationException("Package:$eventPackage が空か、見つかりませんでした。")
        this.eventPackage = eventPackage

        EventRecorder(jdaBuilder)
        return this
    }

    fun getEventClasses(): MutableSet<Class<out ListenerAdapter>> {
        if (!buildFinished)
            throw IllegalStateException("Buildが完了していないため、Classを取得できませんでした。")
        return Reflections(eventPackage).getSubTypesOf(ListenerAdapter::class.java)
    }

    fun initCmdManager(commandPackage: String): FrameworkManager {
        if (Reflections(commandPackage).getSubTypesOf(FrCmdSubstrate::class.java).isEmpty())
            throw UnsupportedOperationException("Package:$commandPackage が空か、見つかりませんでした。")
        this.commandPackage = commandPackage

        jdaBuilder.addEventListeners(FrCmdRouter())
        return this
    }

    fun getCommandClasses(): MutableSet<Class<out FrCmdSubstrate>> {
        if (!buildFinished)
            throw IllegalStateException("Buildが完了していないため、Classを取得できませんでした。")
        return Reflections(commandPackage).getSubTypesOf(FrCmdSubstrate::class.java)
    }

    fun build(): JDA {
        return jdaBuilder.build().awaitReady()
            .also {
                buildFinished = true
                CmdRecorder(it)
            }
    }
}
