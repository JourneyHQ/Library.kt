package dev.yuua.journeylib.qnortz.functions.command

import dev.yuua.journeylib.qnortz.functions.FunctionStruct
import dev.yuua.journeylib.qnortz.functions.command.builder.CommandObject

interface CommandStruct : FunctionStruct {
    val command: CommandObject
}
