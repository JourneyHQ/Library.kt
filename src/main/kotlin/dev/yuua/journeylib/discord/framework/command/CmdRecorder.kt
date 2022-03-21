package dev.yuua.journeylib.discord.framework.command

import dev.yuua.journeylib.discord.framework.FrameworkManager
import dev.yuua.journeylib.discord.framework.command.builder.structure.FrCmd
import dev.yuua.journeylib.discord.framework.command.builder.structure.FrCmdSubstrate
import dev.yuua.journeylib.discord.framework.command.router.FrCmdRouteDB
import dev.yuua.journeylib.discord.framework.command.router.FrCmdRouter
import dev.yuua.journeylib.discord.framework.command.scope.FrCmdScopeDB
import dev.yuua.journeylib.universal.LibFlow
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData

class CmdRecorder(jda: JDA) {
    private val libFlow: LibFlow = LibFlow(this.javaClass.simpleName)

    init {
        val commandList = hashMapOf<Guild,MutableList<FrCmd>>()
        for (guild in jda.guilds) commandList[guild] = ArrayList()

        libFlow.task("コマンドを登録中...")

        for (commandClass in FrameworkManager.getCommandClasses()) {
            val commandInstance: FrCmdSubstrate = commandClass.getConstructor().newInstance()
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

            FrCmdRouteDB.record.addAll(frCommand.routes)

            libFlow.success("Command:${jdaCommandData.name}(${jdaCommandData.description}) をキューに挿入しました！")
        }

        commandList.forEach { (guild, commands) ->
            guild.updateCommands().addCommands(commands.map { it.jdaCmdData }).queue(
                { libFlow.success("Guild:${guild.name} への登録に成功しました。(${commands.size})") },
                { libFlow.failure("Guild:${guild.name} への登録に失敗しました。(${it.message})") }
            )
        }
    }
}
