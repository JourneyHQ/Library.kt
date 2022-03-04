package dev.yuua.journeylib.discord.framework

import org.reflections.Reflections
import dev.yuua.journeylib.discord.framework.command.CmdSubstrate

object Test {
    val something: Unit
        get() {
            Reflections("powa").getSubTypesOf(CmdSubstrate::class.java)
        }
}