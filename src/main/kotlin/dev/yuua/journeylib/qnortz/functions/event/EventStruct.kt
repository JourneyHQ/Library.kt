package dev.yuua.journeylib.qnortz.functions.event

import dev.yuua.journeylib.qnortz.functions.FunctionStruct
import net.dv8tion.jda.api.JDA

interface EventStruct : FunctionStruct {
    val script: JDA.() -> Unit
}
