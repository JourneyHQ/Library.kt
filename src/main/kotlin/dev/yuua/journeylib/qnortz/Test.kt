package dev.yuua.journeylib.qnortz

import dev.minn.jda.ktx.Embed
import net.dv8tion.jda.api.requests.GatewayIntent

class Test {
    fun test() {
        Qnortz {
            name = ""
            token = ""
            intents.addAll(listOf(
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.DIRECT_MESSAGES
            ))
        }
    }
}
