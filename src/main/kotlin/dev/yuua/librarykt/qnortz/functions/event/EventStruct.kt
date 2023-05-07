package dev.yuua.librarykt.qnortz.functions.event

import dev.yuua.librarykt.qnortz.functions.FunctionStruct
import net.dv8tion.jda.api.JDA

interface EventStruct : FunctionStruct {
    val script: JDA.() -> Unit
}
