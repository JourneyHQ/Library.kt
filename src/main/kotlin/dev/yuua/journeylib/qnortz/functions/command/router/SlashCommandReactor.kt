package dev.yuua.journeylib.qnortz.functions.command.router

import dev.minn.jda.ktx.events.listener
import dev.yuua.journeylib.qnortz.functions.command.CommandManager
import dev.yuua.journeylib.qnortz.functions.command.CommandMethodType.SlashCommand
import dev.yuua.journeylib.qnortz.functions.command.CommandMethodType.TextCommand
import dev.yuua.journeylib.qnortz.functions.command.event.toUnified
import dev.yuua.journeylib.qnortz.functions.event.EventStruct
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

/**
 * The router of slash command.
 *
 * @param manager [CommandManager].
 */
class SlashCommandReactor(private val manager: CommandManager) : EventStruct {
    override val script: JDA.() -> Unit = {
        listener<SlashCommandInteractionEvent> { event ->
            val commandFunction = manager.router[CommandRoute(
                event.name,
                event.subcommandGroup,
                event.subcommandName
            )]

            val unifiedEvent = event.toUnified()

            // todo Duplicated code fragment

            val filters = manager.packageFilterRouter.findAll(commandFunction.packageName)
            val passed = filters.all { it.checkEvent(unifiedEvent) }
            if (!passed) {
                it.replyEmbeds(accessForbiddenEmbed(checkResultMessage)).setEphemeral(true).queue()
                return@listener
            }

            if (!commandFunction.checkFilter(unifiedEvent)) {
                event.replyEmbeds(accessForbiddenEmbed()).setEphemeral(true).queue()
                return@listener
            }

            when (commandFunction.type) {
                TextCommand -> commandFunction.textFunction!!.execute(unifiedEvent)
                SlashCommand -> commandFunction.slashFunction!!.execute(event)
            }
        }
    }
}
