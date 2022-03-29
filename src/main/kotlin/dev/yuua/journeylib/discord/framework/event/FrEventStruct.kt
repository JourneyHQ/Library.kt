package dev.yuua.journeylib.discord.framework.event

import net.dv8tion.jda.api.JDA

interface FrEventStruct {
    fun listener(jda: JDA): Any
}
