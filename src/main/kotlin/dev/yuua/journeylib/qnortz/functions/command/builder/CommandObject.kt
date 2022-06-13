package dev.yuua.journeylib.qnortz.functions.command.builder

import dev.yuua.journeylib.qnortz.functions.command.router.CommandRoute
import dev.yuua.journeylib.qnortz.functions.command.builder.function.CommandFunction
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData

data class CommandObject(
    val routes: HashMap<CommandRoute, CommandFunction>,
    val commandData: SlashCommandData
)
