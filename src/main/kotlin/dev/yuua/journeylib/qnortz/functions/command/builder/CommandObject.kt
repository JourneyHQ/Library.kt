package dev.yuua.journeylib.qnortz.functions.command.builder

import dev.yuua.journeylib.qnortz.functions.command.router.CommandRoute
import dev.yuua.journeylib.qnortz.functions.command.builder.function.CommandFunction
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData

/**
 * The static data of the [Command]
 *
 * @param routes The routing data of the command.
 * @param commandData [SlashCommandData] to register to the command.
 */
data class CommandObject(
    val routes: HashMap<CommandRoute, CommandFunction>,
    val commandData: SlashCommandData
)
