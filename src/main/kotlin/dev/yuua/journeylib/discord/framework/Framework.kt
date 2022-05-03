package dev.yuua.journeylib.discord.framework

import dev.minn.jda.ktx.default
import dev.yuua.journeylib.discord.framework.Framework.ManagerType.*
import dev.yuua.journeylib.discord.framework.function.button.FrButtonStruct
import dev.yuua.journeylib.discord.framework.function.command.builder.structure.FrCommandStruct
import dev.yuua.journeylib.discord.framework.function.command.router.FrCommandRecorder
import dev.yuua.journeylib.discord.framework.function.command.router.FrSlashCmdRouter
import dev.yuua.journeylib.discord.framework.function.command.router.FrTextCmdRouter
import dev.yuua.journeylib.discord.framework.function.scope.FrCmdScopeDB
import dev.yuua.journeylib.discord.framework.function.contextmenu.FrContextMenuStruct
import dev.yuua.journeylib.discord.framework.function.event.FrEventRecorder
import dev.yuua.journeylib.discord.framework.function.event.FrEventStruct
import dev.yuua.journeylib.discord.framework.function.modal.FrModalStruct
import dev.yuua.journeylib.discord.framework.function.scope.FrScopeStruct
import dev.yuua.journeylib.discord.framework.function.selectmenu.FrSelectMenuStruct
import dev.yuua.journeylib.universal.LibFlow
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import org.reflections.Reflections
import java.lang.reflect.Constructor
import kotlin.time.Duration

class Framework {
    private val libFlow: LibFlow = LibFlow(this.javaClass.simpleName)

    lateinit var jda: JDA
    var buildFinished = false

    private lateinit var token: String
    private var enableCoroutines = false
    private var timeout = Duration.INFINITE
    private lateinit var intent: GatewayIntent
    private lateinit var intents: Array<out GatewayIntent>
    private lateinit var builder: JDABuilder.() -> Unit

    fun setJDABuilder(builder: JDABuilder.() -> Unit = {}): Framework {
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
    ): Framework {
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
    ): Framework {
        this.token = token
        this.enableCoroutines = enableCoroutines
        this.timeout = Duration.INFINITE
        this.intent = intent
        this.intents = intents

        return this
    }

    var commandInstances = mutableListOf<FrCommandStruct>()
    var eventInstances = mutableListOf<FrEventStruct>()
    var buttonInstances = mutableListOf<FrButtonStruct>()
    var selectMenuInstances = mutableListOf<FrSelectMenuStruct>()
    var contextMenuInstances = mutableListOf<FrContextMenuStruct>()
    var modalInstances = mutableListOf<FrModalStruct>()
    var scopeInstances = mutableListOf<FrScopeStruct>()

    private fun ManagerType.addInstance(function: Class<*>) {
        when (this) {
            Command -> commandInstances.add(function.newInstanceAs())
            Event -> eventInstances.add(function.newInstanceAs())
            Button -> buttonInstances.add(function.newInstanceAs())
            SelectMenu -> selectMenuInstances.add(function.newInstanceAs())
            ContextMenu -> contextMenuInstances.add(function.newInstanceAs())
            Modal -> modalInstances.add(function.newInstanceAs())
            Scope -> scopeInstances.add(function.newInstanceAs())
        }
    }

    enum class ManagerType(val struct: Class<*>) {
        Command(FrCommandStruct::class.java),
        Event(FrEventStruct::class.java),
        Button(FrButtonStruct::class.java),
        SelectMenu(FrSelectMenuStruct::class.java),
        ContextMenu(FrContextMenuStruct::class.java),
        Modal(FrModalStruct::class.java),
        Scope(FrScopeStruct::class.java)
    }

    private inline fun <reified T> Class<*>.newInstanceAs(): T {
        return (this.getConstructor() as Constructor<*>).newInstance() as T
    }

    /**
     * Initialize specific function manager.
     * @param manager [ManagerType] such as [ManagerType.Command], [ManagerType.Button]
     * @param targetPackage Path to package that function classes are located.
     */
    fun initManager(manager: ManagerType, targetPackage: String): Framework {
        val classes = Reflections(targetPackage).getSubTypesOf(manager.struct)

        if (classes.isEmpty())
            throw UnsupportedOperationException("Package:$targetPackage was empty or not found!")

        val functionClasses = classes.filter {
            it.enclosingClass == null && !it.name.contains("$")
        }.onEach {
            libFlow.success("${manager.name}:${it.simpleName} queued!")
        }

        functionClasses.forEach { manager.addInstance(it) }
        return this
    }

    fun build(): Framework {
        jda = default(token, enableCoroutines, timeout, intent, *intents) {
            builder()
        }.awaitReady()
        buildFinished = true

        if (commandInstances.isNotEmpty()) {
            FrCmdScopeDB(this)
            FrCommandRecorder(this)
            FrSlashCmdRouter(this)
            FrTextCmdRouter(this)
        }

        if (eventInstances.isNotEmpty())
            FrEventRecorder(this, eventInstances)

        libFlow.success("Successfully logged in as ${jda.selfUser.asTag}!")
        return this
    }
}
