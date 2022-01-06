package dev.yuua.journeylib.discord.framework

import dev.yuua.journeylib.discord.framework.command.CmdReceiver
import dev.yuua.journeylib.discord.framework.command.CmdRecorder
import dev.yuua.journeylib.discord.framework.event.EventRecorder
import dev.yuua.journeylib.discord.framework.scope.CmdScopeManager
import dev.yuua.journeylib.universal.LibClassFinder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder

object FrameworkManager {
    lateinit var commandPackage: String
    lateinit var eventPackage: String

    lateinit var jdaBuilder: JDABuilder

    fun setJDABuilder(jdaBuilder: JDABuilder): FrameworkManager {
        this.jdaBuilder = jdaBuilder
        return this
    }

    fun initEventManager(eventPackage: String): FrameworkManager {
        if (LibClassFinder().findClasses(eventPackage).isEmpty())
            throw UnsupportedOperationException("Package:$eventPackage が空か、見つかりませんでした。")
        this.eventPackage = eventPackage

        EventRecorder(jdaBuilder)
        return this
    }

    fun initCmdManager(commandPackage: String): FrameworkManager {
        if (LibClassFinder().findClasses(commandPackage).isEmpty())
            throw UnsupportedOperationException("Package:$commandPackage が空か、見つかりませんでした。")
        this.commandPackage = commandPackage

        CmdScopeManager.init()
        jdaBuilder.addEventListeners(CmdReceiver())
        return this
    }

    fun build(): JDA {
        return jdaBuilder.build().awaitReady().also { jda -> CmdRecorder(jda) }
    }
}