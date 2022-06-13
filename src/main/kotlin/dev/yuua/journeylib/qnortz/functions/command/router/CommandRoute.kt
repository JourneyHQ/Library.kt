package dev.yuua.journeylib.qnortz.functions.command.router

import dev.yuua.journeylib.qnortz.functions.command.CommandStructureType

data class CommandRoute(val command: String, val subcommandGroup: String?, val subcommand: String?) {

    var type: CommandStructureType = when {
        subcommandGroup == null && subcommand == null -> CommandStructureType.CommandType
        subcommandGroup == null && subcommand != null -> CommandStructureType.SubcommandType
        subcommandGroup != null && subcommand != null -> CommandStructureType.SubcommandGroupType
        else -> throw IllegalArgumentException("Unknown Command Structure Type!")
    }
}
