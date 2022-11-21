package dev.yuua.journeylib.qnortz

import dev.minn.jda.ktx.jdabuilder.default
import dev.yuua.journeylib.journal.Journal
import dev.yuua.journeylib.journal.Journal.Symbols.*
import dev.yuua.journeylib.qnortz.functions.command.CommandManager
import dev.yuua.journeylib.qnortz.functions.command.event.UnifiedCommandInteractionEvent
import dev.yuua.journeylib.qnortz.functions.command.router.SlashCommandReactor
import dev.yuua.journeylib.qnortz.functions.command.router.TextCommandReactor
import dev.yuua.journeylib.qnortz.functions.event.EventManager
import dev.yuua.journeylib.qnortz.functions.event.EventStruct
import dev.yuua.journeylib.qnortz.limit.Limit
import dev.yuua.journeylib.qnortz.limit.LimitRouter
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.MemberCachePolicy
import net.dv8tion.jda.api.utils.cache.CacheFlag

class Qnortz {
    val journal: Journal

    lateinit var jda: JDA
    lateinit var name: String
    lateinit var token: String
    val intents = mutableListOf<GatewayIntent>()

    constructor(name: String, token: String, vararg intents: GatewayIntent) {
        this.name = name
        this.token = token
        this.intents.addAll(intents)
        journal = Journal(name)

        journal[Info]("Initializing $name...")
    }

    constructor(script: Qnortz.() -> Unit) {
        this.apply(script)
        fun required(name: String) = IllegalArgumentException("$name is required.")

        if (!::name.isInitialized) throw required("Name")
        if (!::token.isInitialized) throw required("Token")

        journal = Journal(name)

        journal[Info]("Initializing $name...")
    }

    fun intents(vararg intents: GatewayIntent) {
        this.intents.addAll(intents)
    }

    // Function Managers
    private lateinit var commandManager: CommandManager
    fun initCommands(
        functionPackage: String,
        vararg limits: Pair<String, Limit<UnifiedCommandInteractionEvent>>
    ): CommandManager {
        commandManager = CommandManager(this, functionPackage, LimitRouter(functionPackage, limits.asList()))
        return commandManager
    }

    private lateinit var eventManager: EventManager
    fun initEvents(
        functionPackage: String,
        vararg limits: Pair<String, Limit<GenericEvent>>,
    ): EventManager {
        eventManager = EventManager(this, functionPackage, LimitRouter(functionPackage, limits.asList()))
        return eventManager
    }

    fun EventManager.addEvents(vararg events: EventStruct) {
        this.add(*events)
    }

    var isDevEnv = false
    lateinit var devPrefix: String
    private val devGuildIdList = mutableListOf<String>()
    val devGuildList = mutableListOf<Guild>()

    fun devEnv(devPrefix: String, vararg devGuildIdList: String) {
        isDevEnv = true
        this.devPrefix = devPrefix
        this.devGuildIdList.addAll(devGuildIdList)
    }

    fun build() {
        build { }
    }

    fun build(script: Qnortz.() -> Unit) {
        jda = default(
            token = token,
            enableCoroutines = true,
            intents = intents
        ) {
            setMemberCachePolicy(MemberCachePolicy.ALL)
            enableCache(CacheFlag.ONLINE_STATUS)
        }.awaitReady()

        for (id in devGuildIdList) {
            val devGuild = jda.getGuildById(id)
            if (devGuild == null) {
                journal[Failure]("Cannot resolve guild ($id)")
            } else {
                devGuildList.add(devGuild)
            }
        }
        journal[Success](
            "Following guilds added as dev guild :",
            *devGuildList.map { "${it.name}(${it.id})" }.toTypedArray()
        )

        QnortzInstances[name] = this

        if (::commandManager.isInitialized && ::eventManager.isInitialized) {
            commandManager.init()
            eventManager.init()
        } else if (::commandManager.isInitialized && !::eventManager.isInitialized) {
            commandManager.init()
            TextCommandReactor(commandManager).script(jda)
            SlashCommandReactor(commandManager).script(jda)
        }

        script(this)
    }
}
