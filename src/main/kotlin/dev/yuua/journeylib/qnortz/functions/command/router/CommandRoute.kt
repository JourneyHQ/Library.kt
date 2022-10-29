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
    private val subcommandGroupIsNull = subcommandGroup == null
    private val subcommandIsNull = subcommand == null

    var type: CommandStructureType = when {
        subcommandGroupIsNull && subcommandIsNull -> CommandStructureType.CommandType
        subcommandGroupIsNull && !subcommandIsNull -> CommandStructureType.SubcommandType
        !subcommandGroupIsNull && !subcommandIsNull -> CommandStructureType.SubcommandGroupType
        else -> throw IllegalArgumentException("Unknown Command Structure Type!")
    }
}
