package dev.yuua.journeylib.discord.framework.command.router

import dev.minn.jda.ktx.interactions.updateCommands
import dev.yuua.journeylib.discord.framework.FrameworkManager
import dev.yuua.journeylib.discord.framework.command.builder.structure.FrCmd
import dev.yuua.journeylib.discord.framework.command.builder.structure.FrCmdStruct
import dev.yuua.journeylib.discord.framework.command.scope.FrCmdScopeDB
import dev.yuua.journeylib.universal.LibFlow
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData

class FrCmdRecorder(jda: JDA) {
    private val libFlow: LibFlow = LibFlow(this.javaClass.simpleName)

    init {
        val commandList = hashMapOf<Guild, MutableList<FrCmd>>()
        for (guild in jda.guilds) commandList[guild] = ArrayList()

        libFlow.task("Recording commands...")

        val allCmdNames = mutableListOf<String?>()

        for (commandClass in FrameworkManager.getCommandClasses()) {
            val commandInstance: FrCmdStruct = commandClass.getConstructor().newInstance()
            val frCommand = commandInstance.cmd().cmd
            val jdaCommandData: SlashCommandData = commandInstance.cmd().cmd.jdaCmdData

            // コマンドを登録するGuild (Scopeが指定されていない場合はEmpty)
            val guilds = mutableListOf<String>()
            FrCmdScopeDB.record.filter {
                it.key.containsAll(frCommand.routes)
            }.values.forEach {
                guilds.addAll(it.guilds)
            }

            val targetGuilds =
                if (guilds.isEmpty()) jda.guilds
                else guilds.map { jda.getGuildById(it) }

            for (guild in targetGuilds) commandList[guild]?.add(frCommand)

            allCmdNames.addAll(frCommand.routes[0].cmd.allNames())
            FrCmdRouteDB.record.addAll(frCommand.routes)

            libFlow.success("Command:${jdaCommandData.name}(${jdaCommandData.description}) added to queue.")
        }

        for ((index, name) in allCmdNames.withIndex()) {
            for ((index2, name2) in allCmdNames.withIndex()) {
                if (index != index2 && name == name2)
                    throw IllegalStateException("Names cannot be duplicated! ($name)")
            }
        }

        commandList.forEach { (guild, commands) ->
            guild.updateCommands {
                addCommands(commands.map { it.jdaCmdData })
            }.queue(
                { libFlow.success("Commands successfully added to Guild:${guild.name} (${commands.size})") },
                { libFlow.failure("Failed to add commands to Guild:${guild.name} (${it.message})") }
            )
        }
    }
}
