package dev.yuua.librarykt.qnortz.functions.command.builder

class Subcommand : CommandBaseWithFunction<Subcommand> {
    /**
     * Creates [Subcommand] instance. (for Java)
     * @param name The name of the subcommand.
     * @param description The description of the subcommand.
     */
    constructor(name: String, description: String) : super(name, description)

    /**
     * Creates [Subcommand] instance. (for Kotlin)
     *
     * @param name The name of the subcommand.
     * @param description The description of the subcommand.
     * @param script The script which is applied to [Subcommand].
     */
    constructor(name: String, description: String, script: Subcommand.() -> Unit) : super(name, description) {
        script()
    }
}
