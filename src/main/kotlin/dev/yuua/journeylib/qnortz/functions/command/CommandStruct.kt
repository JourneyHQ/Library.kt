package dev.yuua.journeylib.qnortz.functions.command

import dev.yuua.journeylib.qnortz.functions.FunctionStruct
import dev.yuua.journeylib.qnortz.functions.command.builder.CommandObject

/**
 * Interface for command class.
 */
interface CommandStruct : FunctionStruct {
    val command: CommandObject

    fun asDevCommand() {
    }
}
