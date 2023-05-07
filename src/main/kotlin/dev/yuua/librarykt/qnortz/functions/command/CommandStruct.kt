package dev.yuua.librarykt.qnortz.functions.command

import dev.yuua.librarykt.qnortz.functions.FunctionStruct
import dev.yuua.librarykt.qnortz.functions.command.builder.CommandObject

/**
 * Interface for command class.
 */
interface CommandStruct : FunctionStruct {
    val command: CommandObject
}
