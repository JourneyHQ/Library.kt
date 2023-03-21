package dev.yuua.journeylib.qnortz.functions.command.builder.function

import dev.yuua.journeylib.qnortz.functions.command.CommandMethodType
import dev.yuua.journeylib.qnortz.functions.command.builder.AutocompleteFunction
import dev.yuua.journeylib.qnortz.functions.command.builder.UnifiedCommandFilter
import dev.yuua.journeylib.qnortz.functions.command.event.UnifiedCommandInteractionEvent
import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.interactions.commands.build.OptionData

/**
 * Data class which holds data of the instance of the command.
 *
 * @param slashFunction [SlashFunction] to execute.
 * @param textFunction [TextFunction] to execute.
 * @param options List of [OptionData].
 * @param filters [UnifiedCommandFilter] to allow or deny command execution.
 *
 * @throws IllegalArgumentException When both [slashFunction] and [textFunction] are provided at the same time.
 */
data class CommandFunction(
    val slashFunction: SlashFunction?,
    val textFunction: TextFunction?,
    val options: List<OptionData>,
    val autocompletes: HashMap<String, AutocompleteFunction>,
    val filters: List<UnifiedCommandFilter>
) {
    private val illegalArgs = IllegalArgumentException("One of them must be null and the other must be not null!")

    private val slashIsNull = slashFunction == null
    private val textIsNull = textFunction == null

    val type = when {
        !slashIsNull -> CommandMethodType.SlashCommand
        !textIsNull -> CommandMethodType.TextCommand
        else -> throw illegalArgs
    }

    init {
        if (!(slashIsNull xor textIsNull))
            throw illegalArgs
    }

    fun checkChannelType(channelType: ChannelType): Pair<Boolean, MutableList<ChannelType>> {
        val acceptedOn = mutableListOf<ChannelType>()
        filters.map { filter -> filter.channelTypes }
            .forEach { channelTypes -> acceptedOn.addAll(channelTypes) }

        return !(acceptedOn.isNotEmpty() && acceptedOn.contains(channelType)) to acceptedOn
    }

    fun checkFilter(event: UnifiedCommandInteractionEvent) = filters.map {
        it.check(event)
    }.flatten().distinct()
}
