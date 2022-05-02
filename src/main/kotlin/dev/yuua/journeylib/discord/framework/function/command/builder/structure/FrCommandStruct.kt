package dev.yuua.journeylib.discord.framework.function.command.builder.structure

import dev.yuua.journeylib.discord.framework.function.FrFunctionStruct

interface FrCommandStruct : FrFunctionStruct {
    fun cmd(): FrCmdBuild
}
