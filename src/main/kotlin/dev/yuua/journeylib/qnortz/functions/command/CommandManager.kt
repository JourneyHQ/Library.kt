package dev.yuua.journeylib.qnortz.functions.command

import dev.minn.jda.ktx.interactions.commands.updateCommands
import dev.yuua.journeylib.journal.Journal.Symbols.*
import dev.yuua.journeylib.qnortz.Qnortz
import dev.yuua.journeylib.qnortz.filter.PackageFilterRouter
import dev.yuua.journeylib.qnortz.functions.FunctionRouter
import dev.yuua.journeylib.qnortz.functions.ManagerStruct
import dev.yuua.journeylib.qnortz.functions.command.builder.CommandObject
import dev.yuua.journeylib.qnortz.functions.command.builder.function.CommandFunction
import dev.yuua.journeylib.qnortz.functions.command.event.UnifiedCommandInteractionEvent
import dev.yuua.journeylib.qnortz.functions.command.router.CommandRoute
import dev.yuua.journeylib.qnortz.functions.functionClasses
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

typealias TaskCoroutine = CoroutineScope.() -> Unit

/**
 * Manages command classes.
 *
 * @param qnortz Qnortz instance.
 * @param functionPackage The package on which command classes placed.
 * @param packageFilterRouter [PackageFilterRouter] for per-package limitation.
 */
class CommandManager(
    override val qnortz: Qnortz,
    override val functionPackage: String,
    override val packageFilterRouter: PackageFilterRouter<UnifiedCommandInteractionEvent>
) : ManagerStruct<CommandStruct, UnifiedCommandInteractionEvent> {

    override val name = "Command"

    override val instances: MutableList<CommandStruct> by lazy {
        functionClasses()
    }

    lateinit var router: CommandRouter

    val routePackageMap = hashMapOf<CommandRoute, String>()

    fun findRoutePackage(route: CommandRoute) = routePackageMap.filterKeys {
        it.command == route.command
                && it.subcommandGroup == route.subcommandGroup
                && it.subcommand == route.subcommand
    }.values.first()

    /**
     * Initializes [CommandManager] and registers commands to Discord.
     */
    override fun init() {
        val jda = qnortz.jda

        val publicCommands = mutableListOf<CommandObject>()
        val guildCommands = hashMapOf<String, MutableList<CommandObject>>()
        // initialize guilds for guild commands
        jda.guilds.forEach {
            guildCommands[it.id] = mutableListOf()
        }

        instances.forEach { instance ->
            val packageName = instance::class.java.packageName
            val filters = packageFilterRouter.findAll(packageName)
            val commandObject = instance.command
            commandObject.routes.map { it.key }.forEach {
                routePackageMap[it] = packageName
            }

            if (filters.all { it.guildIds.isEmpty() }) {
                publicCommands.add(commandObject)
            } else {
                val guildIdLists = filters.map { it.guildIds }.filter { it.isNotEmpty() }
                val allGuildIds = guildIdLists.flatten().distinct()

                var commonGuildIds = guildIdLists.first().toSet()
                for ((index, guildIds) in guildIdLists.withIndex()) {
                    if (index == 0) continue
                    commonGuildIds = commonGuildIds.intersect(guildIds.toSet())
                }

                for (guildId in commonGuildIds)
                    guildCommands[guildId]!!.add(commandObject) // !! -> already initialized
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
                        "Following public commands updated successfully:",
                        *it.map { command -> "${command.name}:${command.description}" }.toTypedArray()
                    )
                }, {
                    journal[Failure]("Updating public commands failed because of following reasons:")
                    throw it
                })
            }

            val devPublicCommandUpdateTask: TaskCoroutine = {
                val devPublicCommands = publicCommands.map {
                    // apply dev prefix
                    it.commandData.apply { name = qnortz.devPrefix + name }
                }

                for (guild in qnortz.devGuildList) {
                    guild.updateCommands {
                        addCommands(devPublicCommands)
                    }.queue({
                        journal[Success](
                            "Following public commands updated successfully (dev/${guild.name}):",
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

                    if (privateCommands.isEmpty()) continue

                    guild.updateCommands().addCommands(
                        privateCommands.map {
                            it.commandData.apply {
                                if (qnortz.isDevEnv) name = qnortz.devPrefix + name
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

            jda.guilds.filter { guildCommands[it.id]!!.isEmpty() }.forEach { it.updateCommands().complete() }

            launch(block = if (!qnortz.isDevEnv) publicCommandUpdateTask else devPublicCommandUpdateTask)
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
