package dev.yuua.journeylib.discord.framework.function.event

import dev.yuua.journeylib.discord.framework.function.FrFunctionStruct
import net.dv8tion.jda.api.JDA

interface FrEventStruct : FrFunctionStruct {
    fun listener(jda: JDA): Any
}
