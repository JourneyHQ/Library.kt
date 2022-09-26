package dev.yuua.journeylib.qnortz.functions.command.builder.function

import dev.yuua.journeylib.qnortz.functions.command.CommandFromType
import dev.yuua.journeylib.qnortz.functions.command.event.UnifiedCommandInteractionEvent
import dev.yuua.journeylib.qnortz.rules.RulesFunction
import dev.yuua.journeylib.qnortz.rules.RulesResultType.Passed
import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.interactions.commands.build.OptionData

/**
 * Data class which holds data of the commands' instance.
 *
 * @param slashFunction [SlashFunction] to execute. Cannot hold with [textFunction].
 * @param textFunction [TextFunction] to execute. Cannot hold with [slashFunction].
 * @param options List of [OptionData].
 * @param rules List of [RulesFunction]. All results must be [Passed] to execute the command.
 * @param acceptedOn List of [ChannelType] on which this command can be executed.
 */
data class CommandFunction(
    val slashFunction: SlashFunction?,
    val textFunction: TextFunction?,
    val options: List<OptionData>,
    val rules: List<RulesFunction<UnifiedCommandInteractionEvent>>,
    val acceptedOn: List<ChannelType>
) {
    private val illegalArgs = IllegalArgumentException("One of them must be null and the other must be not null!")

    val packageName: String

    val type = when {
        slashFunction != null -> CommandFromType.SlashCommand
        textFunction != null -> CommandFromType.TextCommand
        else -> throw illegalArgs
    }

    init {
        if ((slashFunction != null && textFunction != null) || (slashFunction == null && textFunction == null))
            throw illegalArgs
        packageName = when (type) {
            CommandFromType.TextCommand -> textFunction!!::class.java.packageName
            CommandFromType.SlashCommand -> slashFunction!!::class.java.packageName
        }
    }
}
