package dev.yuua.journeylib.discord.framework

import dev.minn.jda.ktx.default
import dev.yuua.journeylib.discord.framework.command.builder.structure.FrCmdSubstrate
import dev.yuua.journeylib.discord.framework.command.router.FrCmdRecorder
import dev.yuua.journeylib.discord.framework.command.router.FrSlashCmdRouter
import dev.yuua.journeylib.discord.framework.command.router.FrTextCmdRouter
import dev.yuua.journeylib.universal.LibFlow
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.requests.GatewayIntent
import org.reflections.Reflections
import java.lang.reflect.Constructor
import kotlin.time.Duration

object FrameworkManager {
    private val libFlow: LibFlow = LibFlow(this.javaClass.simpleName)

    lateinit var jda: JDA
    var buildFinished = false

    private lateinit var token: String
    private var enableCoroutines = false
    private var timeout = Duration.INFINITE
    private lateinit var intent: GatewayIntent
    private lateinit var intents: Array<out GatewayIntent>
    private lateinit var builder: JDABuilder.() -> Unit

    lateinit var commandPackage: String
    lateinit var eventPackage: String
    val events = mutableListOf<ListenerAdapter>()
    val commands = mutableListOf<FrCmdSubstrate>()

    fun setJDABuilder(builder: JDABuilder.() -> Unit = {}): FrameworkManager {
        this.builder = { builder() }
        return this
    }

    fun create(
        token: String,
        enableCoroutines: Boolean = true,
        timeout: Duration = Duration.INFINITE,
        intent: GatewayIntent,
        vararg intents: GatewayIntent,
        builder: JDABuilder.() -> Unit = {}
    ): FrameworkManager {
        this.token = token
        this.enableCoroutines = enableCoroutines
        this.timeout = timeout
        this.intent = intent
        this.intents = intents
        this.builder = builder

        return this
    }

    fun create(
        token: String,
        enableCoroutines: Boolean = true,
        intent: GatewayIntent,
        vararg intents: GatewayIntent
    ): FrameworkManager {
        this.token = token
        this.enableCoroutines = enableCoroutines
        this.timeout = Duration.INFINITE
        this.intent = intent
        this.intents = intents

        return this
    }

    fun initEventManager(eventPackage: String): FrameworkManager {
        val eventClasses = Reflections(eventPackage).getSubTypesOf(ListenerAdapter::class.java)

        if (eventClasses.isEmpty())
            throw UnsupportedOperationException("Package:$eventPackage が空か、見つかりませんでした。")
        this.eventPackage = eventPackage

        val events = eventClasses.filter {
            it.enclosingClass == null && !it.name.contains("$")
        }.map {
            libFlow.success("Event:${it.simpleName} を登録しました！")
            (it.getConstructor() as Constructor<*>).newInstance() as ListenerAdapter
        }

        this.events.addAll(events)

        return this
    }

    fun initCmdManager(commandPackage: String): FrameworkManager {
        val commandClasses = Reflections(commandPackage).getSubTypesOf(FrCmdSubstrate::class.java)

        if (commandClasses.isEmpty())
            throw UnsupportedOperationException("Package:$commandPackage が空か、見つかりませんでした。")
        this.commandPackage = commandPackage

        val commands = commandClasses.filter {
            it.enclosingClass == null && !it.name.contains("$")
        }.map {
            libFlow.success("Command:${it.simpleName} を登録しました！")
            (it.getConstructor() as Constructor<*>).newInstance() as FrCmdSubstrate
        }

        this.commands.addAll(commands)

        return this
    }

    fun getCommandClasses(): MutableSet<Class<out FrCmdSubstrate>> {
        if (!buildFinished)
            throw IllegalStateException("Buildが完了していないため、Classを取得できませんでした。")
        return Reflections(commandPackage).getSubTypesOf(FrCmdSubstrate::class.java)
    }

    fun build(): FrameworkManager {
        jda = default(token, enableCoroutines, timeout, intent, *intents) {
            builder()
        }.awaitReady()
        buildFinished = true
        FrCmdRecorder(jda)
        FrSlashCmdRouter(jda)
        FrTextCmdRouter(jda)
        return this
    }
}
