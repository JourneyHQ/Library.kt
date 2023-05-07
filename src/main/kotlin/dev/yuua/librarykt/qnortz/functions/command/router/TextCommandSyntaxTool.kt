package dev.yuua.librarykt.qnortz.functions.command.router

import dev.yuua.librarykt.qnortz.functions.command.CommandRouter
import dev.yuua.librarykt.qnortz.functions.command.CommandStructureType.*
import dev.yuua.librarykt.qnortz.functions.command.builder.function.CommandFunction
import dev.yuua.librarykt.qnortz.functions.command.builder.option.OptionAnalysisResult
import dev.yuua.librarykt.qnortz.functions.command.builder.option.analyzeOptions

/**
 * Analyzes string command.
 *
 * @param prefix Prefix.
 * @param commandString String of the command. ex: ";ping yuua"
 * @param router [CommandRouter].
 *
 * @return Pair of [CommandFunction] and [OptionAnalysisResult].
 */
fun analyzeTextCommand(
    prefix: String = ";",
    commandString: String,
    router: CommandRouter
): TextCommandAnalysisResult {
    // todo prefix config
    if (!commandString.startsWith(prefix))
        throw IllegalArgumentException("Command not starts with prefix!")

    val commandRaw = commandString.substring(prefix.length).trim()
    val args = commandRaw.split(" ")

    val command = args[0]
    var subcommandGroup: String? = null
    var subcommand: String? = null

    var optionRaw: String? = null

    // Determine command structure type and extract options from command raw
    when (router.inferType(command)) {
        CommandType -> {
            if (commandRaw.length > command.length)
                optionRaw = commandRaw.substring("$command ".length)
        }

        SubcommandType -> {
            subcommand = args[1]

            val commandStructureRaw = "$command $subcommand"
            if (commandRaw.length > commandStructureRaw.length)
                optionRaw = commandRaw.substring("$commandRaw ".length)
        }

        SubcommandGroupType -> {
            subcommandGroup = args[1]
            subcommand = args[2]

            val commandStructureRaw = "$command $subcommandGroup $subcommand"
            if (commandRaw.length > commandStructureRaw.length)
                optionRaw = commandRaw.substring("$commandStructureRaw ".length)
        }
    }

    val commandRoute = CommandRoute(command, subcommandGroup, subcommand)

    val commandFunction = router[commandRoute]

    val options = analyzeOptions(optionRaw ?: "", commandFunction.options)

    return TextCommandAnalysisResult(commandRoute, commandFunction, options)
}

data class TextCommandAnalysisResult(
    val commandRoute: CommandRoute,
    val commandFunction: CommandFunction,
    val optionAnalysisResult: OptionAnalysisResult
)