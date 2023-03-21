package dev.yuua.journeylib.qnortz

import dev.minn.jda.ktx.jdabuilder.default
import dev.minn.jda.ktx.jdabuilder.injectKTX
import dev.schlaubi.lavakord.LavaKord
import dev.schlaubi.lavakord.LavaKordOptions
import dev.schlaubi.lavakord.MutableLavaKordOptions
import dev.schlaubi.lavakord.jda.buildWithLavakord
import dev.yuua.journeylib.journal.Journal
import dev.yuua.journeylib.journal.Journal.Symbols.*
import dev.yuua.journeylib.qnortz.filter.PackageFilter
import dev.yuua.journeylib.qnortz.filter.PackageFilterRouter
import dev.yuua.journeylib.qnortz.functions.command.CommandManager
import dev.yuua.journeylib.qnortz.functions.command.event.UnifiedCommandInteractionEvent
import dev.yuua.journeylib.qnortz.functions.command.router.SlashCommandReactor
import dev.yuua.journeylib.qnortz.functions.command.router.TextCommandReactor
import dev.yuua.journeylib.qnortz.functions.event.EventManager
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.requests.GatewayIntent
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration

/**
 * @param name The name of the Qnortz instance.
 * @param token The token for Discord bot.
 * @param intents Discord [GatewayIntent]s to enable.
 */
class Qnortz(
    val name: String,
    val token: String,
    vararg intents: GatewayIntent,
    qnortzBuilder: Qnortz.() -> Unit
) {
    val journal: Journal

    lateinit var jda: JDA
    private val intents = mutableListOf(*intents)

    // Function Managers
    private lateinit var commandManager: CommandManager
    fun enableCommands(
        functionPackage: String,
        packageFilter: PackageFilter<UnifiedCommandInteractionEvent> = PackageFilter()
    ): CommandManager {
        commandManager = CommandManager(this, functionPackage, PackageFilterRouter(functionPackage, packageFilter))
        return commandManager
    }

    private lateinit var eventManager: EventManager
    fun enableEvents(
        functionPackage: String
    ): EventManager {
        eventManager = EventManager(this, functionPackage)
        return eventManager
    }

    // Development Environment
    var isDev = false
    lateinit var devPrefix: String
    private val devGuildIdList = mutableListOf<String>()
    val devGuildList = mutableListOf<Guild>()

    fun enableDevEnv(devPrefix: String, vararg devGuildIdList: String) {
        isDev = true
        this.devPrefix = devPrefix
        this.devGuildIdList.addAll(devGuildIdList)
    }

    lateinit var lavakord: LavaKord

    private var enableLavakord = false
    private var lavaExecutor: CoroutineContext? = null
    private lateinit var lavaOptions: MutableLavaKordOptions
    private lateinit var lavaBuilder: LavaKordOptions.() -> Unit

    fun enableLavakord(
        executor: CoroutineContext? = null,
        options: MutableLavaKordOptions = MutableLavaKordOptions(),
        builder: LavaKordOptions.() -> Unit = {}
    ) {
        lavaExecutor = executor
        lavaOptions = options
        lavaBuilder = builder
        enableLavakord = true
    }

    // Builders
    suspend fun build(jdaBuilder: JDABuilder.() -> Unit = {}): Qnortz {
        if (!QnortzInstances.exists(name)) {
            QnortzInstances[name] = this
        } else {
            throw UnsupportedOperationException("$name already exists.")
        }

        journal[Task]("Initializing $name. Please wait...")

        jda = if (enableLavakord) {
            val l = JDABuilder.createDefault(token, intents)
                .apply(jdaBuilder)
                .apply {
                    injectKTX(timeout = Duration.INFINITE)
                }.buildWithLavakord()
            lavakord = l.lavakord
            l.jda.awaitReady()
        } else {
            default(
                token = token,
                enableCoroutines = true,
                intents = intents,
                builder = jdaBuilder
            ).awaitReady()
        }

        for (id in devGuildIdList) {
            val devGuild = jda.getGuildById(id)
            if (devGuild == null) {
                journal[Failure]("Cannot resolve a development guild id: $id. Skipping...")
            } else {
                devGuildList.add(devGuild)
            }
        }

        journal[Success](
            "Following guilds added as development guild :",
            *devGuildList.map { "${it.name}(${it.id})" }.toTypedArray()
        )

        if (::commandManager.isInitialized) {
            commandManager.init()
            TextCommandReactor(commandManager).script(jda)
            SlashCommandReactor(commandManager).script(jda)

            journal[Success]("Command manager initialized.")
        }

        if (::eventManager.isInitialized) {
            eventManager.init()

            journal[Success]("Event manager initialized.")
        }

        return this
    }

    fun terminate(immediate: Boolean) {
        if (immediate) jda.shutdownNow() else jda.shutdown()
        QnortzInstances.remove(name)
        journal[Info]("$name have been successfully terminated.")
    }

    init {
        this.apply(qnortzBuilder)
        journal = Journal("Qnortz/$name")
    }
}
