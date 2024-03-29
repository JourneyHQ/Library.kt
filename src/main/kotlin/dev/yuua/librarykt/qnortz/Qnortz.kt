package dev.yuua.librarykt.qnortz

import dev.minn.jda.ktx.jdabuilder.default
import dev.yuua.librarykt.journal.Journal
import dev.yuua.librarykt.journal.Journal.Symbols.*
import dev.yuua.librarykt.qnortz.filter.PackageFilter
import dev.yuua.librarykt.qnortz.filter.PackageFilterRouter
import dev.yuua.librarykt.qnortz.functions.command.CommandManager
import dev.yuua.librarykt.qnortz.functions.command.event.UnifiedCommandInteractionEvent
import dev.yuua.librarykt.qnortz.functions.command.router.SlashCommandReactor
import dev.yuua.librarykt.qnortz.functions.command.router.TextCommandReactor
import dev.yuua.librarykt.qnortz.functions.event.EventManager
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.requests.GatewayIntent

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

    // Builders
    fun build(jdaBuilder: JDABuilder.() -> Unit = {}): Qnortz {
        if (!QnortzInstances.exists(name)) {
            QnortzInstances[name] = this
        } else {
            throw UnsupportedOperationException("$name already exists.")
        }

        journal[Task]("Initializing $name. Please wait...")

        jda = default(
            token = token,
            enableCoroutines = true,
            intents = intents,
            builder = jdaBuilder
        ).awaitReady()

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
