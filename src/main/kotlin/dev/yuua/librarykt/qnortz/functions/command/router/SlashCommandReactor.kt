package dev.yuua.librarykt.qnortz.functions.command.router

import dev.minn.jda.ktx.events.listener
import dev.yuua.librarykt.qnortz.functions.command.CommandManager
import dev.yuua.librarykt.qnortz.functions.command.CommandMethodType.SlashCommand
import dev.yuua.librarykt.qnortz.functions.command.CommandMethodType.TextCommand
import dev.yuua.librarykt.qnortz.functions.command.event.toUnified
import dev.yuua.librarykt.qnortz.functions.event.EventStruct
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
            val commandRoute = CommandRoute(
                event.name,
                event.subcommandGroup,
                event.subcommandName
            )
            val commandFunction = manager.router[commandRoute]

            val unifiedEvent = event.toUnified()

            // todo Duplicated code fragment ↓

            // check filter
            val filters = manager.packageFilterRouter.findAll(manager.findRoutePackage(commandRoute))
            val packageFilterMessages = filters.map { it.check(unifiedEvent) }.flatten().distinct()
            val commandFilterMessages = commandFunction.checkFilter(unifiedEvent)
            val flattenedMessages = (packageFilterMessages + commandFilterMessages).joinToString("\n") { "* $it" }

            if (packageFilterMessages.isNotEmpty() || commandFilterMessages.isNotEmpty()) {
                event.replyEmbeds(accessForbiddenEmbed(flattenedMessages)).setEphemeral(true).queue()
                return@listener
            }

            when (commandFunction.type) {
                TextCommand -> commandFunction.textFunction!!.execute(unifiedEvent)
                SlashCommand -> commandFunction.slashFunction!!.execute(event)
            }
        }
    }
}
