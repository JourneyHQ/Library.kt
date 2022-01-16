package dev.yuua.journeylib.discord.framework.command

import dev.yuua.journeylib.discord.framework.FrameworkManager
import dev.yuua.journeylib.discord.framework.scope.CmdScopeManager
import dev.yuua.journeylib.universal.LibClassFinder
import dev.yuua.journeylib.universal.LibFlow
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.interactions.commands.build.CommandData

class CmdRecorder(jda: JDA) {
    private val libFlow: LibFlow = LibFlow(this.javaClass.simpleName)

    init {
        val commandList: HashMap<Guild, ArrayList<CommandData>> = object : HashMap<Guild, ArrayList<CommandData>>() {
            init {
                for (guild in jda.guilds) put(guild, ArrayList())
            }
        }
        libFlow.task("コマンドを登録中...")

        for (commandClass in LibClassFinder().findClasses(FrameworkManager.commandPackage)) {
            if (commandClass.enclosingClass != null
                || commandClass.name.contains("$")
                || commandClass.simpleName.equals("Scope")
            ) continue

            val command: CmdSubstrate = (commandClass.getConstructor().newInstance() as CmdSubstrate)
            val commandData: CommandData = command.data()

            val guilds = CmdScopeManager.record[command.javaClass.packageName]?.guilds ?: emptyArray()
            for (guild in (
                    if (guilds.isEmpty())
                        jda.guilds
                    else guilds.map { guildid ->
                        jda.getGuildById(
                            guildid
                        )
                    }))
                commandList[guild]?.add(commandData)

            libFlow.success("Command:${commandData.name}(${commandData.description}) をキューに挿入しました！")
        }

        commandList.forEach { (guild, commands) ->
            guild.updateCommands().addCommands(commands).queue(
                {
                    libFlow.success("Guild:${guild.name} への登録に成功しました。(${commands.size})")
                },
                { t ->
                    libFlow.failure("Guild:${guild.name} への登録に失敗しました。(${t.message})")
                })
        }
    }
}