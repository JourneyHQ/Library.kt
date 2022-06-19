package dev.yuua.journeylib.qnortz.functions.command

import dev.yuua.journeylib.journal.Journal.Symbols.*
import dev.yuua.journeylib.qnortz.Qnortz
import dev.yuua.journeylib.qnortz.functions.FunctionRouter
import dev.yuua.journeylib.qnortz.functions.ManagerStruct
import dev.yuua.journeylib.qnortz.functions.command.builder.CommandObject
import dev.yuua.journeylib.qnortz.functions.command.builder.function.CommandFunction
import dev.yuua.journeylib.qnortz.functions.command.event.UnifiedCommandInteractionEvent
import dev.yuua.journeylib.qnortz.functions.command.router.CommandRoute
import dev.yuua.journeylib.qnortz.functions.functionClasses
import dev.yuua.journeylib.qnortz.limit.LimitRouter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

typealias TaskCoroutine = CoroutineScope.() -> Unit

/**
 * Manages command classes.
 *
 * @param qnortz Qnortz instance.
 * @param functionPackage The package on which command classes placed.
 * @param limitRouter [LimitRouter] for per-package limitation.
 */
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

    /**
     * Initializes [CommandManager] and registers commands to Discord.
     */
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

        val journal = qnortz.journal

        val router = CommandRouter()

        for (publicCommand in publicCommands)
            publicCommand.routes.forEach { (commandRoute, commandFunction) ->
                if (qnortz.isDevEnv)
                    commandRoute.command = qnortz.devPrefix + commandRoute.command // apply dev prefix
                router[commandRoute] = commandFunction
            }

        for (guildCommand in guildCommands)
            guildCommand.value.forEach {
                it.routes.forEach { (commandRoute, commandFunction) ->
                    if (qnortz.isDevEnv)
                        commandRoute.command = qnortz.devPrefix + commandRoute.command // apply dev prefix
                    router[commandRoute] = commandFunction
                }
            }

        runBlocking {
            // update public commands
            val publicCommandUpdateTask: TaskCoroutine = {
                jda.updateCommands().addCommands(
                    publicCommands.map { it.commandData }
                ).queue({
                    journal[Success](
                        "Following public commands updated successfully :",
                        *it.map { command -> "${command.name}:${command.description}" }.toTypedArray()
                    )
                }, {
                    journal[Failure]("Updating public commands failed because of following reasons:")
                    throw it
                })
            }

            val devPublicCommandUpdateTask: TaskCoroutine = {
                for (guild in qnortz.devGuildList) {
                    guild.updateCommands().addCommands(
                        publicCommands.map {
                            // apply dev prefix
                            it.commandData.apply { name = qnortz.devPrefix + name }
                        }
                    ).queue({
                        journal[Success](
                            "Following public commands updated successfully (dev) :",
                            *it.map { command -> "${command.name}(${command.description})" }.toTypedArray()
                        )
                    }, {
                        journal[Failure]("Updating public commands failed because of following reasons:")
                        throw it
                    })
                }
            }

            //update private commands
            val privateCommandsUpdateTask: TaskCoroutine = {
                for ((guildId, privateCommands) in guildCommands) {
                    // dev-env && this guild is not a dev guild.
                    if (qnortz.isDevEnv && !qnortz.devGuildList.map { it.id }.contains(guildId)) {
                        journal[Info]("Guild($guildId) is not a dev guild. Skipping private command update...")
                        continue
                    }

                    val guild = jda.getGuildById(guildId)
                    if (guild == null) {
                        journal[Failure]("Cannot resolve Guild($guildId). Skipping private command update...")
                        continue
                    }

                    //clear private commands
                    if (privateCommands.isEmpty()) {
                        guild.updateCommands().queue()
                        continue
                    }

                    guild.updateCommands().addCommands(
                        privateCommands.map {
                            it.commandData.apply {
                                if (qnortz.isDevEnv)
                                    name = qnortz.devPrefix + name
                            }
                        }
                    ).queue({
                        journal[Success](
                            "Following private commands for ${guild.name}($guildId) updated successfully ${if (qnortz.isDevEnv) "(dev) " else ""}:",
                            *it.map { command -> "${command.name}(${command.description})" }.toTypedArray()
                        )
                    }, {
                        journal[Failure]("Updating private commands failed because of following reasons:")
                        throw it
                    })
                }
            }

            if (!qnortz.isDevEnv)
                launch(block = publicCommandUpdateTask)
            else
                launch(block = devPublicCommandUpdateTask)

            launch(block = privateCommandsUpdateTask)
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
