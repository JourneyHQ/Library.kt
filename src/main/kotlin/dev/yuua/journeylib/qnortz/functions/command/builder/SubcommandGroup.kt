package dev.yuua.journeylib.qnortz.functions.command.builder

import dev.yuua.journeylib.qnortz.rules.RulesFunction
import dev.yuua.journeylib.qnortz.rules.RulesResult
import dev.yuua.journeylib.qnortz.functions.command.event.CommandInteraction
import dev.yuua.journeylib.qnortz.functions.command.event.UnifiedCommandInteractionEvent

class SubcommandGroup {
    val name: String
    val description: String

    constructor(name: String, description: String) {
        this.name = name
        this.description = description
    }

    constructor(name: String, description: String, script: SubcommandGroup.() -> Unit) {
        this.name = name
        this.description = description
        script()
    }

    val subcommands = mutableListOf<Subcommand>()

    var rulesFunction: RulesFunction<UnifiedCommandInteractionEvent>? = null

    // Java
    fun subcommands(vararg commands: Subcommand): SubcommandGroup {
        subcommands.addAll(commands)
        return this
    }

    // Kotlin DSL
    fun subcommand(name: String, description: String, script: Subcommand.() -> Unit) {
        subcommands.add(Subcommand(name, description, script))
    }

    // Kotlin DSL
    fun rules(script: UnifiedCommandInteractionEvent.() -> RulesResult) {
        rulesFunction = RulesFunction {
            script(it)
        }
    }

    // Java
    fun rules(script: RulesFunction<UnifiedCommandInteractionEvent>): SubcommandGroup {
        rulesFunction = script
        return this
    }
}
