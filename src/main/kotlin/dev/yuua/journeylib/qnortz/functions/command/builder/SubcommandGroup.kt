package dev.yuua.journeylib.qnortz.functions.command.builder

import dev.minn.jda.ktx.interactions.commands.Command
import dev.yuua.journeylib.qnortz.rules.RulesFunction
import dev.yuua.journeylib.qnortz.rules.RulesResult
import dev.yuua.journeylib.qnortz.functions.command.event.CommandInteraction
import dev.yuua.journeylib.qnortz.functions.command.event.UnifiedCommandInteractionEvent
import dev.yuua.journeylib.qnortz.rules.RulesResultType

class SubcommandGroup {
    val name: String
    val description: String

    /**
     * Creates [SubcommandGroup] instance. (for Java)
     * @param name The name of the subcommand group.
     * @param description The description of the subcommand group.
     */
    constructor(name: String, description: String) {
        this.name = name
        this.description = description
    }

    /**
     * Creates [SubcommandGroup] instance. (for Kotlin)
     *
     * @param name The name of the subcommand group.
     * @param description The description of the subcommand group.
     * @param script The script which is applied to [SubcommandGroup].
     */
    constructor(name: String, description: String, script: SubcommandGroup.() -> Unit) {
        this.name = name
        this.description = description
        script()
    }

    val subcommands = mutableListOf<Subcommand>()

    var rulesFunction: RulesFunction<UnifiedCommandInteractionEvent>? = null

    /**
     * Adds subcommands to the subcommand group. (for Java)
     *
     * @param commands Subcommands to add.
     */
    fun subcommands(vararg commands: Subcommand): SubcommandGroup {
        subcommands.addAll(commands)
        return this
    }

    /**
     * Adds the subcommand to the subcommand group. (for Kotlin)
     *
     * @param name The name of the subcommand.
     * @param description The description of the subcommand.
     * @param script The script which is applied to [Subcommand].
     */
    fun subcommand(name: String, description: String, script: Subcommand.() -> Unit) {
        subcommands.add(Subcommand(name, description, script))
    }

    /**
     * Adds the rules function to the subcommand group. (for Kotlin)
     * To use the command, [RulesResultType] must be [RulesResultType.Passed]
     *
     * @param script The script to control access.
     */
    fun rules(script: UnifiedCommandInteractionEvent.() -> RulesResult) {
        rulesFunction = RulesFunction {
            script(it)
        }
    }

    /**
     * Adds the rules function to the subcommand group. (for Java)
     * To use the command, [RulesResultType] must be [RulesResultType.Passed]
     *
     * @param script The script to control access.
     */
    fun rules(script: RulesFunction<UnifiedCommandInteractionEvent>): SubcommandGroup {
        rulesFunction = script
        return this
    }
}
