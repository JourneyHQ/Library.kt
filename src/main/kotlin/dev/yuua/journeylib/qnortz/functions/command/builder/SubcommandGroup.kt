package dev.yuua.journeylib.qnortz.functions.command.builder

class SubcommandGroup : CommandBase<SubcommandGroup> {
    /**
     * Creates [SubcommandGroup] instance. (for Java)
     * @param name The name of the subcommand-group.
     * @param description The description of the subcommand-group.
     */
    constructor(name: String, description: String) : super(name, description)

    /**
     * Creates [SubcommandGroup] instance. (for Kotlin)
     *
     * @param name The name of the subcommand-group.
     * @param description The description of the subcommand-group.
     * @param script The script which is applied to [SubcommandGroup].
     */
    constructor(name: String, description: String, script: SubcommandGroup.() -> Unit) : super(name, description) {
        script()
    }

    val subcommands = mutableListOf<Subcommand>()

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
}
