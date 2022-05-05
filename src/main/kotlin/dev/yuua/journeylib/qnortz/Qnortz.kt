package dev.yuua.journeylib.qnortz

import dev.yuua.journeylib.universal.LibFlow
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.requests.GatewayIntent

class Qnortz {
    private val libFlow = LibFlow(this.javaClass.simpleName)

    val jda: JDA
    lateinit var name: String
    lateinit var token: String
    val intents = mutableListOf<GatewayIntent>()

    constructor(name: String, token: String, vararg intents: GatewayIntent) {
        this.name = name
        this.token = token
        this.intents.addAll(intents)
    }

    constructor(script: Qnortz.() -> Unit) {
        script(this)

        fun required(name: String) = IllegalArgumentException("$name is required.")

        if (!::name.isInitialized)
            throw required("Name")
        if (!::token.isInitialized)
            throw required("Token")
    }

    fun build(script: Qnortz.() -> Unit) {

    }
}
