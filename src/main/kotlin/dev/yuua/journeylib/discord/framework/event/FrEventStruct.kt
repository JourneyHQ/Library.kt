package dev.yuua.journeylib.discord.framework.event

import dev.minn.jda.ktx.CoroutineEventListener
import net.dv8tion.jda.api.JDA

interface FrEventStruct {
    fun listener(jda: JDA): CoroutineEventListener
}
