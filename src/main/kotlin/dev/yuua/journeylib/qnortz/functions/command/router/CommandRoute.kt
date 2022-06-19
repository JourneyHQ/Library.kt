package dev.yuua.journeylib.qnortz.functions.command.router

import dev.yuua.journeylib.qnortz.functions.command.CommandStructureType

/**
 * The data class which stores the routing data of the command.
 *
 * @param command The name of the command.
 * @param subcommandGroup The name of the subcommand group.
 * @param subcommand The name of the subcommand.
 *
 * @throws IllegalArgumentException If type of the command is unknown.
 */
data class CommandRoute(var command: String, val subcommandGroup: String?, val subcommand: String?) {
    var type: CommandStructureType = when {
        subcommandGroup == null && subcommand == null -> CommandStructureType.CommandType
        subcommandGroup == null && subcommand != null -> CommandStructureType.SubcommandType
        subcommandGroup != null && subcommand != null -> CommandStructureType.SubcommandGroupType
        else -> throw IllegalArgumentException("Unknown Command Structure Type!")
    }
}
