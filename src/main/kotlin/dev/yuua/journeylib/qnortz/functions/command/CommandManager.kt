package dev.yuua.journeylib.qnortz.functions.command

import dev.yuua.journeylib.qnortz.Qnortz
import dev.yuua.journeylib.qnortz.functions.FunctionRouter
import dev.yuua.journeylib.qnortz.functions.ManagerStruct
import dev.yuua.journeylib.qnortz.functions.command.builder.CommandObject
import dev.yuua.journeylib.qnortz.functions.command.builder.function.CommandFunction
import dev.yuua.journeylib.qnortz.functions.command.event.UnifiedCommandInteractionEvent
import dev.yuua.journeylib.qnortz.functions.command.router.CommandRoute
import dev.yuua.journeylib.qnortz.functions.functionClasses
import dev.yuua.journeylib.qnortz.limit.LimitRouter
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class CommandManager(
    override val qnortz: Qnortz,
    override val functionPackage: String,
    override val limitRouter: LimitRouter<UnifiedCommandInteractionEvent>
) : ManagerStruct<CommandStruct, UnifiedCommandInteractionEvent> {

    override val name = "Command"

    override val instances: MutableList<CommandStruct> by lazy {
        functionClasses()
    }

    lateinit var router: CommandRouter

    override fun init() {
        val jda = qnortz.jda

        val publicCommands = mutableListOf<CommandObject>()
        val guildCommands = hashMapOf<String, MutableList<CommandObject>>()
        //init guildCommands
        jda.guilds.forEach {
            guildCommands[it.id] = mutableListOf()
        }

        instances.forEach {
            val packageName = it::class.java.packageName
            val limit = limitRouter[packageName]
            val commandObject = it.command

            if (limit.guilds.isEmpty()) {
                publicCommands.add(commandObject)
            } else {
                for (guild in limit.guilds)
                    guildCommands[guild]!!.add(commandObject) // !! -> already initialized
            }
        }

        val libFlow = qnortz.libFlow

        libFlow.header("Public Commands Update")

        val router = CommandRouter()

        runBlocking {
            // update public commands
            val publicCommandUpdateTask = launch {
                jda.updateCommands().addCommands(
                    publicCommands.map {
                        router.routes.putAll(it.routes)
                        it.commandData
                    }
                ).queue({
                    libFlow.success("Following public commands updated successfully:")
                    it.forEach { command -> libFlow.success("${command.name}:${command.description}") }
                }, {
                    libFlow.failure("Updating public commands failed because of following reasons:")
                    throw it
                })
            }

            //update private commands
            val privateCommandsUpdateTask = launch {
                guildCommands.forEach { (guildId, privateCommands) ->
                    val guild = jda.getGuildById(guildId)!!
                    guild.updateCommands().addCommands(
                        privateCommands.map {
                            router.routes.putAll(it.routes)
                            it.commandData
                        }
                    ).queue({
                        libFlow.success("Following private commands updated successfully:")
                        it.forEach { command -> libFlow.success("${command.name}:${command.description} for ${guild.name}:${guild.id}") }
                    }, {
                        libFlow.failure("Updating private commands failed because of following reasons:")
                        throw it
                    })
                }
            }

            publicCommandUpdateTask.join()
            privateCommandsUpdateTask.join()
        }

        this.router = router
    }
}

class CommandRouter : FunctionRouter<CommandRoute, CommandFunction, CommandStructureType> {
    override val routes = hashMapOf<CommandRoute, CommandFunction>()

    private val routeNotFoundError = NoSuchElementException("No such command route found!")

    override fun get(route: CommandRoute): CommandFunction {
        val routeEntry = routes.filterKeys {
            it.command == route.command
                    && it.subcommandGroup == route.subcommandGroup
                    && it.subcommand == route.subcommand
        }.entries.firstOrNull() ?: throw routeNotFoundError

        return routeEntry.value
    }

    override fun inferType(context: String): CommandStructureType {
        val route = routes.filterKeys {
            it.command == context
        }.entries.firstOrNull()?.key?.type ?: throw routeNotFoundError

        return route
    }
}
