package dev.yuua.journeylib.discord.framework.command.router

import dev.yuua.journeylib.discord.framework.command.builder.option.FrOptionLib
import dev.yuua.journeylib.discord.framework.command.router.FrCmdTypeEnum.*

object FrTextAnalyzer {
    fun analyze(prefix: String, text: String): Pair<FrCmdRouteRecord, FrOptionLib.OptionAnalysisResult?> {
        if (!text.startsWith(text))
            throw IllegalArgumentException("Command not starts with prefix!")

        val commandText = text.substring(prefix.length)
        val args = commandText.split(" ")

        val struct = FrCmdRouteDB.findStruct(args[0])

        val cmd = args[0]
        var subcmdGroup: String? = null
        var subcmd: String? = null

        var option: String? = null

        when (struct) {
            Cmd -> {
                if (commandText.length > cmd.length)
                    option = commandText.substring("$cmd ".length)
            }
            Cmd_Subcmd -> {
                subcmd = args[1]
                if (commandText.length > "$cmd $subcmd".length)
                    option = commandText.substring("$cmd $subcmd ".length)
            }
            Cmd_SubcmdGroup -> {
                subcmdGroup = args[1]
                subcmd = args[2]
                if (commandText.length > "$cmd $subcmdGroup $subcmd".length)
                    option = commandText.substring("$cmd $subcmdGroup $subcmd ".length)
            }
        }
        val routeRecord = FrCmdRouteDB.find(cmd, subcmdGroup, subcmd)
        val options = FrOptionLib.analyze(option ?: "", routeRecord.options)

        return FrCmdRouteDB.find(cmd, subcmdGroup, subcmd) to options
    }
}
